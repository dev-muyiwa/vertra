package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.RefreshTokenUseCase;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.application.port.out.persistence.UserSessionRepository;
import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.application.port.out.security.TokenHashingPort;
import com.vertra.domain.exception.UnauthorizedException;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.user.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private static final int ACCESS_TOKEN_EXPIRY_SECONDS = 1200; // 20 minutes
    private static final int REFRESH_TOKEN_EXPIRY_SECONDS = 2592000; // 30 days

    private final UserSessionRepository userSessionRepo;
    private final TokenGenerationPort tokenGen;
    private final TokenHashingPort tokenHasher;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public RefreshTokenResponse execute(RefreshTokenCommand cmd) {
        cmd.validate();

        var refreshTokenHash = tokenHasher.hash(cmd.refreshToken());

        UserSession session = userSessionRepo.findByRefreshTokenHash(refreshTokenHash)
                .orElseThrow(() -> {
                    auditPort.log(AuditAction.USER_LOGIN_FAILED, null, null, null, ActorType.USER,
                            null, null, null, cmd.ipAddress(), cmd.userAgent(), UUID.randomUUID(),
                            Map.of("reason", "Invalid refresh token"),
                            false, "Token refresh failed: invalid refresh token");
                    return new UnauthorizedException("Invalid refresh token");
                });

        if (!session.isActive()) {
            auditPort.log(AuditAction.USER_LOGIN_FAILED, null, session.getUserId(), null, ActorType.USER,
                    null, null, null, cmd.ipAddress(), cmd.userAgent(), UUID.randomUUID(),
                    Map.of("reason", "Session expired or revoked"),
                    false, "Token refresh failed: session expired or revoked");
            throw new UnauthorizedException("Session expired or revoked");
        }

        // Revoke the old session
        UserSession revokedSession = session.revoke();
        userSessionRepo.save(revokedSession);

        // Generate new tokens
        String newAccessToken = tokenGen.generateAccessToken(session.getUserId(), new HashMap<>());
        String newRefreshToken = tokenGen.generateRefreshToken();
        String jti = tokenGen.extractJti(newAccessToken);

        // Create new session
        UserSession newSessionObj = UserSession.builder()
                .userId(session.getUserId())
                .deviceId(session.getDeviceId())
                .sessionTokenHash(tokenHasher.hash(jti))
                .refreshTokenHash(tokenHasher.hash(newRefreshToken))
                .ipAddress(cmd.ipAddress())
                .userAgent(cmd.userAgent())
                .deviceFingerprint(session.getDeviceFingerprint())
                .expiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS))
                .build();

        UserSession newSession = userSessionRepo.save(newSessionObj);

        auditPort.log(AuditAction.USER_LOGIN, null, session.getUserId(), null, ActorType.USER,
                null, null, null, cmd.ipAddress(), cmd.userAgent(), UUID.randomUUID(),
                Map.of("session_id", newSession.getId().toString(), "action", "token_refresh"),
                true, "Token refreshed successfully for user " + session.getUserId());

        log.info("Token refreshed successfully for user {}", session.getUserId());

        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken,
                ACCESS_TOKEN_EXPIRY_SECONDS
        );
    }
}
