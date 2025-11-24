package com.vertra.domain.vo;

import com.vertra.domain.model.user.OAuthProvider;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OAuthUserInfo {

    String email;
    String firstName;
    String lastName;
    String profilePictureUrl;
    OAuthProvider provider;
    String providerId;  // User ID from OAuth provider
    boolean emailVerified;

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required from OAuth provider");
        }
        if (provider == null) {
            throw new IllegalArgumentException("OAuth provider is required");
        }
        if (providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("Provider ID is required");
        }
    }
}