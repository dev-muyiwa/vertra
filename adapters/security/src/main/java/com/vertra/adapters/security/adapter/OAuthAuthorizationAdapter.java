package com.vertra.adapters.security.adapter;

import com.vertra.adapters.security.config.OAuthConfig;
import com.vertra.application.port.out.oauth.OAuthAuthorizationPort;
import com.vertra.domain.model.user.OAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthAuthorizationAdapter implements OAuthAuthorizationPort {

    private static final int STATE_LENGTH = 32;

    // Default authorization URLs
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String MICROSOFT_AUTH_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize";

    // Default scopes
    private static final String GOOGLE_SCOPE = "openid email profile";
    private static final String GITHUB_SCOPE = "read:user user:email";
    private static final String MICROSOFT_SCOPE = "openid email profile User.Read";

    private final OAuthConfig oAuthConfig;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateAuthorizationUrl(OAuthProvider provider, String state, String redirectUri) {
        OAuthConfig.ProviderConfig config = oAuthConfig.getProviderConfig(provider);

        String authUrl = getAuthorizationUrl(provider, config);
        String scope = getScope(provider, config);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(authUrl)
                .queryParam("client_id", config.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state)
                .queryParam("scope", scope);

        // Provider-specific parameters
        switch (provider) {
            case GOOGLE -> builder
                    .queryParam("access_type", "offline")
                    .queryParam("prompt", "consent");
            case MICROSOFT -> builder
                    .queryParam("response_mode", "query");
            case GITHUB -> {} // No additional params needed
        }

        String url = builder.build().toUriString();
        log.debug("Generated OAuth authorization URL for provider {}: {}", provider, url);

        return url;
    }

    @Override
    public String generateState() {
        byte[] stateBytes = new byte[STATE_LENGTH];
        secureRandom.nextBytes(stateBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);
    }

    private String getAuthorizationUrl(OAuthProvider provider, OAuthConfig.ProviderConfig config) {
        if (config.getAuthorizationUrl() != null && !config.getAuthorizationUrl().isBlank()) {
            return config.getAuthorizationUrl();
        }
        return switch (provider) {
            case GOOGLE -> GOOGLE_AUTH_URL;
            case GITHUB -> GITHUB_AUTH_URL;
            case MICROSOFT -> MICROSOFT_AUTH_URL;
        };
    }

    private String getScope(OAuthProvider provider, OAuthConfig.ProviderConfig config) {
        if (config.getScope() != null && !config.getScope().isBlank()) {
            return config.getScope();
        }
        return switch (provider) {
            case GOOGLE -> GOOGLE_SCOPE;
            case GITHUB -> GITHUB_SCOPE;
            case MICROSOFT -> MICROSOFT_SCOPE;
        };
    }
}
