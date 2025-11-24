package com.vertra.adapters.web.dto.response.auth;

public record OAuthAuthorizeResponse(
        String authorizationUrl,
        String state,
        String provider
) {}
