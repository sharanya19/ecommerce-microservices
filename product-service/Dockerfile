# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY settings.gradle build.gradle gradle.properties ./
COPY product-service/build.gradle ./product-service/
COPY product-service/src ./product-service/src
RUN gradle :product-service:build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/product-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

