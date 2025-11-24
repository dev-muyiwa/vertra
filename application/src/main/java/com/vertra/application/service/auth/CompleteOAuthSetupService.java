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

        // Step 1: Validate and parse temporary token
        TokenGenerationPort.TemporaryTokenClaims tokenClaims = tokenGenerator.parseTemporaryToken(command.temporaryToken());
        if (!tokenClaims.valid()) {
            log.error("Invalid or expired temporary token");
            throw UnauthorizedException.invalidToken();
        }

        String email = tokenClaims.email();
        if (email == null || email.isBlank()) {
            log.error("No email found in temporary token");
            throw UnauthorizedException.invalidToken();
        }

        // Step 2: Check if user already exists (shouldn't, but verify)
        if (userRepository.existsByEmail(new Email(email))) {
            log.error("User already exists during setup: {}", email);
            throw new IllegalStateException("User already exists");
        }

        // Step 3: Create user with OAuth provider info from token
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .firstName(command.firstName())
                .lastName(command.lastName())
                .oAuthProvider(tokenClaims.provider())
                .oAuthId(tokenClaims.providerId())
                .profilePictureUrl(command.profilePictureUrl())
                .accountPublicKey(command.accountPublicKey())
                .recoveryEncryptedPrivateKey(command.recoveryEncryptedPrivateKey())
                .recoverySalt(command.recoverySalt())
                .emailVerifiedAt(Instant.now()) // OAuth users are verified
                .failedLoginAttempts(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lastLoginAt(Instant.now())
                .build();

//        user.validateEmail();
//        user.validateAccountPublicKey();

        User savedUser = userRepository.save(user);

        log.info("User created: userId={}", savedUser.getId());

        // Step 4: Create device
        UserDevice device = UserDevice.builder()
                .id(UUID.randomUUID())
                .userId(savedUser.getId())
                .deviceId(command.deviceId())
                .deviceName(command.deviceName())
                .deviceFingerprint(command.deviceFingerprint())
                .encryptedPrivateKey(command.encryptedPrivateKey())
                .isTrusted(true)  // First device is trusted by default
                .createdAt(Instant.now())
                .lastUsedAt(Instant.now())
                .build();

        UserDevice savedDevice = deviceRepository.save(device);

        log.info("Device registered: deviceId={}", savedDevice.getDeviceId());

        // Step 5: Generate JWT tokens
        String accessToken = tokenGenerator.generateAccessToken(
                savedUser.getId(),
                new HashMap<>()
        );

        String refreshToken = tokenGenerator.generateRefreshToken();
        String jti = tokenGenerator.extractJti(accessToken);

        // Step 6: Create session
        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
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
                AuditAction.USER_CREATED,
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
                        "oauth_provider", savedUser.getOAuthProvider().name()
                ),
                true,
                "New user created via OAuth: " + savedUser.getId()
        );

        auditPort.log(
                AuditAction.DEVICE_REGISTERED,
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
                        "device_name", savedDevice.getDeviceName(),
                        "first_device", true
                ),
                true,
                "Device registered during OAuth setup: " + savedDevice.getDeviceId()
        );

        // TODO: Create default organization

        log.info("OAuth setup completed successfully: userId={}", savedUser.getId());

        return new CompleteOAuthSetupResponse(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_EXPIRY_SECONDS,
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
