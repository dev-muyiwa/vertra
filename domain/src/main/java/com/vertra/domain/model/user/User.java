package com.vertra.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

@Value
@With
@Builder(toBuilder = true)
public class User {
    UUID id;
    String firstName;
    String lastName;
    String email;

    // Legacy password field - null for OAuth users
    String passwordHash;

    Instant emailVerifiedAt;

    OAuthProvider oAuthProvider;
    String oAuthId;
    String profilePictureUrl;

    // Zero-knowledge encryption fields
    String accountPublicKey;  // RSA public key (plaintext)
    String recoveryEncryptedPrivateKey;  // Private key encrypted with recovery key
    String recoverySalt;

    Instant lockedUntil;
    int failedLoginAttempts;

    Instant lastLoginAt;
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(Instant.now());
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isEmailVerified() {
        return emailVerifiedAt != null;
    }

    public boolean isOAuthUser() {
        return oAuthProvider != null;
    }

    public User markEmailAsVerified() {
        return this.withEmailVerifiedAt(Instant.now());
    }

    public User incrementFailedLoginAttempts() {
        int newAttempts = this.failedLoginAttempts + 1;

        if (newAttempts >= 5) {
            return this.withFailedLoginAttempts(newAttempts)
                    .withLockedUntil(Instant.now().plusSeconds(900)); // 15 minutes
        }

        return this.withFailedLoginAttempts(newAttempts);
    }

    public User recordLogin() {
        return this.withLastLoginAt(Instant.now())
                .withFailedLoginAttempts(0)
                .withLockedUntil(null);
    }
}