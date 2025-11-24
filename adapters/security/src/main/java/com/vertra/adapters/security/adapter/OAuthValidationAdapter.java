package com.vertra.adapters.security.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.vertra.adapters.security.config.OAuthConfig;
import com.vertra.application.port.out.oauth.OAuthValidationPort;
import com.vertra.domain.exception.UnauthorizedException;
import com.vertra.domain.model.user.OAuthProvider;
import com.vertra.domain.vo.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthValidationAdapter implements OAuthValidationPort {

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";
    private static final String GITHUB_EMAILS_URL = "https://api.github.com/user/emails";
    private static final String MICROSOFT_USER_INFO_URL = "https://graph.microsoft.com/v1.0/me";

    private final RestClient restClient;
    private final OAuthConfig oAuthConfig;

    @Override
    public OAuthUserInfo verifyAndExtractUserInfo(OAuthProvider provider, String accessToken) {
        log.debug("Verifying OAuth token for provider: {}", provider);

        if (accessToken == null || accessToken.isBlank()) {
            throw UnauthorizedException.invalidToken();
        }

        return switch (provider) {
            case GOOGLE -> verifyGoogleToken(accessToken);
            case GITHUB -> verifyGitHubToken(accessToken);
            case MICROSOFT -> verifyMicrosoftToken(accessToken);
        };
    }

    private OAuthUserInfo verifyGoogleToken(String accessToken) {
        try {
            JsonNode userInfo = restClient.get()
                    .uri(GOOGLE_USER_INFO_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);

            if (userInfo == null) {
                log.error("Failed to get Google user info: null response");
                throw UnauthorizedException.invalidToken();
            }

            String email = getJsonString(userInfo, "email");
            if (email == null) {
                log.error("No email in Google response");
                throw UnauthorizedException.invalidToken();
            }

            return OAuthUserInfo.builder()
                    .email(email)
                    .firstName(getJsonString(userInfo, "given_name"))
                    .lastName(getJsonString(userInfo, "family_name"))
                    .profilePictureUrl(getJsonString(userInfo, "picture"))
                    .provider(OAuthProvider.GOOGLE)
                    .providerId(getJsonString(userInfo, "sub"))
                    .emailVerified(getJsonBoolean(userInfo, "email_verified", false))
                    .build();

        } catch (RestClientException e) {
            log.error("Failed to verify Google token", e);
            throw UnauthorizedException.invalidToken();
        }
    }

    private OAuthUserInfo verifyGitHubToken(String accessToken) {
        try {
            // Get user profile
            JsonNode userInfo = restClient.get()
                    .uri(GITHUB_USER_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .retrieve()
                    .body(JsonNode.class);

            if (userInfo == null) {
                log.error("Failed to get GitHub user info: null response");
                throw UnauthorizedException.invalidToken();
            }

            String providerId = String.valueOf(userInfo.get("id").asLong());

            // GitHub may not return email in user endpoint, need to fetch from emails endpoint
            String email = getJsonString(userInfo, "email");
            boolean emailVerified = false;

            if (email == null) {
                // Fetch primary email from emails endpoint
                JsonNode emailsResponse = restClient.get()
                        .uri(GITHUB_EMAILS_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                        .header("X-GitHub-Api-Version", "2022-11-28")
                        .retrieve()
                        .body(JsonNode.class);

                if (emailsResponse != null && emailsResponse.isArray()) {
                    for (JsonNode emailNode : emailsResponse) {
                        if (emailNode.has("primary") && emailNode.get("primary").asBoolean()) {
                            email = getJsonString(emailNode, "email");
                            emailVerified = getJsonBoolean(emailNode, "verified", false);
                            break;
                        }
                    }
                }
            }

            if (email == null) {
                log.error("No email found in GitHub response");
                throw UnauthorizedException.invalidToken();
            }

            // Parse name into first/last name
            String name = getJsonString(userInfo, "name");
            String firstName = null;
            String lastName = null;
            if (name != null && !name.isBlank()) {
                String[] parts = name.split(" ", 2);
                firstName = parts[0];
                if (parts.length > 1) {
                    lastName = parts[1];
                }
            }

            return OAuthUserInfo.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .profilePictureUrl(getJsonString(userInfo, "avatar_url"))
                    .provider(OAuthProvider.GITHUB)
                    .providerId(providerId)
                    .emailVerified(emailVerified)
                    .build();

        } catch (RestClientException e) {
            log.error("Failed to verify GitHub token", e);
            throw UnauthorizedException.invalidToken();
        }
    }

    private OAuthUserInfo verifyMicrosoftToken(String accessToken) {
        try {
            JsonNode userInfo = restClient.get()
                    .uri(MICROSOFT_USER_INFO_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);

            if (userInfo == null) {
                log.error("Failed to get Microsoft user info: null response");
                throw UnauthorizedException.invalidToken();
            }

            // Microsoft Graph API uses 'mail' or 'userPrincipalName' for email
            String email = getJsonString(userInfo, "mail");
            if (email == null) {
                email = getJsonString(userInfo, "userPrincipalName");
            }

            if (email == null) {
                log.error("No email in Microsoft response");
                throw UnauthorizedException.invalidToken();
            }

            return OAuthUserInfo.builder()
                    .email(email)
                    .firstName(getJsonString(userInfo, "givenName"))
                    .lastName(getJsonString(userInfo, "surname"))
                    .profilePictureUrl(null) // Microsoft requires separate call for photo
                    .provider(OAuthProvider.MICROSOFT)
                    .providerId(getJsonString(userInfo, "id"))
                    .emailVerified(true) // Microsoft accounts are verified
                    .build();

        } catch (RestClientException e) {
            log.error("Failed to verify Microsoft token", e);
            throw UnauthorizedException.invalidToken();
        }
    }

    private String getJsonString(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }

    private boolean getJsonBoolean(JsonNode node, String field, boolean defaultValue) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return defaultValue;
        }
        return node.get(field).asBoolean(defaultValue);
    }
}
