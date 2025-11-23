package com.vertra.domain.model.user;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Value
@With
@Builder
public class Otp {
    private static final int EXPIRY_HOURS = 24;
    UUID id;
    UUID userId;
    String token;
    Instant expiresAt;
    Instant createdAt;
    Instant usedAt;

    public static Otp create(UUID userId, String token, Duration expiryDuration) {
        Instant now = Instant.now();
        return Otp.builder()
                .userId(userId)
                .token(token)
                .expiresAt(now.plus(expiryDuration))
                .createdAt(now)
                .build();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isValid() {
        return !isExpired() && !isUsed();
    }

    public Otp markAsUsed() {
        if (!isValid()) {
            throw new IllegalStateException("Token is not valid");
        }
        return this.withUsedAt(Instant.now());
    }
}
