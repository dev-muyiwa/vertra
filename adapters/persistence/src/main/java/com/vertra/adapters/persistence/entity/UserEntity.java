package com.vertra.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_oauth", columnList = "oauth_provider, oauth_id")
        }
)
@Builder(toBuilder = true)
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // OAuth fields
    @Column(name = "oauth_provider", length = 20)
    @Enumerated(EnumType.STRING)
    private OAuthProviderEntity oAuthProvider;

    @Column(name = "oauth_id")
    private String oAuthId;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    // Zero-knowledge encryption fields
    @Column(name = "account_public_key", columnDefinition = "TEXT")
    private String accountPublicKey;

    @Column(name = "recovery_encrypted_private_key", columnDefinition = "TEXT")
    private String recoveryEncryptedPrivateKey;

    @Column(name = "recovery_salt")
    private String recoverySalt;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "failed_login_attempts", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int failedLoginAttempts;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public enum OAuthProviderEntity {
        GOOGLE, GITHUB, MICROSOFT
    }
}