package com.vertra.adapters.web.dto.response.auth;

import java.util.UUID;

public record OAuthSetupResponse(
        String accessToken,
        String refreshToken,
        int expiresIn,
        String deviceId,  // For recovery flow - device ID to update with encrypted key
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
