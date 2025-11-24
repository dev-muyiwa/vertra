package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.RecoverDeviceUseCase;
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
public class RecoverDeviceService implements RecoverDeviceUseCase {

    private static final int ACCESS_TOKEN_EXPIRY_SECONDS = 3600;

    private final UserRepository userRepository;
    private final UserDeviceRepository deviceRepository;
    private final UserSessionRepository sessionRepository;
    private final TokenGenerationPort tokenGenerator;
    private final TokenHashingPort tokenHasher;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public RecoverDeviceResponse execute(RecoverDeviceCommand command) {
        log.info("Processing device recovery");

        command.validate();

        // Step 1: Validate and parse temporary token
        TokenGenerationPort.TemporaryTokenClaims tokenClaims = tokenGenerator.parseTemporaryToken(command.temporaryToken());
        if (!tokenClaims.valid()) {
            log.error("Invalid or expired temporary token for device recovery");
            throw UnauthorizedException.invalidToken();
        }

        String email = tokenClaims.email();
        if (email == null || email.isBlank()) {
            log.error("No email found in temporary token");
            throw UnauthorizedException.invalidToken();
        }

        // Step 2: Find user
        User user = userRepository.findUndeletedByEmail(new Email(email))
                .orElseThrow(() -> {
                    log.error("User not found for device recovery: {}", email);
                    return UnauthorizedException.invalidToken();
                });

        // Step 3: Register new device
        UserDevice device = UserDevice.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .deviceId(command.deviceId())
                .deviceName(command.deviceName())
                .deviceFingerprint(command.deviceFingerprint())
                .encryptedPrivateKey(command.encryptedPrivateKey())
                .isTrusted(false)  // New recovered device is not trusted by default
                .createdAt(Instant.now())
                .lastUsedAt(Instant.now())
                .build();

        UserDevice savedDevice = deviceRepository.save(device);

        log.info("Device registered via recovery: userId={}, deviceId={}", user.getId(), savedDevice.getDeviceId());

        // Step 4: Generate JWT tokens
        String accessToken = tokenGenerator.generateAccessToken(user.getId(), new HashMap<>());
        String refreshToken = tokenGenerator.generateRefreshToken();
        String jti = tokenGenerator.extractJti(accessToken);

        // Step 5: Create session
        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
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

        // Step 6: Update user last login
        User updatedUser = user.recordLogin();
        userRepository.save(updatedUser);

        // Step 7: Audit log
        auditPort.log(
                AuditAction.DEVICE_RECOVERY_SUCCESS,
                null,
                user.getId(),
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
                        "device_name", savedDevice.getDeviceName()
                ),
                true,
                "Device recovered successfully for user: " + user.getId()
        );

        log.info("Device recovery completed: userId={}, deviceId={}", user.getId(), savedDevice.getDeviceId());

        return new RecoverDeviceResponse(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_EXPIRY_SECONDS,
                new RecoverDeviceResponse.UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getProfilePictureUrl()
                )
        );
    }
}
