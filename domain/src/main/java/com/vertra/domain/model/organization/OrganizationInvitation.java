package com.vertra.domain.model.organization;

import com.vertra.domain.vo.Uuid;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class OrganizationInvitation {
    Uuid id;
    String email;
    OrganizationRole role;
    String tokenHash;

    Uuid organizationId;
    Uuid invitedBy;

    Instant createdAt;
    Instant expiresAt;
    Instant acceptedAt;
}
