package com.vertra.adapter.web.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken
) {
}
