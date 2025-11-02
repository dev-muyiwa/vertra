package com.vertra.adapters.web.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken
) {
}
