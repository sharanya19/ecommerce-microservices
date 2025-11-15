# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY settings.gradle build.gradle gradle.properties ./
COPY order-service/build.gradle ./order-service/
COPY order-service/src ./order-service/src
RUN gradle :order-service:build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/order-service/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]

