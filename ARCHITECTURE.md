# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway (8080)                      │
│              Single Entry Point for All Services            │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼──────┐   ┌────────▼────────┐   ┌────▼──────┐
│ Eureka Server│   │  Config Server   │   │  Services │
│   (8761)     │   │    (8888)        │   │           │
└──────────────┘   └──────────────────┘   └───────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼──────┐   ┌────────▼────────┐   ┌──────▼──────┐
│User Service │   │Product Service │   │Order Service│
│   (8081)    │   │    (8082)       │   │   (8083)    │
└─────────────┘   └─────────────────┘   └────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼──────┐   ┌────────▼────────┐
│Inventory    │   │Payment Service  │
│Service(8084)│   │    (8085)       │
└─────────────┘   └─────────────────┘
        │                   │
        └───────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼──────┐   ┌────────▼────────┐   ┌──────▼──────┐
│    MySQL     │   │     Redis       │   │    Kafka    │
│   Database   │   │     Cache       │   │   Events    │
└──────────────┘   └─────────────────┘   └─────────────┘
```

## Service Communication Flow

### Synchronous Communication
- **API Gateway** → **Services**: REST API calls via Eureka service discovery
- **Services** → **MySQL**: JPA/Hibernate for data persistence
- **Services** → **Redis**: Caching layer for performance

### Asynchronous Communication
- **Services** → **Kafka**: Event publishing for decoupled communication
- **Kafka** → **Services**: Event consumption for reactive updates

## Data Flow Examples

### Order Processing Flow
1. Client → API Gateway → Order Service
2. Order Service creates order
3. Order Service publishes "order.created" event to Kafka
4. Inventory Service consumes event and reserves inventory
5. Payment Service consumes event and processes payment
6. Order Service updates order status based on payment result

### Product Update Flow
1. Client → API Gateway → Product Service
2. Product Service updates product
3. Product Service publishes "product.updated" event
4. Inventory Service consumes event and updates inventory
5. Cache is invalidated in Redis

## Technology Stack by Layer

### Infrastructure Layer
- **Service Discovery**: Netflix Eureka
- **Configuration**: Spring Cloud Config
- **API Gateway**: Spring Cloud Gateway
- **Message Broker**: Apache Kafka
- **Cache**: Redis
- **Database**: MySQL 8.0

### Application Layer
- **Framework**: Spring Boot 3.2.0
- **Cloud**: Spring Cloud 2023.0.0
- **Persistence**: Spring Data JPA
- **Monitoring**: Spring Boot Actuator

### Deployment Layer
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **Build Tool**: Gradle (Groovy)

## Service Responsibilities

### User Service
- User registration and authentication
- User profile management
- User role management
- Publishes: user.created, user.updated, user.deleted

### Product Service
- Product catalog management
- Product search and filtering
- Product category management
- Publishes: product.created, product.updated, product.deleted

### Order Service
- Order creation and management
- Order status tracking
- Order history
- Consumes: payment events
- Publishes: order.created, order.status.updated

### Inventory Service
- Stock management
- Inventory reservation
- Low stock alerts
- Consumes: order events, product events
- Publishes: inventory.updated, inventory.reserved

### Payment Service
- Payment processing
- Payment status tracking
- Refund processing
- Consumes: order events
- Publishes: payment.processed, payment.refunded

## Scalability Features

1. **Horizontal Scaling**: All services can be scaled independently
2. **Load Balancing**: Eureka provides client-side load balancing
3. **Caching**: Redis reduces database load
4. **Async Processing**: Kafka enables non-blocking operations
5. **Stateless Services**: Services can be scaled without session affinity

## Resilience Features

1. **Service Discovery**: Automatic service registration and health checks
2. **Configuration Management**: Centralized config with refresh capability
3. **Health Checks**: Actuator endpoints for monitoring
4. **Event-Driven**: Decoupled services reduce cascading failures
5. **Database Isolation**: Each service has its own database

## Security Considerations

1. **API Gateway**: Single entry point for security policies
2. **Service-to-Service**: Internal communication within network
3. **Database**: Separate databases per service
4. **Secrets**: Should use Kubernetes Secrets or Vault in production

## Monitoring Points

1. **Eureka Dashboard**: Service registration status
2. **Actuator Endpoints**: Health, metrics, info
3. **Kafka Topics**: Event flow monitoring
4. **Database Metrics**: Connection pools, query performance
5. **Redis Metrics**: Cache hit rates

## Deployment Strategies

### Development
- Docker Compose for local development
- All services in single network
- Easy to start/stop

### Production
- Kubernetes for orchestration
- Separate namespaces
- Resource limits and quotas
- Auto-scaling policies
- Rolling updates

## Future Enhancements

1. **Distributed Tracing**: Zipkin/Jaeger integration
2. **Circuit Breakers**: Resilience4j for fault tolerance
3. **API Documentation**: Swagger/OpenAPI
4. **Authentication**: OAuth2/JWT implementation
5. **Rate Limiting**: API Gateway filters
6. **Service Mesh**: Istio for advanced traffic management

