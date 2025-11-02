package com.vertra.domain.model.user;

import com.vertra.domain.vo.Email;
import com.vertra.domain.vo.HashedPassword;
import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record User(
        Uuid id,
        String firstName,
        String lastName,
        Email email,
        HashedPassword passwordHash,
        boolean hasAcceptedTerms,
        Instant emailVerifiedAt,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt

) {
}
