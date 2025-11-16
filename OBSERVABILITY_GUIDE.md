# Observability Stack

This repository now exposes distributed tracing, metrics, and dashboards through
OpenTelemetry, Prometheus, and Grafana.

## Local Docker Compose

Run the full stack (includes OTEL Collector, Prometheus, and Grafana):

```bash
docker compose up -d --build otel-collector prometheus grafana user-service product-service \
  order-service inventory-service payment-service api-gateway
```

- OTLP traces are sent to the collector (`otel-collector:4317`) and forwarded to Zipkin.
- Prometheus scrapes each service at `/actuator/prometheus` and the collector metrics endpoint.
- Grafana automatically provisions two dashboards under the `E-Commerce` folder.

| Component | URL |
|-----------|-----|
| Grafana | http://localhost:3000 (admin / admin) |
| Prometheus | http://localhost:9090 |
| OTEL Collector metrics | http://localhost:8889/metrics |

## Kubernetes

The manifests in `k8s/otel` deploy the collector plus a `ServiceMonitor`. Apply them with:

```bash
kubectl apply -f k8s/otel/otel-collector.yaml
kubectl apply -f k8s/otel/otel-collector-servicemonitor.yaml
```

Ensure that Zipkin and Prometheus Operator are available in the same cluster/namespace.

## Custom Business Metrics

Every microservice records domain-specific counters that are visible in Prometheus:

- `user_registrations_total`, `user_logins_total`
- `product_created_total`, `product_stock_adjustments_total`
- `orders_created_total`, `order_status_updates_total`
- `inventory_reservations_total`, `inventory_quantity_adjustments_total`
- `payments_completed_total`, `payments_failed_total`, `payments_refunded_total`

Use the provided Grafana dashboards or extend them to add more KPIs.


