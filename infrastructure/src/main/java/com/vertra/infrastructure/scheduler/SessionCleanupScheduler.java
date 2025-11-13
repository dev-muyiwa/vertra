package com.vertra.infrastructure.scheduler;

import com.vertra.application.port.out.persistence.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionCleanupScheduler {

    private final UserSessionRepository sessionRepo;

    @Scheduled(cron = "${scheduler.session-cleanup.cron:0 0 2 * * ?}")
    public void cleanupExpiredSessions() {
        log.info("Starting expired session cleanup job");

        try {
            sessionRepo.deleteExpiredSessions();
            log.info("Expired session cleanup completed successfully");
        } catch (Exception e) {
            log.error("Expired session cleanup failed", e);
        }
    }
}
