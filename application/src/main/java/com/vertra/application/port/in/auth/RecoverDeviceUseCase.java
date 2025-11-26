package com.vertra.application.port.in.auth;

import java.util.UUID;

public interface RecoverDeviceUseCase {
    RecoverDeviceResponse execute(RecoverDeviceCommand command);

    record RecoverDeviceCommand(
            String temporaryToken,  // From OAuth callback recovery response
            String deviceId,  // Client-generated UUID
            String deviceName,  // "Chrome on MacBook Pro"
            String deviceFingerprint,
            String ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (temporaryToken == null || temporaryToken.isBlank()) {
                throw new IllegalArgumentException("Temporary token is required");
            }
            if (deviceId == null || deviceId.isBlank()) {
                throw new IllegalArgumentException("Device ID is required");
            }
            if (deviceName == null || deviceName.isBlank()) {
                throw new IllegalArgumentException("Device name is required");
            }
            if (deviceFingerprint == null || deviceFingerprint.isBlank()) {
                throw new IllegalArgumentException("Device fingerprint is required");
            }
        }
    }

    record RecoverDeviceResponse(
            String accessToken,
            String refreshToken,
            int expiresIn,
            String deviceId,  // Server-created device UUID (for client to know which device to update)
            String recoveryEncryptedPrivateKey,  // New recovery encrypted private key
            UserInfo user
    ) {
        public record UserInfo(
                UUID id,
                String email,
                String firstName,
                String lastName,
                String profilePictureUrl
        ) {}
    }
}
