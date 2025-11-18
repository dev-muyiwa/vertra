package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StartEmailVerificationRequest(
        @NotBlank(message = "Redirect path is required")
        @Pattern(
                regexp = "^/[^:]*$",
                message = "Redirect path must start with / and cannot contain protocol (://)"
        )
        @Size(max = 500, message = "Redirect path is too long")
        String redirectPath
) {
}
