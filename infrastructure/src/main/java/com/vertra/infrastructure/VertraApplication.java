package com.vertra.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication(scanBasePackages = {
        "com.vertra.infrastructure",
        "com.vertra.application",
        "com.vertra.domain",
        "com.vertra.adapters"
})
@EntityScan(basePackages = "com.vertra.adapters.persistence.entity")
@ConfigurationPropertiesScan
public class VertraApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(VertraApplication.class, args);
            logApplicationStartup(context.getEnvironment());
        } catch (Exception e) {
            log.error("Application failed to start", e);
            System.exit(1);
        }
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }

        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "/");
        String hostAddress = "localhost";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Unable to determine host address", e);
        }

        String[] profiles = env.getActiveProfiles();
        String activeProfiles = profiles.length == 0 ? env.getDefaultProfiles()[0] : profiles[0];

        log.info("""
                        
                        ----------------------------------------------------------
                        Application '{}' is running!
                        Access URLs:
                            Local:      {}://localhost:{}{}
                            External:   {}://{}:{}{}
                        Profile(s):     {}
                        ----------------------------------------------------------
                        """,
                env.getProperty("spring.application.name"),
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                activeProfiles
        );
    }

}
