package com.vertra.infrastructure.security;

import com.vertra.domain.exception.InvalidTokenException;
import com.vertra.domain.port.out.TokenGenerator;
import com.vertra.domain.vo.Uuid;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator implements TokenGenerator {
    @Value("${vertra.jwt.secret}")
    private String secret;

    @Value("${vertra.jwt.issuer:vertra}")
    private String issuer;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generateAccessToken(Uuid userId) {
        Instant now = Instant.now();
        Date exp = Date.from(now.plusSeconds(900)); // 15 minutes
        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(exp)
                .signWith(getKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(Uuid userId) {
        Instant now = Instant.now();
        Date exp = Date.from(now.plusSeconds(604800)); // 7 days
        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(exp)
                .signWith(getKey())
                .compact();
    }

    @Override
    public String generatePasswordResetToken(Uuid userId) {
        Instant now = Instant.now();
        Date exp = Date.from(now.plusSeconds(3600)); // 1 hour
        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .claim("type", "reset")
                .issuedAt(Date.from(now))
                .expiration(exp)
                .signWith(getKey())
                .compact();
    }

    @Override
    public Uuid validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Uuid.fromString(claims.getSubject());
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }
}
