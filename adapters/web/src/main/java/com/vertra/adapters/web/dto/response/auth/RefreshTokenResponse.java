package com.vertra.adapters.web.dto.response.auth;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        int expiresIn
) {
}
