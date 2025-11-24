package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record OAuthAuthorizeRequest(
        @NotBlank(message = "Redirect URI is required")
        String redirectUri
) {}
