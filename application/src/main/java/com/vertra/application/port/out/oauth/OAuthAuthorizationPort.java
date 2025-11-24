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
}
