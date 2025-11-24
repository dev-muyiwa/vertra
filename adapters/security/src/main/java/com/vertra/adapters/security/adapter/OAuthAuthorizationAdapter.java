package com.vertra.adapters.security.adapter;

import com.vertra.adapters.security.config.OAuthConfig;
import com.vertra.application.port.out.oauth.OAuthAuthorizationPort;
import com.vertra.domain.model.user.OAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthAuthorizationAdapter implements OAuthAuthorizationPort {

    private static final int STATE_LENGTH = 32;
    private static final int STATE_EXPIRY_SECONDS = 600; // 10 minutes

    // Default authorization URLs
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String MICROSOFT_AUTH_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize";

    // Default token URLs
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String MICROSOFT_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

    // Default scopes
    private static final String GOOGLE_SCOPE = "openid email profile";
    private static final String GITHUB_SCOPE = "read:user user:email";
    private static final String MICROSOFT_SCOPE = "openid email profile User.Read";

    private final OAuthConfig oAuthConfig;
    private final RestClient oauthRestClient;
    private final SecureRandom secureRandom = new SecureRandom();

    // Store state with expiry time for validation
    private final Map<String, Instant> stateStore = new ConcurrentHashMap<>();

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
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);

        // Store state with expiry time
        stateStore.put(state, Instant.now().plusSeconds(STATE_EXPIRY_SECONDS));
        log.debug("Generated and stored OAuth state: {}", state);

        return state;
    }

    @Override
    public boolean validateState(String state) {
        if (state == null || state.isBlank()) {
            log.warn("State validation failed: state is null or blank");
            return false;
        }

        // Clean up expired states
        cleanupExpiredStates();

        Instant expiry = stateStore.remove(state);
        if (expiry == null) {
            log.warn("State validation failed: state not found in store");
            return false;
        }

        if (Instant.now().isAfter(expiry)) {
            log.warn("State validation failed: state has expired");
            return false;
        }

        log.debug("State validated successfully: {}", state);
        return true;
    }

    @Override
    public String exchangeCodeForToken(OAuthProvider provider, String code, String redirectUri) {
        OAuthConfig.ProviderConfig config = oAuthConfig.getProviderConfig(provider);
        String tokenUrl = getTokenUrl(provider, config);

        log.debug("Exchanging authorization code for access token: provider={}", provider);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", config.getClientId());
        requestBody.add("client_secret", config.getClientSecret());
        requestBody.add("code", code);
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("grant_type", "authorization_code");

        try {
            Map<String, Object> response = oauthRestClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null || !response.containsKey("access_token")) {
                throw new IllegalStateException("No access token in OAuth response");
            }

            String accessToken = (String) response.get("access_token");
            log.info("Successfully exchanged authorization code for access token: provider={}", provider);

            return accessToken;

        } catch (Exception e) {
            log.error("Failed to exchange authorization code for access token: provider={}", provider, e);
            throw new IllegalStateException("Failed to exchange authorization code: " + e.getMessage(), e);
        }
    }

    private void cleanupExpiredStates() {
        Instant now = Instant.now();
        stateStore.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
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

    private String getTokenUrl(OAuthProvider provider, OAuthConfig.ProviderConfig config) {
        if (config.getTokenUrl() != null && !config.getTokenUrl().isBlank()) {
            return config.getTokenUrl();
        }
        return switch (provider) {
            case GOOGLE -> GOOGLE_TOKEN_URL;
            case GITHUB -> GITHUB_TOKEN_URL;
            case MICROSOFT -> MICROSOFT_TOKEN_URL;
        };
    }
}
