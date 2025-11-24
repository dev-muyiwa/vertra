package com.vertra.application.port.out.oauth;

import com.vertra.domain.model.user.OAuthProvider;

public interface OAuthAuthorizationPort {

    /**
     * Generates the authorization URL for the specified OAuth provider.
     *
     * @param provider    The OAuth provider (GOOGLE, GITHUB, MICROSOFT)
     * @param state       A random state parameter for CSRF protection
     * @param redirectUri The URI where the OAuth provider should redirect after authentication
     * @return The full authorization URL to redirect the user to
     */
    String generateAuthorizationUrl(OAuthProvider provider, String state, String redirectUri);

    /**
     * Generates a cryptographically secure state parameter.
     *
     * @return A random state string
     */
    String generateState();

    /**
     * Exchanges an authorization code for an access token.
     *
     * @param provider    The OAuth provider (GOOGLE, GITHUB, MICROSOFT)
     * @param code        The authorization code received from the OAuth provider
     * @param redirectUri The same redirect URI used in the authorization request
     * @return The access token from the OAuth provider
     */
    String exchangeCodeForToken(OAuthProvider provider, String code, String redirectUri);

    /**
     * Validates the state parameter to prevent CSRF attacks.
     *
     * @param state The state parameter received from the OAuth callback
     * @return true if the state is valid, false otherwise
     */
    boolean validateState(String state);
}
