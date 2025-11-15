#!/bin/bash

echo "Building E-Commerce Microservices..."

# Build all services
./gradlew clean build -x test

echo "Build completed!"

