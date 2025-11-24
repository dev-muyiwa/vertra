package com.vertra.domain.model.user;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
@With
public class UserDevice {
    UUID id;
    UUID userId;

    String deviceId;  // Unique device identifier (e.g., UUID generated on device)
    String deviceName;  // "Chrome on MacBook Pro"
    String deviceFingerprint;  // SHA-256 hash of device characteristics

    // Zero-knowledge crypto
    String encryptedPrivateKey;  // Account private key encrypted with device's DEK

    // Security
    boolean isTrusted;  // User has explicitly trusted this device

    // Audit
    Instant createdAt;
    Instant lastUsedAt;
    Instant revokedAt;

    public UserDevice recordUsage() {
        return this.withLastUsedAt(Instant.now());
    }

    public UserDevice revoke() {
        if (this.revokedAt != null) {
            throw new IllegalStateException("Device is already revoked");
        }
        return this.withRevokedAt(Instant.now());
    }

    public UserDevice trust() {
        return this.withTrusted(true);
    }

    public boolean isActive() {
        return revokedAt == null;
    }
}
