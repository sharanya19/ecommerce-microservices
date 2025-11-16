package com.ecommerce.user.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMetricsRecorder {

    private final Counter registrations;
    private final Counter logins;

    public UserMetricsRecorder(MeterRegistry registry) {
        this.registrations = Counter.builder("user_registrations_total")
            .description("Total number of registered users")
            .register(registry);
        this.logins = Counter.builder("user_logins_total")
            .description("Total successful logins")
            .register(registry);
    }

    public void recordRegistration() {
        registrations.increment();
    }

    public void recordLogin() {
        logins.increment();
    }
}


