package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.*;

public record RegisterUserRequest(
        @NotBlank @Size(min = 3, max = 30)
        String firstName,

        @NotBlank @Size(min = 3, max = 30)
        String lastName,

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, max = 30)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
        String password,

        @AssertTrue(message = "You must accept the terms and conditions")
        boolean hasAcceptedTerms
) {
}
