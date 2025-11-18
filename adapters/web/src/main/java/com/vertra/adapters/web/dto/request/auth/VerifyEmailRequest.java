package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequest(
        @NotBlank(message = "Verification token is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9-_]{32,}$",
                message = "Invalid token format"
        )
        String token
) {
}
