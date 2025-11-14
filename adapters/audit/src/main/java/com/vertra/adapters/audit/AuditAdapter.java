package com.vertra.adapters.audit;

import com.vertra.adapters.persistence.entity.AuditLogEntity;
import com.vertra.adapters.persistence.repository.JpaAuditLogRepository;
import com.vertra.application.port.out.audit.AuditPort;
import com.vertra.domain.model.audit.ActorType;
import com.vertra.domain.model.audit.AuditAction;
import com.vertra.domain.vo.Inet6;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditAdapter implements AuditPort {

    private final JpaAuditLogRepository repo;


    @Override
    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(AuditAction action, UUID organizationId, UUID actorMemberId, UUID actorServiceTokenId, ActorType actorType, UUID projectId, UUID configId, UUID secretId, Inet6 ipAddress, String userAgent, UUID requestId, Map<String, Object> metadata, boolean success, String message) {
        try {
            AuditLogEntity entity = AuditLogEntity.builder()
                    .action(action)
                    .organizationId(organizationId)
                    .actorMemberId(actorMemberId)
                    .actorServiceTokenId(actorServiceTokenId)
                    .ipAddress(ipAddress.toString())
                    .userAgent(userAgent)
                    .metadata(metadata)
                    .success(success)
                    .message(message)
                    .timestamp(Instant.now())
                    .build();

            repo.saveAndFlush(entity);

            if (log.isDebugEnabled()) {
                log.debug("Audit log created: action={}, actor={}, success={}",
                        action, actorType.toString(), success);
            }

        } catch (Exception e) {
            log.error("Failed to create audit log: action={}, actor={}, error={}",
                    action, actorType.toString(), e.getMessage());
        }
    }
}
