package com.vertra.domain.model.project;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Project (
    Uuid id,
    String name,

    Uuid orgId,
    Uuid createdBy,

    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
) {
    public Project rename(String newName) {
        return new Project(
                this.id,
                newName,
                this.orgId,
                this.createdBy,
                this.createdAt,
                Instant.now(),
                this.deletedAt
        );
    }
}
