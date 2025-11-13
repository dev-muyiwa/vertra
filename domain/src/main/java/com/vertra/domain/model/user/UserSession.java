package com.vertra.domain.model.user;

import com.vertra.domain.vo.HashedToken;
import com.vertra.domain.vo.Inet6;
import com.vertra.domain.vo.PublicKeyFingerPrint;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder(toBuilder = true)
public record UserSession(
        UUID id,
        UUID userId,

        HashedToken sessionTokenHash,
        HashedToken refreshTokenHash,

        Inet6 ipAddress,
        String userAgent,

        Instant createdAt,
        Instant expiresAt,
        Instant revokedAt
) {
    public boolean isActive() {
        return revokedAt == null && expiresAt.isAfter(Instant.now());
    }
}
