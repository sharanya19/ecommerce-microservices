package com.ecommerce.inventory.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class InventoryMetricsRecorder {

    private final Counter created;
    private final Counter quantityAdjustments;
    private final Counter reservations;
    private final Counter confirmations;

    public InventoryMetricsRecorder(MeterRegistry registry) {
        this.created = Counter.builder("inventory_records_created_total")
            .description("Inventory entries created")
            .register(registry);
        this.quantityAdjustments = Counter.builder("inventory_quantity_adjustments_total")
            .description("Quantity adjustments applied")
            .register(registry);
        this.reservations = Counter.builder("inventory_reservations_total")
            .description("Reservation operations")
            .register(registry);
        this.confirmations = Counter.builder("inventory_reservation_confirmations_total")
            .description("Reservation confirmations")
            .register(registry);
    }

    public void recordInventoryCreated() {
        created.increment();
    }

    public void recordQuantityAdjustment(Number change) {
        quantityAdjustments.increment(Math.abs(change.doubleValue()));
    }

    public void recordReservation(Number quantity) {
        reservations.increment(quantity.doubleValue());
    }

    public void recordReservationConfirmation(Number quantity) {
        confirmations.increment(quantity.doubleValue());
    }
}


