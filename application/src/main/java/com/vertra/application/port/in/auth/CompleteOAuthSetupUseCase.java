package com.vertra.application.port.in.auth;

import java.util.UUID;

public interface CompleteOAuthSetupUseCase {
    CompleteOAuthSetupResponse execute(CompleteOAuthSetupCommand command);

    record CompleteOAuthSetupCommand(
            String temporaryToken,  // From OAuth callback response
            String firstName,
            String lastName,
            String profilePictureUrl,
            String accountPublicKey,  // RSA public key (Base64)
            String deviceId,  // Client-generated UUID
            String deviceName,  // "Chrome on MacBook Pro"
            String deviceFingerprint,
            String encryptedPrivateKey,  // Account private key encrypted with device's DEK
            String recoveryEncryptedPrivateKey,  // Account private key encrypted with recovery key
            String recoverySalt,  // Salt for deriving recovery key
            String ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (temporaryToken == null || temporaryToken.isBlank()) {
                throw new IllegalArgumentException("Temporary token is required");
            }
            if (accountPublicKey == null || accountPublicKey.isBlank()) {
                throw new IllegalArgumentException("Account public key is required");
            }
            if (deviceId == null || deviceId.isBlank()) {
                throw new IllegalArgumentException("Device ID is required");
            }
            if (deviceName == null || deviceName.isBlank()) {
                throw new IllegalArgumentException("Device name is required");
            }
            if (encryptedPrivateKey == null || encryptedPrivateKey.isBlank()) {
                throw new IllegalArgumentException("Encrypted private key is required");
            }
            if (recoveryEncryptedPrivateKey == null || recoveryEncryptedPrivateKey.isBlank()) {
                throw new IllegalArgumentException("Recovery encrypted private key is required");
            }
            if (recoverySalt == null || recoverySalt.isBlank()) {
                throw new IllegalArgumentException("Recovery salt is required");
            }
        }
    }

    record CompleteOAuthSetupResponse(
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
