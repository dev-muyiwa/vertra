package com.vertra.adapters.persistence.adapter;

import com.vertra.application.port.out.security.DatabaseContextPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseContextAdapter implements DatabaseContextPort {

    private final JdbcTemplate template;


    @Override
    public void setUserContext(UUID userId) {
        try {
            String sql = String.format("SET LOCAL app.current_user_id = '%s'", userId);
            template.execute(sql);
            log.debug("Set RLS context for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to set RLS context for user: {}", userId, e);
            throw new RuntimeException("Failed to set database context", e);
        }
    }

    @Override
    public void clearUserContext() {
        try {
            template.execute("RESET app.current_user_id");
            log.debug("Cleared RLS context");
        } catch (Exception e) {
            log.warn("Failed to clear RLS context", e);
        }
    }
}