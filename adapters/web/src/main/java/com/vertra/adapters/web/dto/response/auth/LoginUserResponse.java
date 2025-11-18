package com.vertra.adapters.web.dto.response.auth;

import java.util.UUID;

public record LoginUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        SessionToken token
) {
    public record SessionToken(
            String access,
            String refresh,
            int expiresIn
    ) {
    }
}
