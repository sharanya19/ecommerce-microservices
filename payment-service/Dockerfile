# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY settings.gradle build.gradle gradle.properties ./
COPY payment-service/build.gradle ./payment-service/
COPY payment-service/src ./payment-service/src
RUN gradle :payment-service:build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/payment-service/build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

