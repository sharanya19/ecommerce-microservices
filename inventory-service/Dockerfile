# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY settings.gradle build.gradle gradle.properties ./
COPY inventory-service/build.gradle ./inventory-service/
COPY inventory-service/src ./inventory-service/src
RUN gradle :inventory-service:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/inventory-service/build/libs/*.jar app.jar
ARG OTEL_AGENT_VERSION=1.33.0
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /otel && curl -sSL https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar -o /otel/opentelemetry-javaagent.jar
ENV OTEL_SERVICE_NAME=inventory-service
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_RESOURCE_ATTRIBUTES=service.name=inventory-service,service.namespace=ecommerce,environment=local
EXPOSE 8084
ENTRYPOINT ["java", "-javaagent:/otel/opentelemetry-javaagent.jar", "-jar", "app.jar"]

