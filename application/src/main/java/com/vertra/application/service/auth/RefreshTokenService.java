package com.vertra.application.service.auth;

import com.vertra.application.port.in.auth.RefreshTokenUseCase;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.application.port.out.persistence.UserDeviceRepository;
import com.vertra.application.port.out.persistence.UserSessionRepository;
import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.application.port.out.security.TokenHashingPort;
import com.vertra.domain.exception.UnauthorizedException;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.model.user.UserDevice;
import com.vertra.domain.model.user.UserSession;
import com.vertra.domain.vo.HashedToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private static final int ACCESS_TOKEN_EXPIRY_SECONDS = 1200; // 20 minutes
    private static final int REFRESH_TOKEN_EXPIRY_SECONDS = 2592000; // 30 days

    private final UserSessionRepository userSessionRepo;
    private final UserDeviceRepository userDeviceRepo;
    private final TokenGenerationPort tokenGen;
    private final TokenHashingPort tokenHasher;
    private final AuditPort auditPort;

    @Override
    @Transactional
    public RefreshTokenResponse execute(RefreshTokenCommand cmd) {
        cmd.validate();

        // Hash the refresh token for lookup
        var refreshTokenHash = tokenHasher.hash(cmd.refreshToken());

        // Find session by refresh token hash
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

        // Validate device ID matches the session
        UUID requestDeviceId = UUID.fromString(cmd.deviceId());
        if (session.getDeviceId() != null && !session.getDeviceId().equals(requestDeviceId)) {
            auditPort.log(AuditAction.USER_LOGIN_FAILED, null, session.getUserId(), null, ActorType.USER,
                    null, null, null, cmd.ipAddress(), cmd.userAgent(), UUID.randomUUID(),
                    Map.of("reason", "Device ID mismatch", "expected", session.getDeviceId().toString(), "provided", cmd.deviceId()),
                    false, "Token refresh failed: device ID mismatch");
            throw new UnauthorizedException("Invalid device");
        }

        // Validate device exists and is trusted
        UserDevice device = userDeviceRepo.findByUserIdAndDeviceId(session.getUserId(), cmd.deviceId())
                .orElseThrow(() -> {
                    auditPort.log(AuditAction.USER_LOGIN_FAILED, null, session.getUserId(), null, ActorType.USER,
                            null, null, null, cmd.ipAddress(), cmd.userAgent(), UUID.randomUUID(),
                            Map.of("reason", "Device not found", "device_id", cmd.deviceId()),
                            false, "Token refresh failed: device not found");
                    return new UnauthorizedException("Device not found");
                });

        if (!device.isTrusted()) {
            auditPort.log(AuditAction.USER_LOGIN_FAILED, null, session.getUserId(), null, ActorType.USER,
                    null, null, null, cmd.ipAddress(), cmd.userAgent(), UUID.randomUUID(),
                    Map.of("reason", "Untrusted device", "device_id", cmd.deviceId()),
                    false, "Token refresh failed: untrusted device");
            throw new UnauthorizedException("Untrusted device");
        }

        if (!device.isActive()) {
            auditPort.log(AuditAction.USER_LOGIN_FAILED, null, session.getUserId(), null, ActorType.USER,
                    null, null, null, cmd.ipAddress(), cmd.userAgent(), UUID.randomUUID(),
                    Map.of("reason", "Inactive device", "device_id", cmd.deviceId()),
                    false, "Token refresh failed: inactive device");
            throw new UnauthorizedException("Inactive device");
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
                .deviceId(requestDeviceId)
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
