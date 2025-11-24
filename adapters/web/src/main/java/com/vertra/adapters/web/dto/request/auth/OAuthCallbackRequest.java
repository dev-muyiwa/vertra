package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record OAuthCallbackRequest(
        @NotBlank(message = "Authorization code is required")
        String code,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Redirect URI is required")
        String redirectUri,

        String deviceId,

        @NotBlank(message = "Device name is required")
        String deviceName,

        @NotBlank(message = "Device fingerprint is required")
        String deviceFingerprint
) {}
