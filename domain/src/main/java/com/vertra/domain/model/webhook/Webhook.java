package com.vertra.domain.model.webhook;

import com.vertra.domain.vo.Uuid;

import java.time.Instant;

public record Webhook(
        Uuid id,
        String url,
        String secret,
        java.util.Set<String> events,
        boolean enabled,
        Uuid userId,
        Uuid orgId,
        Uuid projectId,
        Uuid configId,
        Uuid createdBy,
        Instant lastTriggeredAt,
        Instant createdAt,
        Instant updatedAt
) {
}
