package com.vertra.adapters.web.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CurrentUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String profilePictureUrl,
        boolean hasOrganization,
        boolean isEmailVerified
) {
}
