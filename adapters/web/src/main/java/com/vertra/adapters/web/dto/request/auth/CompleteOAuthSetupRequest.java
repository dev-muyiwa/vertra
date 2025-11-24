package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record CompleteOAuthSetupRequest(
        @NotBlank(message = "Temporary token is required")
        String temporaryToken,

        String firstName,

        String lastName,

        String profilePictureUrl,

        @NotBlank(message = "Account public key is required")
        String accountPublicKey,

        @NotBlank(message = "Device ID is required")
        String deviceId,

        @NotBlank(message = "Device name is required")
        String deviceName,

        @NotBlank(message = "Device fingerprint is required")
        String deviceFingerprint,

        @NotBlank(message = "Encrypted private key is required")
        String encryptedPrivateKey,

        @NotBlank(message = "Recovery encrypted private key is required")
        String recoveryEncryptedPrivateKey,

        @NotBlank(message = "Recovery salt is required")
        String recoverySalt
) {}
