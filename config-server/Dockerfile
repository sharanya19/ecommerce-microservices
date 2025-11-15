# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY settings.gradle build.gradle ./
COPY config-server/build.gradle ./config-server/
COPY config-server/src ./config-server/src
RUN gradle :config-server:build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/config-server/build/libs/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]

