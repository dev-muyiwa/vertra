package com.vertra.application.port.in.auth;

import java.util.UUID;

public interface RecoverDeviceUseCase {
    RecoverDeviceResponse execute(RecoverDeviceCommand command);

    record RecoverDeviceCommand(
            String temporaryToken,  // From OAuth callback recovery response
            String deviceId,  // Client-generated UUID
            String deviceName,  // "Chrome on MacBook Pro"
            String deviceFingerprint,
            String encryptedPrivateKey,  // Account private key encrypted with new device's DEK
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
            if (encryptedPrivateKey == null || encryptedPrivateKey.isBlank()) {
                throw new IllegalArgumentException("Encrypted private key is required");
            }
        }
    }

    record RecoverDeviceResponse(
            String accessToken,
            String refreshToken,
            int expiresIn,
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
