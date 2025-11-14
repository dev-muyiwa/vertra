package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.LoginUserUseCase;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.application.port.out.persistence.UserSessionRepository;
import com.vertra.application.port.out.security.PasswordHashingPort;
import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.application.port.out.security.TokenHashingPort;
import com.vertra.domain.exception.UnauthorizedException;
import com.vertra.domain.model.user.User;
import com.vertra.domain.model.user.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserService implements LoginUserUseCase {

    private static final int ACCESS_TOKEN_EXPIRY_SECONDS = 1200; // 20 minutes
    private static final int REFRESH_TOKEN_EXPIRY_SECONDS = 2592000; // 30 days
    private final UserRepository userRepo;
    private final UserSessionRepository userSessionRepo;
    private final PasswordHashingPort passwordHasher;
    private final TokenGenerationPort tokenGen;
    private final TokenHashingPort tokenHasher;
//    private final AuditPort auditPort;

    @Override
    @Transactional
    public LoginUserResponse execute(LoginUserCommand cmd) {
        cmd.validate();

        User existingUser = userRepo.findUndeletedByEmail(cmd.email())
                .orElseThrow(() -> {
//                    auditPort.log(AuditAction.USER_LOGIN_FAILED, null, null, null, ActorType.USER,
//                            null, null, null, cmd.ipAddress(), cmd.userAgent(), Uuid.random(), Map.of("reason", "User not found"),
//                            false, "User login failed: user not found for email " + cmd.email().value());
                    return UnauthorizedException.invalidCredentials();
                });

        if (existingUser.isLocked()) {
//            auditPort.log(AuditAction.USER_LOGIN_FAILED, existingUser.getId(), null, null, ActorType.USER,
//                    null, null, null, cmd.ipAddress(), cmd.userAgent(), Uuid.random(), Map.of("reason", "Account locked"),
//                    false, "User login failed: account locked for email " + cmd.email().value());
            throw UnauthorizedException.accountLocked();
        }

        if (existingUser.isDeleted()) {
//            auditPort.log(AuditAction.USER_LOGIN_FAILED, existingUser.getId(), null, null, ActorType.USER,
//                    null, null, null, cmd.ipAddress(), cmd.userAgent(), Uuid.random(), Map.of("reason", "Account deleted"),
//                    false, "User login failed: account deleted for email " + cmd.email().value());
            throw UnauthorizedException.accountDeactivated();
        }

        if (!passwordHasher.verify(cmd.password(), existingUser.getPasswordHash())) {
//            auditPort.log(AuditAction.USER_LOGIN_FAILED, existingUser.getId(), null, null, ActorType.USER,
//                    null, null, null, cmd.ipAddress(), cmd.userAgent(), Uuid.random(), Map.of("reason", "Invalid credentials"),
//                    false, "User login failed: invalid credentials for email " + cmd.email().value());
            throw UnauthorizedException.invalidCredentials();
        }

        String accessToken = tokenGen.generateAccessToken(existingUser.getId(), new HashMap<>());
        String refreshToken = tokenGen.generateRefreshToken();
        String jti = tokenGen.extractJti(accessToken);

        UserSession session = UserSession.builder()
                .userId(existingUser.getId())
                .sessionTokenHash(tokenHasher.hash(jti))
                .refreshTokenHash(tokenHasher.hash(refreshToken))
                .ipAddress(cmd.ipAddress())
                .userAgent(cmd.userAgent())
                .expiresAt(Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRY_SECONDS))
                .build();

        userSessionRepo.save(session);

        existingUser.recordLogin();
        userRepo.save(existingUser);

//        auditPort.log(AuditAction.USER_LOGIN, null, existingUser.getId(), null, ActorType.USER,
//                null, null, null, cmd.ipAddress(), cmd.userAgent(), Uuid.random(), Map.<String, Object>of("session_id", session.id().toString()),
//                true, "User logged in successfully for email " + cmd.email().value());

        log.info("User logged in successfully for email {}", existingUser.getId());

        return new LoginUserResponse(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_EXPIRY_SECONDS,
                new LoginUserResponse.UserInfo(
                        existingUser.getId(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        existingUser.getEmail()
                )
        );
    }
}
