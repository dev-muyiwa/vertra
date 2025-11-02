package com.vertra.domain.model.audit;

import com.vertra.domain.vo.Inet6;
import com.vertra.domain.vo.Uuid;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Builder(toBuilder = true)
@Value
public class AuditLog {
    Uuid id;

    Uuid orgId;
    Uuid actorMemberId;
    Uuid actorServiceTokenId;

    ActorType actorType;
    AuditAction action;

    Uuid projectId;
    Uuid configId;
    Uuid secretId;

    Inet6 ip;
    String userAgent;
    String requestId;
    Map<String, Object> metadata;
    Boolean success;
    String errorMessage;

    Instant createdAt;
}