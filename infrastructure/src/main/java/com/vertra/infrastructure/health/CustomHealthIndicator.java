package com.vertra.infrastructure.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Slf4j
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

            long used = heapUsage.getUsed();
            long max = heapUsage.getMax();
            double usagePercent = (double) used / max * 100;

            Health.Builder builder = Health.up()
                    .withDetail("memory.used", formatBytes(used))
                    .withDetail("memory.max", formatBytes(max))
                    .withDetail("memory.usage", String.format("%.2f%%", usagePercent));

            if (usagePercent > 90) {
                builder = Health.down()
                        .withDetail("memory.used", formatBytes(used))
                        .withDetail("memory.max", formatBytes(max))
                        .withDetail("memory.usage", String.format("%.2f%%", usagePercent))
                        .withDetail("warning", "Memory usage critically high");
            }

            return builder.build();

        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private String formatBytes(long bytes) {
        long mb = bytes / (1024 * 1024);
        return mb + " MB";
    }
}
