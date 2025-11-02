package com.vertra.domain.model.organization;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record OrganizationMember (
    Uuid id,
    OrganizationRole role,

    Uuid userId,
    Uuid orgId,
    Uuid invitedBy,

    Instant joinedAt,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
){}
