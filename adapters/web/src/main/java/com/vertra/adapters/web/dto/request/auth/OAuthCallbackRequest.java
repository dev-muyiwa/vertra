package com.vertra.adapters.web.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OAuthCallbackRequest(
        @NotBlank(message = "Access token is required")
        String accessToken,

        String deviceId,

        @NotBlank(message = "Device name is required")
        String deviceName,

        @NotBlank(message = "Device fingerprint is required")
        String deviceFingerprint
) {}
