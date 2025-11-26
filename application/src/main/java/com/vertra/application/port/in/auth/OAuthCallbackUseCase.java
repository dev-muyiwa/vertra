package com.vertra.application.port.in.auth;

import com.vertra.domain.model.user.OAuthProvider;

import java.util.UUID;

public interface OAuthCallbackUseCase {

    OAuthCallbackResponse execute(OAuthCallbackCommand command);

    record OAuthCallbackCommand(
            String provider,
            String token,
            String deviceId,
            String deviceName,
            String deviceFingerprint,
            String ipAddress,
            String userAgent
    ) {
        public void validate() {
            if (provider == null || provider.isBlank()) {
                throw new IllegalArgumentException("OAuth provider is required");
            }
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("OAuth token is required");
            }
            if (deviceName == null || deviceName.isBlank()) {
                throw new IllegalArgumentException("Device name is required");
            }
            if (deviceFingerprint == null || deviceFingerprint.isBlank()) {
                throw new IllegalArgumentException("Device fingerprint is required");
            }
        }

        public OAuthProvider getProviderEnum() {
            return OAuthProvider.fromString(provider);
        }
    }

    sealed interface OAuthCallbackResponse {

        /**
         * New user - needs to complete setup
         */
        record NewUserResponse(
                String email,
                String firstName,
                String lastName,
                String profilePictureUrl,
                OAuthProvider provider,
                String providerId,
                String deviceId,  // Server-generated device ID
                String temporaryToken  // Short-lived token to complete setup
        ) implements OAuthCallbackResponse {}

        /**
         * Existing user with known device - login successful
         */
        record KnownDeviceResponse(
                String accessToken,
                String refreshToken,
                int expiresIn,
                UserInfo user,
                String encryptedPrivateKey  // From device
        ) implements OAuthCallbackResponse {

            public record UserInfo(
                    UUID id,
                    String email,
                    String firstName,
                    String lastName
//                    String profilePictureUrl,
//                    boolean emailVerified
            ) {}
        }

        /**
         * Existing user with new device - needs recovery code
         */
        record RecoveryRequiredResponse(
                String email,
                String firstName,
                String lastName,
                String recoverySalt,
                String temporaryToken  // Short-lived token for recovery
        ) implements OAuthCallbackResponse {}
    }
}
