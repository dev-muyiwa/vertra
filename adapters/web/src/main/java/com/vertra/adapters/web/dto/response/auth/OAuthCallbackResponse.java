package com.vertra.adapters.web.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OAuthCallbackResponse(
        String status,  // "new_user", "known_device", "recovery_required"
        String temporaryToken,  // For new_user and recovery_required
        String deviceId,  // For new_user (server-generated device ID)
        String accessToken,  // For known_device
        String refreshToken,  // For known_device
        Integer expiresIn,  // For known_device
        String encryptedPrivateKey,  // For known_device
        String recoverySalt,  // For recovery_required
        UserInfo user
) {
    public record UserInfo(
            UUID id,
            String email,
            String firstName,
            String lastName,
            String profilePictureUrl,
            String provider,
            String providerId
    ) {}

    public static OAuthCallbackResponse newUser(
            String temporaryToken,
            String deviceId,
            String email,
            String firstName,
            String lastName,
            String profilePictureUrl,
            String provider,
            String providerId
    ) {
        return new OAuthCallbackResponse(
                "new_user",
                temporaryToken,
                deviceId,
                null,
                null,
                null,
                null,
                null,
                new UserInfo(null, email, firstName, lastName, profilePictureUrl, provider, providerId)
        );
    }

    public static OAuthCallbackResponse knownDevice(
            String accessToken,
            String refreshToken,
            int expiresIn,
            String encryptedPrivateKey,
            UUID userId,
            String email,
            String firstName,
            String lastName
    ) {
        return new OAuthCallbackResponse(
                "known_device",
                null,
                null,
                accessToken,
                refreshToken,
                expiresIn,
                encryptedPrivateKey,
                null,
                new UserInfo(userId, email, firstName, lastName, null, null, null)
        );
    }

    public static OAuthCallbackResponse recoveryRequired(
            String temporaryToken,
            String recoverySalt,
            String email,
            String firstName,
            String lastName
    ) {
        return new OAuthCallbackResponse(
                "recovery_required",
                temporaryToken,
                null,
                null,
                null,
                null,
                null,
                recoverySalt,
                new UserInfo(null, email, firstName, lastName, null, null, null)
        );
    }
}
