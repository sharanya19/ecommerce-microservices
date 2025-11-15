# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY settings.gradle build.gradle gradle.properties ./
COPY inventory-service/build.gradle ./inventory-service/
COPY inventory-service/src ./inventory-service/src
RUN gradle :inventory-service:build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/inventory-service/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]

