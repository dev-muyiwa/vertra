package com.vertra.domain.model.organization;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Instant;

@Getter
@Builder
@With
public class Organization {
    private Uuid id;
    private String name;
    private String slug;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public void validateName() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Organization name cannot be empty");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Organization name too long (max 255 characters)");
        }
    }

    public void validateSlug() {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("Organization slug cannot be empty");
        }
        if (!slug.matches("^[a-z0-9-]+$")) {
            throw new IllegalArgumentException("Slug must be lowercase alphanumeric with hyphens only");
        }
        if (slug.length() > 100) {
            throw new IllegalArgumentException("Slug too long (max 100 characters)");
        }
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void markAsDeleted() {
        this.deletedAt = Instant.now();
    }
}
