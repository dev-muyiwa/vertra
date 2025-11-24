package com.vertra.domain.model.user;

import com.vertra.domain.vo.HashedToken;
import com.vertra.domain.vo.Inet6;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

@Value
@With
@Builder(toBuilder = true)
public class UserSession {
    UUID id;
    UUID userId;
    UUID deviceId;

    HashedToken sessionTokenHash;
    HashedToken refreshTokenHash;

    Inet6 ipAddress;
    String userAgent;
    String deviceFingerprint;

    Instant createdAt;
    Instant expiresAt;
    Instant lastActivityAt;
    Instant revokedAt;

    public boolean isActive() {
        return revokedAt == null && expiresAt.isAfter(Instant.now());
    }

    public UserSession revoke() {
        if (this.revokedAt != null) {
            throw new IllegalStateException("Session is already revoked");
        }
        return this.withRevokedAt(Instant.now());
    }

    public UserSession updateActivity() {
        return this.withLastActivityAt(Instant.now());
    }
}
