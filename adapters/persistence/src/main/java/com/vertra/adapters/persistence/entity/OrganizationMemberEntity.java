package com.vertra.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "organization_members",
        indexes = {
                @Index(name = "idx_org_members_user_id", columnList = "user_id"),
                @Index(name = "idx_org_members_org_id", columnList = "org_id"),
                @Index(name = "idx_org_members_user_org", columnList = "user_id, org_id")
        }
)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrganizationRoleEntity role;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(name = "invited_by")
    private UUID invitedBy;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public enum OrganizationRoleEntity {
        OWNER, ADMIN, DEVELOPER, VIEWER
    }
}
