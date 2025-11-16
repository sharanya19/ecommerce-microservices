package com.ecommerce.product.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductMetricsRecorder {

    private final Counter createdCounter;
    private final Counter stockAdjustmentCounter;

    public ProductMetricsRecorder(MeterRegistry registry) {
        this.createdCounter = Counter.builder("product_created_total")
            .description("Total number of products created")
            .register(registry);
        this.stockAdjustmentCounter = Counter.builder("product_stock_adjustments_total")
            .description("Total stock adjustment operations")
            .register(registry);
    }

    public void recordProductCreated() {
        createdCounter.increment();
    }

    public void recordStockAdjustment(Number delta) {
        stockAdjustmentCounter.increment(Math.abs(delta.doubleValue()));
    }
}


