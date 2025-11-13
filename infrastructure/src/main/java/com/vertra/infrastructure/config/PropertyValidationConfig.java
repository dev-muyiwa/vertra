package com.vertra.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Slf4j
@Configuration
public class PropertyValidationConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @PostConstruct
    public void validateProperties() {
        log.info("Validating application properties for profile: {}", activeProfile);

        Assert.hasText(jwtSecret, "JWT secret must be configured");
        Assert.isTrue(
                jwtSecret.length() >= 32,
                "JWT secret must be at least 32 characters long for HS256"
        );

        if ("prod".equals(activeProfile)) {
            Assert.isTrue(
                    !jwtSecret.contains("change-this"),
                    "Default JWT secret detected in production! Set JWT_SECRET environment variable"
            );
        }

        Assert.hasText(datasourceUrl, "Database URL must be configured");
        Assert.hasText(datasourceUsername, "Database username must be configured");

        log.info("Property validation completed successfully");
    }
}
