package com.ecommerce.payment.diagnostics;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class ConfigServerHealthClient {

    private static final Logger log = LoggerFactory.getLogger(ConfigServerHealthClient.class);

    private final RestTemplate restTemplate;
    private final String configServerHealthUrl;

    public ConfigServerHealthClient(
            RestTemplate restTemplate,
            @Value("${external.config-server.health-url}") String configServerHealthUrl) {
        this.restTemplate = restTemplate;
        this.configServerHealthUrl = configServerHealthUrl;
    }

    @CircuitBreaker(name = "configServer", fallbackMethod = "fallback")
    @Retry(name = "configServer")
    @RateLimiter(name = "configServer")
    @Bulkhead(name = "configServer")
    public Map<String, Object> fetchConfigServerHealth() {
        ResponseEntity<Map> response = restTemplate.getForEntity(configServerHealthUrl, Map.class);
        Map<String, Object> body = response.getBody();
        if (body == null) {
            return Map.of("status", "UNKNOWN", "timestamp", OffsetDateTime.now().toString());
        }
        return body;
    }

    @SuppressWarnings("unused")
    private Map<String, Object> fallback(Throwable throwable) {
        log.warn("Config Server health check failed: {}", throwable.getMessage());
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("status", "DEGRADED");
        fallbackResponse.put("timestamp", OffsetDateTime.now().toString());
        fallbackResponse.put("message", Optional.ofNullable(throwable.getMessage()).orElse("Config Server unavailable"));
        return fallbackResponse;
    }
}


