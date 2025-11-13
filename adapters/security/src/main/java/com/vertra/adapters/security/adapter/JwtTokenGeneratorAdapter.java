package com.vertra.adapters.security.adapter;

import com.vertra.application.port.out.security.TokenGenerationPort;
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
    public String generateAccessToken(UUID userId, String email, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
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
    public TokenClaims parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new TokenClaims(
                    UUID.fromString(claims.getSubject()),
                    claims.get("email", String.class),
                    claims.getId(),
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant(),
                    true
            );
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return new TokenClaims(null, null, null, null, null, false);
        } catch (JwtException e) {
            log.error("JWT parsing failed", e);
            return new TokenClaims(null, null, null, null, null, false);
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
