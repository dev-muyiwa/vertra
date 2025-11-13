package com.vertra.adapters.web.dto.response.auth;

import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
}
