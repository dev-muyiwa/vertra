package com.vertra.domain.port.out;

import com.vertra.domain.model.audit.AuditLog;

public interface AuditLogRepository {
    void append(AuditLog log);
}
