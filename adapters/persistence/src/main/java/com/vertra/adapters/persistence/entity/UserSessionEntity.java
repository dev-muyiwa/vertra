package com.vertra.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "user_sessions",
        indexes = {
                @Index(name = "idx_user_sessions_user_id", columnList = "user_id, created_at"),
                @Index(name = "idx_user_sessions_session_token_hash", columnList = "session_token_hash"),
                @Index(name = "idx_user_sessions_refresh_token_hash", columnList = "refresh_token_hash"),
                @Index(name = "idx_user_sessions_active", columnList = "user_id, revoked_at")
        }
)
public class UserSessionEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "session_token_hash", nullable = false, unique = true)
    private String sessionTokenHash;

    @Column(name = "refresh_token_hash", unique = true)
    private String refreshTokenHash;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.lastActivityAt == null) {
            this.lastActivityAt = this.createdAt;
        }
    }
}