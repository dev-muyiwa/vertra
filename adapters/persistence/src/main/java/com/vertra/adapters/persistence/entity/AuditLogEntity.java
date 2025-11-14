package com.vertra.adapters.persistence.entity;

import com.vertra.domain.model.audit.AuditAction;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_logs_organization", columnList = "organization_id, timestamp"),
                @Index(name = "idx_audit_logs_actor_member", columnList = "actor_member_id, timestamp"),
                @Index(name = "idx_audit_logs_resource", columnList = "resource_type, resource_id"),
                @Index(name = "idx_audit_logs_action", columnList = "action, timestamp"),
                @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp DESC")
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 100)
    private AuditAction action;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "actor_member_id")
    private UUID actorMemberId;

    @Column(name = "actor_service_token_id")
    private UUID actorServiceTokenId;

    @Column(name = "resource_type", length = 100)
    private String resourceType;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Type(JsonBinaryType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}