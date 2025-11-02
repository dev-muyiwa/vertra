package com.vertra.domain.model.organization;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Organization(
        Uuid id,
        String name,
        String slug,

        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public Organization rename(String newName, String newSlug) {
        return new Organization(
                this.id,
                newName,
                newSlug,
                this.createdAt,
                Instant.now(),
                this.deletedAt
        );
    }
}
