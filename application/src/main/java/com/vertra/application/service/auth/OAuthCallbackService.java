package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.OAuthCallbackUseCase;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.application.port.out.oauth.OAuthValidationPort;
import com.vertra.application.port.out.persistence.UserDeviceRepository;
import com.vertra.application.port.out.persistence.UserRepository;
import com.vertra.application.port.out.persistence.UserSessionRepository;
import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.application.port.out.security.TokenHashingPort;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.user.OAuthProvider;
import com.vertra.domain.model.user.User;
import com.vertra.domain.model.user.UserDevice;
import com.vertra.domain.model.user.UserSession;
import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.Inet6;
import com.vertra.domain.vo.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthCallbackService implements OAuthCallbackUseCase {

    private static final int ACCESS_TOKEN_EXPIRY_SECONDS = 3600;  // 1 hour
    private static final int REFRESH_TOKEN_EXPIRY_SECONDS = 2592000;  // 30 days
    private static final int TEMPORARY_TOKEN_EXPIRY_SECONDS = 600;  // 10 minutes
    private final OAuthValidationPort oauthValidator;
    private final UserRepository userRepository;
    private final UserDeviceRepository deviceRepository;
    private final UserSessionRepository sessionRepository;
    private final TokenGenerationPort tokenGenerator;
    private final TokenHashingPort tokenHasher;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public OAuthCallbackResponse execute(OAuthCallbackCommand command) {
        log.info("OAuth callback: provider={}, deviceId={}", command.provider(), command.deviceId());

        command.validate();

        OAuthProvider provider = command.getProviderEnum();
        OAuthUserInfo oauthUserInfo = oauthValidator.verifyAndExtractUserInfo(provider, command.token());
        oauthUserInfo.validate();

        log.info("OAuth token verified: email={}, provider={}", oauthUserInfo.getEmail(), provider);

        // Step 2: Check if user exists
        Optional<User> existingUser = userRepository.findUndeletedByEmail(new Email(oauthUserInfo.getEmail()));

        if (existingUser.isEmpty()) {
            return handleNewUser(oauthUserInfo, command);
        }

        User user = existingUser.get();

        // Step 3: Check if device exists
        if (command.deviceId() != null) {
            Optional<UserDevice> existingDevice = deviceRepository.findByUserIdAndDeviceId(
                    user.getId(),
                    command.deviceId()
            );

            if (existingDevice.isPresent() && existingDevice.get().isActive()) {
                return handleKnownDevice(user, existingDevice.get(), command);
            }
        }

        // Step 4: New device - require recovery
        return handleNewDevice(user, command);
    }

    private OAuthCallbackResponse handleNewUser(OAuthUserInfo oauthInfo, OAuthCallbackCommand command) {
        log.info("New user registration via OAuth: email={}", oauthInfo.getEmail());

        // Create user account immediately
        User user = User.builder()
                .email(oauthInfo.getEmail())
                .firstName(oauthInfo.getFirstName())
                .lastName(oauthInfo.getLastName())
                .oAuthProvider(oauthInfo.getProvider())
                .oAuthId(oauthInfo.getProviderId())
                .profilePictureUrl(oauthInfo.getProfilePictureUrl())
                .emailVerifiedAt(Instant.now()) // OAuth users are verified
                .failedLoginAttempts(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lastLoginAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created: userId={}", savedUser.getId());

        // Create device (server-side generated device ID)
        String deviceId = UUID.randomUUID().toString();
        UserDevice device = UserDevice.builder()
                .userId(savedUser.getId())
                .deviceId(deviceId)
                .deviceName(command.deviceName())
                .deviceFingerprint(command.deviceFingerprint())
                .isTrusted(true)  // First device is trusted by default
                .createdAt(Instant.now())
                .lastUsedAt(Instant.now())
                .build();

        UserDevice savedDevice = deviceRepository.save(device);
        log.info("Device created: deviceId={}", savedDevice.getDeviceId());

        // Generate setup token for completing setup (includes user ID and device ID)
        String temporaryToken = tokenGenerator.generateSetupToken(
                savedUser.getId(),
                deviceId,
                TEMPORARY_TOKEN_EXPIRY_SECONDS
        );

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
                        "provider", oauthInfo.getProvider().name(),
                        "email", oauthInfo.getEmail(),
                        "oauth_id", oauthInfo.getProviderId()
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
                "Device registered during OAuth callback: " + savedDevice.getDeviceId()
        );

        return new OAuthCallbackResponse.NewUserResponse(
                oauthInfo.getEmail(),
                oauthInfo.getFirstName(),
                oauthInfo.getLastName(),
                oauthInfo.getProfilePictureUrl(),
                oauthInfo.getProvider(),
                oauthInfo.getProviderId(),
                deviceId,
                temporaryToken
        );
    }

    private OAuthCallbackResponse handleKnownDevice(
            User user,
            UserDevice device,
            OAuthCallbackCommand command
    ) {
        log.info("Known device login: userId={}, deviceId={}", user.getId(), device.getDeviceId());

        // Generate JWT tokens
        String accessToken = tokenGenerator.generateAccessToken(
                user.getId(),
                new HashMap<>()
        );

        String refreshToken = tokenGenerator.generateRefreshToken();
        String jti = tokenGenerator.extractJti(accessToken);

        // Create session
        UserSession session = UserSession.builder()
                .userId(user.getId())
                .deviceId(UUID.fromString(device.getDeviceId()))
                .sessionTokenHash(tokenHasher.hash(jti))
                .refreshTokenHash(tokenHasher.hash(refreshToken))
                .ipAddress(Inet6.parse(command.ipAddress()))
                .userAgent(command.userAgent())
                .deviceFingerprint(command.deviceFingerprint())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(ACCESS_TOKEN_EXPIRY_SECONDS))
                .lastActivityAt(Instant.now())
                .build();

        UserSession savedSession = sessionRepository.save(session);

        // Update device last used
        UserDevice updatedDevice = device.recordUsage();
        deviceRepository.save(updatedDevice);

        // Update user last login
        User updatedUser = user.recordLogin();
        userRepository.save(updatedUser);

        auditPort.log(
                AuditAction.OAUTH_LOGIN_SUCCESS,
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
                        "device_id", device.getDeviceId(),
                        "session_id", savedSession.getId()
                ),
                true,
                "OAuth login successful for user ID: " + user.getId()
        );

        log.info("OAuth login successful: userId={}, sessionId={}", user.getId(), savedSession.getId());

        return new OAuthCallbackResponse.KnownDeviceResponse(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_EXPIRY_SECONDS,
                new OAuthCallbackResponse.KnownDeviceResponse.UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName()
//                        user.getProfilePictureUrl(),
//                        user.isEmailVerified()
                ),
                device.getEncryptedPrivateKey()
        );
    }

    private OAuthCallbackResponse handleNewDevice(User user, OAuthCallbackCommand command) {
        log.info("New device detected: userId={}, deviceId={}", user.getId(), command.deviceId());

        // Generate temporary token for recovery process
        String temporaryToken = tokenGenerator.generateTemporaryToken(
                user.getEmail(),
                TEMPORARY_TOKEN_EXPIRY_SECONDS
        );

        auditPort.log(
                AuditAction.DEVICE_RECOVERY_INITIATED,
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
                        "device_name", command.deviceName(),
                        "device_fingerprint", command.deviceFingerprint()
                ),
                true,
                "Device recovery initiated for user ID: " + user.getId()
        );

        return new OAuthCallbackResponse.RecoveryRequiredResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRecoverySalt(),
                temporaryToken
        );
    }
}
