package com.vertra.domain.model.config;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Config(
        Uuid id,
        String name,
        String description,

        Uuid projectId,
        Uuid parentId,
        Uuid createdBy,

        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
