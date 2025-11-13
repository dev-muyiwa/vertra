package com.vertra.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomMetrics {

    private final Counter userRegistrationCounter;
    private final Counter userLoginCounter;
    private final Counter authenticationFailureCounter;

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.userRegistrationCounter = Counter.builder("vertra.user.registrations")
                .description("Number of user registrations")
                .register(meterRegistry);

        this.userLoginCounter = Counter.builder("vertra.user.logins")
                .description("Number of successful user logins")
                .register(meterRegistry);

        this.authenticationFailureCounter = Counter.builder("vertra.auth.failures")
                .description("Number of authentication failures")
                .register(meterRegistry);
    }

    public void recordUserRegistration() {
        userRegistrationCounter.increment();
        log.debug("User registration metric incremented");
    }

    public void recordUserLogin() {
        userLoginCounter.increment();
        log.debug("User login metric incremented");
    }

    public void recordAuthenticationFailure() {
        authenticationFailureCounter.increment();
        log.debug("Authentication failure metric incremented");
    }
}
