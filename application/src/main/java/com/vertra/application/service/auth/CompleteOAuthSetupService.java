package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.CompleteOAuthSetupUseCase;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.application.port.out.persistence.UserDeviceRepository;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.application.port.out.persistence.UserSessionRepository;
import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.application.port.out.security.TokenHashingPort;
import com.vertra.domain.exception.UnauthorizedException;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.user.User;
import com.vertra.domain.model.user.UserDevice;
import com.vertra.domain.model.user.UserSession;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Inet6;
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
public class CompleteOAuthSetupService implements CompleteOAuthSetupUseCase {
    private static final int ACCESS_TOKEN_EXPIRY_SECONDS = 3600;
    private final UserRepository userRepository;
    private final UserDeviceRepository deviceRepository;
    private final UserSessionRepository sessionRepository;
    private final TokenGenerationPort tokenGenerator;
    private final TokenHashingPort tokenHasher;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public CompleteOAuthSetupResponse execute(CompleteOAuthSetupCommand command) {
        log.info("Completing OAuth setup for new user");

        command.validate();

        // Step 1: Validate and parse setup token
        TokenGenerationPort.SetupTokenClaims tokenClaims = tokenGenerator.parseSetupToken(command.temporaryToken());
        if (!tokenClaims.valid()) {
            log.error("Invalid or expired setup token");
            throw UnauthorizedException.invalidToken();
        }

        UUID userId = tokenClaims.userId();
        String deviceId = tokenClaims.deviceId();

        if (userId == null || deviceId == null) {
            log.error("Missing user ID or device ID in setup token");
            throw UnauthorizedException.invalidToken();
        }

        // Step 2: Get existing user (should already exist from callback)
        User user = userRepository.findUndeletedById(new com.vertra.domain.vo.Uuid(userId))
                .orElseThrow(() -> {
                    log.error("User not found during setup: {}", userId);
                    return UnauthorizedException.invalidToken();
                });

        // Step 3: Update user with cryptographic keys
        User updatedUser = user.toBuilder()
                .accountPublicKey(command.accountPublicKey())
                .recoveryEncryptedPrivateKey(command.recoveryEncryptedPrivateKey())
                .recoverySalt(command.recoverySalt())
                .updatedAt(Instant.now())
                .build();

//        updatedUser.validateEmail();
//        updatedUser.validateAccountPublicKey();

        User savedUser = userRepository.save(updatedUser);

        log.info("User keys updated: userId={}", savedUser.getId());

        // Step 4: Get existing device and update with encrypted private key
        UserDevice device = deviceRepository.findByUserIdAndDeviceId(savedUser.getId(), deviceId)
                .orElseThrow(() -> {
                    log.error("Device not found during setup: userId={}, deviceId={}", savedUser.getId(), deviceId);
                    return UnauthorizedException.invalidToken();
                });

        UserDevice updatedDevice = device.withEncryptedPrivateKey(command.encryptedPrivateKey());

        UserDevice savedDevice = deviceRepository.save(updatedDevice);

        log.info("Device updated with encrypted private key: deviceId={}", savedDevice.getDeviceId());

        // Step 5: Generate JWT tokens
        String accessToken = tokenGenerator.generateAccessToken(
                savedUser.getId(),
                new HashMap<>()
        );

        String refreshToken = tokenGenerator.generateRefreshToken();
        String jti = tokenGenerator.extractJti(accessToken);

        // Step 6: Create session
        UserSession session = UserSession.builder()
                .userId(savedUser.getId())
                .deviceId(UUID.fromString(savedDevice.getDeviceId()))
                .sessionTokenHash(tokenHasher.hash(jti))
                .refreshTokenHash(tokenHasher.hash(refreshToken))
                .ipAddress(Inet6.parse(command.ipAddress()))
                .userAgent(command.userAgent())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRY_SECONDS))
                .lastActivityAt(Instant.now())
                .build();

        sessionRepository.save(session);

        log.info("Session created: sessionId={}", session.getId());

        // Step 7: Audit log
        auditPort.log(
                AuditAction.OAUTH_LOGIN_SUCCESS,
                null,
                savedUser.getId(),
                null,
                ActorType.USER,
                null,
                null,
                null,
                Inet6.parse(command.ipAddress()),
                command.userAgent(),
                UUID.randomUUID(),
                Map.of(
                        "device_id", savedDevice.getDeviceId(),
                        "oauth_provider", savedUser.getOAuthProvider().name(),
                        "setup_completed", true
                ),
                true,
                "OAuth setup completed for user: " + savedUser.getId()
        );


        // TODO: Create default organization

        log.info("OAuth setup completed successfully: userId={}", savedUser.getId());

        return new CompleteOAuthSetupResponse(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_EXPIRY_SECONDS,
                user.getRecoveryEncryptedPrivateKey(),
                new CompleteOAuthSetupResponse.UserInfo(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getFirstName(),
                        savedUser.getLastName(),
                        ""
//                        savedUser.getProfilePictureUrl(),
//                        savedUser.isEmailVerified()
                )
        );
    }
}
