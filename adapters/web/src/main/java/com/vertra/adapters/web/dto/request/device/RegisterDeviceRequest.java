package com.vertra.adapters.web.dto.request.device;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceRequest(
        @NotBlank(message = "Device ID is required")
        String deviceId,

        @NotBlank(message = "Encrypted private key is required")
        String encryptedPrivateKey
) {}
