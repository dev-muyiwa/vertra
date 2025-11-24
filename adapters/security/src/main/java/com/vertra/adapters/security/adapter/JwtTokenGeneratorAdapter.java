package com.vertra.adapters.security.adapter;

import com.vertra.application.port.out.security.TokenGenerationPort;
import com.vertra.domain.model.user.OAuthProvider;
import com.vertra.domain.vo.Uuid;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenGeneratorAdapter implements TokenGenerationPort {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_PROVIDER = "provider";
    private static final String CLAIM_PROVIDER_ID = "provider_id";
    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String TOKEN_TYPE_TEMPORARY = "temporary";

    private final SecretKey secretKey;
    private final String issuer;
    private final long expirationMs;
    private final SecureRandom secureRandom;

    public JwtTokenGeneratorAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer:vertra-api}") String issuer,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expirationMs = expirationMs;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generateAccessToken(UUID userId, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .id(Uuid.random().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public String generateTemporaryToken(String email, int expirySeconds) {
        return generateTemporaryToken(email, null, null, expirySeconds);
    }

    @Override
    public String generateTemporaryToken(String email, OAuthProvider provider, String providerId, int expirySeconds) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirySeconds);

        var builder = Jwts.builder()
                .subject(email)
                .id(Uuid.random().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_TEMPORARY);

        if (provider != null) {
            builder.claim(CLAIM_PROVIDER, provider.name());
        }
        if (providerId != null) {
            builder.claim(CLAIM_PROVIDER_ID, providerId);
        }

        return builder.signWith(secretKey).compact();
    }

    @Override
    public TemporaryTokenClaims parseTemporaryToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Verify this is a temporary token
            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            if (!TOKEN_TYPE_TEMPORARY.equals(tokenType)) {
                log.warn("Token is not a temporary token");
                return new TemporaryTokenClaims(null, null, null, null, null, false);
            }

            String email = claims.get(CLAIM_EMAIL, String.class);
            String providerStr = claims.get(CLAIM_PROVIDER, String.class);
            String providerId = claims.get(CLAIM_PROVIDER_ID, String.class);

            OAuthProvider provider = null;
            if (providerStr != null) {
                try {
                    provider = OAuthProvider.valueOf(providerStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid OAuth provider in token: {}", providerStr);
                }
            }

            return new TemporaryTokenClaims(
                    email,
                    provider,
                    providerId,
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant(),
                    true
            );
        } catch (ExpiredJwtException e) {
            log.warn("Temporary token expired: {}", e.getMessage());
            return new TemporaryTokenClaims(null, null, null, null, null, false);
        } catch (JwtException e) {
            log.error("Temporary token parsing failed", e);
            return new TemporaryTokenClaims(null, null, null, null, null, false);
        }
    }

    @Override
    public TokenClaims parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new TokenClaims(
                    UUID.fromString(claims.getSubject()),
                    claims.getId(),
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant(),
                    true
            );
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return new TokenClaims(null, null, null, null, false);
        } catch (JwtException e) {
            log.error("JWT parsing failed", e);
            return new TokenClaims(null, null, null, null, false);
        }
    }

    @Override
    public String extractJti(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getId();
        } catch (JwtException e) {
            log.error("Failed to extract JTI from JWT", e);
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
