# E-Commerce Microservices System

A comprehensive, industry-standard microservices architecture for an e-commerce platform built with Spring Boot, featuring service discovery, API gateway, distributed configuration, event-driven communication, and containerization.

## 🏗️ Architecture Overview

This project implements a microservices architecture with the following components:

### Infrastructure Services
- **Eureka Server** (Port 8761) - Service Discovery and Registration
- **Config Server** (Port 8888) - Centralized Configuration Management
- **API Gateway** (Port 8080) - Single Entry Point for All Services

### Business Services
- **User Service** (Port 8081) - User management and authentication
- **Product Service** (Port 8082) - Product catalog management
- **Order Service** (Port 8083) - Order processing and management
- **Inventory Service** (Port 8084) - Inventory and stock management
- **Payment Service** (Port 8085) - Payment processing

## ✨ Features

- ✅ **REST APIs** - Comprehensive RESTful endpoints for all services
- ✅ **JPA + MySQL** - Persistent data storage with MySQL databases
- ✅ **Redis Caching** - High-performance caching layer
- ✅ **Kafka Events** - Event-driven communication between services
- ✅ **Actuator Health Checks** - Monitoring and health endpoints
- ✅ **Config Server** - Centralized configuration management
- ✅ **Eureka Service Discovery** - Automatic service registration and discovery
- ✅ **API Gateway** - Unified API entry point with routing
- ✅ **Docker Support** - Containerized services
- ✅ **Kubernetes Deployment** - Production-ready K8s manifests

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Data JPA**
- **MySQL 8.0**
- **Redis 7**
- **Apache Kafka**
- **Netflix Eureka**
- **Spring Cloud Gateway**
- **Spring Cloud Config**
- **Gradle (Groovy)**
- **Docker**
- **Kubernetes**

## 📋 Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher
- Docker and Docker Compose
- Kubernetes cluster (for K8s deployment)
- MySQL 8.0 (if running without Docker)
- Redis (if running without Docker)
- Apache Kafka (if running without Docker)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended for Development)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ecommerce-microservices
   ```

2. **Build all services**
   ```bash
   ./gradlew build
   ```

3. **Start all services with Docker Compose**
   ```bash
   docker-compose up -d
   ```

4. **Verify services are running**
   - Eureka Dashboard: http://localhost:8761
   - API Gateway: http://localhost:8080
   - Config Server: http://localhost:8888

### Option 2: Local Development

1. **Start infrastructure services**
   ```bash
   # Start MySQL, Redis, and Kafka using Docker
   docker-compose up -d mysql redis zookeeper kafka
   ```

2. **Start services in order**
   ```bash
   # Terminal 1: Eureka Server
   ./gradlew :eureka-server:bootRun

   # Terminal 2: Config Server
   ./gradlew :config-server:bootRun

   # Terminal 3: API Gateway
   ./gradlew :api-gateway:bootRun

   # Terminal 4-8: Business Services
   ./gradlew :user-service:bootRun
   ./gradlew :product-service:bootRun
   ./gradlew :order-service:bootRun
   ./gradlew :inventory-service:bootRun
   ./gradlew :payment-service:bootRun
   ```

## 📡 API Endpoints

All APIs are accessible through the API Gateway at `http://localhost:8080`

### User Service
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Product Service
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products?category={category}` - Get products by category
- `GET /api/products?search={term}` - Search products
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Order Service
- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/user/{userId}` - Get orders by user
- `POST /api/orders` - Create order
- `PATCH /api/orders/{id}/status` - Update order status

### Inventory Service
- `GET /api/inventory` - Get all inventory
- `GET /api/inventory/product/{productId}` - Get inventory by product
- `POST /api/inventory/product/{productId}` - Create inventory
- `PATCH /api/inventory/product/{productId}/reserve` - Reserve quantity
- `PATCH /api/inventory/product/{productId}/confirm` - Confirm reservation

### Payment Service
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/order/{orderId}` - Get payments by order
- `POST /api/payments` - Process payment
- `POST /api/payments/{id}/refund` - Refund payment

## 🐳 Docker Commands

### Build Images
```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build user-service
```

### Start Services
```bash
# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d user-service

# View logs
docker-compose logs -f user-service
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## ☸️ Kubernetes Deployment

1. **Create namespace**
   ```bash
   kubectl apply -f k8s/namespace.yaml
   ```

2. **Deploy infrastructure**
   ```bash
   kubectl apply -f k8s/mysql-deployment.yaml
   kubectl apply -f k8s/redis-deployment.yaml
   kubectl apply -f k8s/kafka-deployment.yaml
   ```

3. **Deploy services**
   ```bash
   kubectl apply -f k8s/eureka-deployment.yaml
   kubectl apply -f k8s/config-server-deployment.yaml
   kubectl apply -f k8s/api-gateway-deployment.yaml
   kubectl apply -f k8s/user-service-deployment.yaml
   kubectl apply -f k8s/product-service-deployment.yaml
   kubectl apply -f k8s/order-service-deployment.yaml
   kubectl apply -f k8s/inventory-service-deployment.yaml
   kubectl apply -f k8s/payment-service-deployment.yaml
   ```

4. **Check deployment status**
   ```bash
   kubectl get pods -n ecommerce
   kubectl get services -n ecommerce
   ```

## 🔍 Monitoring & Health Checks

All services expose Actuator endpoints:

- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`

Eureka Dashboard: `http://localhost:8761`

## 📊 Database Schema

Each service has its own MySQL database:
- `userdb` - User Service
- `productdb` - Product Service
- `orderdb` - Order Service
- `inventorydb` - Inventory Service
- `paymentdb` - Payment Service

## 🔄 Event-Driven Communication

Services communicate via Kafka topics:
- `user-events` - User lifecycle events
- `product-events` - Product lifecycle events
- `order-events` - Order lifecycle events
- `inventory-events` - Inventory update events
- `payment-events` - Payment processing events

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific service
./gradlew :user-service:test
```

## 📝 Configuration

Service configurations are managed centrally via Config Server. Configuration files are located in:
- `config-server/src/main/resources/config/`

Each service can override configuration using profiles:
- `application.yml` - Default configuration
- `application-k8s.yml` - Kubernetes configuration

## 🏗️ Project Structure

```
ecommerce-microservices/
├── eureka-server/          # Service Discovery
├── config-server/          # Configuration Server
├── api-gateway/            # API Gateway
├── user-service/           # User Management
├── product-service/        # Product Catalog
├── order-service/          # Order Processing
├── inventory-service/       # Inventory Management
├── payment-service/        # Payment Processing
├── k8s/                    # Kubernetes Manifests
├── docker-compose.yml      # Docker Compose Configuration
├── build.gradle            # Root Build File
└── settings.gradle         # Gradle Settings
```

## 🔐 Security Notes

This is a demonstration project. For production use:
- Implement proper authentication and authorization
- Use HTTPS/TLS
- Secure database credentials
- Implement API rate limiting
- Add request validation and sanitization
- Use secrets management for sensitive data

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📄 License

This project is provided as-is for educational purposes.

## ✨ Enhanced Features (NEW!)

### 🔐 Authentication & Authorization
- **Spring Security** with JWT tokens
- Password encryption with BCrypt
- Role-based access control
- Secure API endpoints

### 📚 API Documentation
- **Swagger/OpenAPI** integration
- Interactive API documentation
- JWT authentication in Swagger UI
- Auto-generated API docs

### 🔍 Distributed Tracing
- **Zipkin** integration for request tracing
- Performance monitoring across services
- Service dependency visualization
- Access at: http://localhost:9411

### 🛡️ Circuit Breakers
- **Resilience4j** for fault tolerance
- Automatic service degradation
- Retry mechanisms
- Circuit breaker health indicators

### 📊 ELK Stack Logging
- **Elasticsearch** for log storage
- **Logstash** for log processing
- **Kibana** for visualization
- Centralized logging across all services
- Access Kibana at: http://localhost:5601

### 🔄 CI/CD Pipeline
- **Jenkins** pipeline configuration
- Automated testing
- Docker image building
- Kubernetes deployment
- Test reporting

### 🧪 Comprehensive Testing
- **JUnit 5** test framework
- Unit tests for services
- Integration tests for controllers
- Mock-based testing with Mockito
- Test coverage reports

See [ENHANCEMENTS.md](ENHANCEMENTS.md) for detailed documentation.

## 📞 Support

For issues and questions, please open an issue in the repository.

---

**Built with ❤️ using Spring Boot and Microservices Architecture**

