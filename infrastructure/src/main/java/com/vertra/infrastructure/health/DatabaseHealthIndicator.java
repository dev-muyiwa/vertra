package com.vertra.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            if (resultSet.next() && resultSet.getInt(1) == 1) {
                return Health.up()
                        .withDetail("database", "Reachable")
                        .build();
            }
            return Health.down()
                    .withDetail("database", "Unreachable")
                    .withDetail("error", "Query failed")
                    .build();

        } catch (Exception e) {
            log.error("Database health check failed", e);
            return Health.down(e)
                    .withDetail("database", "Unreachable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
