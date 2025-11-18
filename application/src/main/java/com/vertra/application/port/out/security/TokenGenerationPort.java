package com.vertra.application.port.out.security;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface TokenGenerationPort {

    String generateAccessToken(UUID userId, Map<String, Object> claims);

    String generateRefreshToken();

    String generateToken();

    TokenClaims parseToken(String token);

    String extractJti(String token);

    record TokenClaims(
            UUID userId,
            String jti,
            Instant issuedAt,
            Instant expiresAt,
            boolean valid
    ) {
    }
}
