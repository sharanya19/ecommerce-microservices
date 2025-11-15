# Deployment Guide

## Prerequisites

- Docker and Docker Compose installed
- Kubernetes cluster (for K8s deployment)
- Java 17+ and Gradle (for local development)

## Docker Compose Deployment

### Step 1: Build the Project

```bash
# Build all services
./gradlew clean build -x test
```

### Step 2: Start Services

```bash
# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f

# Check specific service logs
docker-compose logs -f user-service
```

### Step 3: Verify Services

1. **Eureka Dashboard**: http://localhost:8761
   - Should show all registered services

2. **API Gateway**: http://localhost:8080
   - Test endpoint: http://localhost:8080/api/users

3. **Health Checks**:
   - http://localhost:8080/actuator/health
   - http://localhost:8081/actuator/health
   - http://localhost:8082/actuator/health

### Step 4: Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## Kubernetes Deployment

### Step 1: Build Docker Images

```bash
# Build images for each service
docker build -t ecommerce/eureka-server:1.0.0 ./eureka-server
docker build -t ecommerce/config-server:1.0.0 ./config-server
docker build -t ecommerce/api-gateway:1.0.0 ./api-gateway
docker build -t ecommerce/user-service:1.0.0 ./user-service
docker build -t ecommerce/product-service:1.0.0 ./product-service
docker build -t ecommerce/order-service:1.0.0 ./order-service
docker build -t ecommerce/inventory-service:1.0.0 ./inventory-service
docker build -t ecommerce/payment-service:1.0.0 ./payment-service
```

### Step 2: Push Images to Registry (Optional)

```bash
# Tag and push to your registry
docker tag ecommerce/user-service:1.0.0 your-registry/user-service:1.0.0
docker push your-registry/user-service:1.0.0
```

### Step 3: Deploy to Kubernetes

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Deploy infrastructure
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/redis-deployment.yaml
kubectl apply -f k8s/kafka-deployment.yaml

# Wait for infrastructure to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n ecommerce --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n ecommerce --timeout=300s
kubectl wait --for=condition=ready pod -l app=kafka -n ecommerce --timeout=300s

# Deploy services
kubectl apply -f k8s/eureka-deployment.yaml
kubectl apply -f k8s/config-server-deployment.yaml
kubectl apply -f k8s/api-gateway-deployment.yaml
kubectl apply -f k8s/user-service-deployment.yaml
kubectl apply -f k8s/product-service-deployment.yaml
kubectl apply -f k8s/order-service-deployment.yaml
kubectl apply -f k8s/inventory-service-deployment.yaml
kubectl apply -f k8s/payment-service-deployment.yaml
```

### Step 4: Check Deployment Status

```bash
# Check pods
kubectl get pods -n ecommerce

# Check services
kubectl get services -n ecommerce

# Check logs
kubectl logs -f deployment/user-service -n ecommerce
```

### Step 5: Access Services

```bash
# Port forward to access services
kubectl port-forward service/api-gateway 8080:8080 -n ecommerce
kubectl port-forward service/eureka-server 8761:8761 -n ecommerce
```

## Troubleshooting

### Services Not Starting

1. Check logs:
   ```bash
   docker-compose logs service-name
   kubectl logs pod-name -n ecommerce
   ```

2. Verify dependencies:
   - MySQL is running
   - Redis is running
   - Kafka is running
   - Eureka is running (for service discovery)

### Database Connection Issues

- Verify MySQL is accessible
- Check database credentials in config files
- Ensure database exists or auto-create is enabled

### Service Discovery Issues

- Verify Eureka server is running
- Check service registration in Eureka dashboard
- Verify service names match in config files

### Kafka Connection Issues

- Verify Kafka and Zookeeper are running
- Check Kafka bootstrap servers configuration
- Verify network connectivity between services

## Production Considerations

1. **Secrets Management**: Use Kubernetes Secrets or external secret management
2. **Resource Limits**: Set appropriate CPU and memory limits
3. **Scaling**: Configure horizontal pod autoscaling
4. **Monitoring**: Set up Prometheus and Grafana
5. **Logging**: Configure centralized logging (ELK Stack)
6. **Security**: Enable TLS/SSL, use service mesh (Istio)
7. **Backup**: Configure database backups
8. **Disaster Recovery**: Set up multi-region deployment

