package com.vertra.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "user_devices",
        indexes = {
                @Index(name = "idx_user_devices_user_id", columnList = "user_id"),
                @Index(name = "idx_user_devices_device_id", columnList = "user_id, device_id"),
                @Index(name = "idx_user_devices_fingerprint", columnList = "device_fingerprint")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_device", columnNames = {"user_id", "device_id"})
        }
)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "device_fingerprint", nullable = false)
    private String deviceFingerprint;

    // Zero-knowledge: account private key encrypted with device's DEK
    @Column(name = "encrypted_private_key", columnDefinition = "TEXT", nullable = false)
    private String encryptedPrivateKey;

    @Column(name = "is_trusted", nullable = false)
    private boolean isTrusted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at", nullable = false)
    private Instant lastUsedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.lastUsedAt == null) {
            this.lastUsedAt = this.createdAt;
        }
    }
}
