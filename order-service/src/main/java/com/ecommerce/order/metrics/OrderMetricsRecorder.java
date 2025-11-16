package com.ecommerce.order.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetricsRecorder {

    private final Counter ordersCreated;
    private final Counter statusUpdates;
    private final Counter paymentUpdates;

    public OrderMetricsRecorder(MeterRegistry registry) {
        this.ordersCreated = Counter.builder("orders_created_total")
            .description("Total orders created")
            .register(registry);
        this.statusUpdates = Counter.builder("order_status_updates_total")
            .description("Order status update operations")
            .register(registry);
        this.paymentUpdates = Counter.builder("order_payment_updates_total")
            .description("Order payment status update operations")
            .register(registry);
    }

    public void recordOrderCreated() {
        ordersCreated.increment();
    }

    public void recordStatusChange(String status) {
        statusUpdates.increment();
    }

    public void recordPaymentStatusChange(String status) {
        paymentUpdates.increment();
    }
}


