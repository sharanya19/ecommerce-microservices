# Resilience4j Setup

The application now ships with a consistent Resilience4j configuration in **every** domain
microservice (`user`, `product`, `order`, `inventory`, `payment`). The key pieces are:

1. **Dependencies** – each service includes the `resilience4j-spring-boot3` BOM which provides
   retry, circuit-breaker, bulkhead and rate-limiter support.
2. **Configuration** – every `application.yml` declares the same `resilience4j.*` instances and an
   `external.config-server.health-url` pointing to the Config Server actuator endpoint.
3. **Client wrapper** – a lightweight `ConfigServerHealthClient` bean wraps a `RestTemplate`
   call to the Config Server, annotated with `@CircuitBreaker`, `@Retry`, `@Bulkhead` and
   `@RateLimiter`. The client exposes graceful fallbacks so the consuming controller can continue
   working even when the downstream dependency is degraded.
4. **Diagnostics endpoint** – each service exposes `GET /diagnostics/config-server`, so the
   behaviour can be exercised quickly from the terminal or Swagger.

```java
@Component
class ConfigServerHealthClient {
    @CircuitBreaker(name = "configServer", fallbackMethod = "fallback")
    @Retry(name = "configServer")
    @RateLimiter(name = "configServer")
    @Bulkhead(name = "configServer")
    Map<String, Object> fetchConfigServerHealth() {
        ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
        return Optional.ofNullable(response.getBody())
                       .orElseGet(() -> Map.of("status", "UNKNOWN"));
    }

    private Map<String, Object> fallback(Throwable throwable) {
        return Map.of(
            "status", "DEGRADED",
            "timestamp", OffsetDateTime.now().toString(),
            "message", Optional.ofNullable(throwable.getMessage())
                               .orElse("Config Server unavailable"));
    }
}
```

## Optional WebClient Wrapper

If you prefer the reactive API you can drop the following snippet into any service. The annotations
stay the same – simply return a `Mono<T>` instead of blocking on the `RestTemplate`.

```java
@Component
class ReactiveInventoryClient {
    private final WebClient webClient;

    ReactiveInventoryClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://inventory-service:8084").build();
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallback")
    @Retry(name = "inventoryService")
    @RateLimiter(name = "inventoryService")
    @Bulkhead(name = "inventoryService", type = Bulkhead.Type.THREADPOOL)
    public Mono<InventoryDto> fetchInventory(Long productId) {
        return webClient.get()
            .uri("/inventory/product/{productId}", productId)
            .retrieve()
            .bodyToMono(InventoryDto.class);
    }

    private Mono<InventoryDto> fallback(Long productId, Throwable throwable) {
        return Mono.just(new InventoryDto(productId, 0, 0, "DEGRADED"));
    }
}
```

This pattern can be re-used for any downstream HTTP integration – simply reference the matching
`resilience4j` instance in `application.yml` and provide a fallback with the same method signature.


