package com.ecommerce.payment.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class PaymentMetricsRecorder {

    private final Counter completed;
    private final Counter failed;
    private final Counter refunds;

    public PaymentMetricsRecorder(MeterRegistry registry) {
        this.completed = Counter.builder("payments_completed_total")
            .description("Completed payments")
            .register(registry);
        this.failed = Counter.builder("payments_failed_total")
            .description("Failed payments")
            .register(registry);
        this.refunds = Counter.builder("payments_refunded_total")
            .description("Refund operations")
            .register(registry);
    }

    public void recordPaymentCompleted() {
        completed.increment();
    }

    public void recordPaymentFailed() {
        failed.increment();
    }

    public void recordRefund() {
        refunds.increment();
    }
}


