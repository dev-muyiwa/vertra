package com.vertra.application.port.in.device;

import java.util.UUID;

public interface RegisterDeviceUseCase {
    RegisterDeviceResponse execute(RegisterDeviceCommand command);

    record RegisterDeviceCommand(
            UUID userId,  // From JWT token
            String deviceId,  // Client-generated UUID
            String encryptedPrivateKey,  // Account private key encrypted with device's DEK
            String ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (deviceId == null || deviceId.isBlank()) {
                throw new IllegalArgumentException("Device ID is required");
            }
            if (encryptedPrivateKey == null || encryptedPrivateKey.isBlank()) {
                throw new IllegalArgumentException("Encrypted private key is required");
            }
        }
    }

    record RegisterDeviceResponse(
            boolean success,
            String message,
            String deviceId
    ) {}
}
