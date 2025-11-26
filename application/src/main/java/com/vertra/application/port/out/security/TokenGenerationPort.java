package com.vertra.application.port.out.security;

import com.vertra.domain.model.user.OAuthProvider;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface TokenGenerationPort {

    String generateAccessToken(UUID userId, Map<String, Object> claims);

    String generateRefreshToken();

    String generateToken();

    TokenClaims parseToken(String token);

    String extractJti(String token);

    /**
     * Generates a temporary token for OAuth setup flows.
     * Contains email, provider info, and expiration.
     */
    String generateTemporaryToken(String email, int expirySeconds);

    /**
     * Generates a temporary token with OAuth provider info for new user setup.
     */
    String generateTemporaryToken(String email, OAuthProvider provider, String providerId, int expirySeconds);

    /**
     * Generates a temporary token for OAuth setup with user and device IDs.
     */
    String generateSetupToken(UUID userId, String deviceId, int expirySeconds);

    /**
     * Validates and parses a temporary token.
     */
    TemporaryTokenClaims parseTemporaryToken(String token);

    /**
     * Validates and parses a setup token.
     */
    SetupTokenClaims parseSetupToken(String token);

    record TokenClaims(
            UUID userId,
            String jti,
            Instant issuedAt,
            Instant expiresAt,
            boolean valid
    ) {
    }

    record TemporaryTokenClaims(
            String email,
            OAuthProvider provider,
            String providerId,
            Instant issuedAt,
            Instant expiresAt,
            boolean valid
    ) {
    }

    record SetupTokenClaims(
            UUID userId,
            String deviceId,
            Instant issuedAt,
            Instant expiresAt,
            boolean valid
    ) {
    }
}
