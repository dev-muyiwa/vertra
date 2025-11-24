package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RecoverDeviceRequest(
        @NotBlank(message = "Temporary token is required")
        String temporaryToken,

        @NotBlank(message = "Device ID is required")
        String deviceId,

        @NotBlank(message = "Device name is required")
        String deviceName,

        @NotBlank(message = "Device fingerprint is required")
        String deviceFingerprint,

        @NotBlank(message = "Encrypted private key is required")
        String encryptedPrivateKey
) {}
