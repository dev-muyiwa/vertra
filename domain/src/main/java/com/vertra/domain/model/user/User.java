package com.vertra.domain.model.user;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private boolean hasAcceptedTerms;
    private Instant emailVerifiedAt;

    private Instant lockedUntil;
    private int failedLoginAttempts;

    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(Instant.now());
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.updatedAt = Instant.now();
    }

    public void markEmailAsVerified() {
        this.emailVerifiedAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}