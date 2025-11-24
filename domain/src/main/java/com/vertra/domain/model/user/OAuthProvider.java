package com.vertra.domain.model.user;

public enum OAuthProvider {
    GOOGLE,
    GITHUB,
    MICROSOFT;

    public static OAuthProvider fromString(String provider) {
        if (provider == null) {
            throw new IllegalArgumentException("OAuth provider cannot be null");
        }

        return switch (provider.toLowerCase()) {
            case "google" -> GOOGLE;
            case "github" -> GITHUB;
            case "microsoft" -> MICROSOFT;
            default -> throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        };
    }
}
