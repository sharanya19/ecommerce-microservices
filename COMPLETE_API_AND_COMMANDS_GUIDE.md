# Complete E-Commerce Microservices API & Commands Guide

## 📋 Table of Contents

1. [Service Ports Overview](#service-ports-overview)
2. [API Endpoints](#api-endpoints)
3. [Running PowerShell Test Scripts](#running-powershell-test-scripts)
4. [Docker Commands](#docker-commands)
5. [Kubernetes Commands](#kubernetes-commands)
6. [Troubleshooting Commands](#troubleshooting-commands)
7. [Monitoring & Observability URLs](#monitoring--observability-urls)
8. [Swagger UI URLs](#swagger-ui-urls)
9. [Health Check Endpoints](#health-check-endpoints)
10. [Port Forwarding](#port-forwarding)
11. [Sleep/Wait Commands](#sleepwait-commands)

---

## 🔌 Service Ports Overview

### Infrastructure Services
| Service | Port | Description |
|---------|------|-------------|
| Eureka Server | 8761 | Service Discovery |
| Config Server | 8888 | Configuration Management |
| API Gateway | 8080 | API Gateway |
| Zipkin | 9411 | Distributed Tracing |
| Prometheus | 9090 | Metrics Collection |
| Grafana | 3000 | Metrics Visualization |
| OTEL Collector | 4317, 4318, 8889 | OpenTelemetry Collector |
| Elasticsearch | 9200 | Log Storage |
| Logstash | 5044, 9600 | Log Processing |

### Microservices
| Service | Port | Description |
|---------|------|-------------|
| User Service | 8081 | User Management |
| Product Service | 8082 | Product Catalog |
| Order Service | 8083 | Order Management |
| Inventory Service | 8084 | Inventory Management |
| Payment Service | 8085 | Payment Processing |

### Database & Messaging
| Service | Port | Description |
|---------|------|-------------|
| MySQL | 3306 | Primary Database |
| Redis | 6379 | Cache & Session Store |
| Kafka | 9092 | Message Broker |
| Zookeeper | 2181 | Kafka Coordination |

---

## 📡 API Endpoints

### User Service (Port 8081)

#### Authentication & User Management
```http
POST   /users/register              # Register new user
POST   /users/login                 # User login
GET    /users                       # Get all users (requires auth)
GET    /users/{id}                  # Get user by ID (requires auth)
GET    /users/username/{username}   # Get user by username (requires auth)
PUT    /users/{id}                  # Update user (requires auth)
DELETE /users/{id}                  # Delete user (requires auth)
```

#### Example Requests

**Register User:**
```bash
curl -X POST http://localhost:8081/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User",
    "phone": "9999999999",
    "address": "Hyderabad",
    "role": "CUSTOMER"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8081/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Password123!"
  }'
```

**Get User (with auth):**
```bash
curl -X GET http://localhost:8081/users/{id} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Product Service (Port 8082)

```http
GET    /products                    # Get all products
GET    /products/{id}               # Get product by ID
GET    /products?search={query}     # Search products
GET    /products?category={cat}     # Get products by category
POST   /products                    # Create product
PUT    /products/{id}               # Update product
PATCH  /products/{id}/stock?quantity={qty}  # Update stock
DELETE /products/{id}               # Delete product
```

#### Example Requests

**Create Product:**
```bash
curl -X POST http://localhost:8082/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Product description",
    "price": 2999.99,
    "stock": 75,
    "category": "Electronics",
    "imageUrl": "https://example.com/image.png",
    "sku": "TEST-001",
    "status": "ACTIVE"
  }'
```

**Update Stock:**
```bash
curl -X PATCH "http://localhost:8082/products/{id}/stock?quantity=120"
```

### Order Service (Port 8083)

```http
GET    /orders                      # Get all orders
GET    /orders/{id}                 # Get order by ID
GET    /orders/user/{userId}         # Get orders by user
POST   /orders                      # Create order
PATCH  /orders/{id}/status?status={status}        # Update order status
PATCH  /orders/{id}/payment-status?paymentStatus={status}  # Update payment status
```

#### Example Requests

**Create Order:**
```bash
curl -X POST http://localhost:8083/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ],
    "shippingAddress": "Hyderabad",
    "billingAddress": "Hyderabad"
  }'
```

### Inventory Service (Port 8084)

```http
GET    /inventory                   # Get all inventory
GET    /inventory/product/{productId}  # Get inventory by product
POST   /inventory/product/{productId}?initialQuantity={qty}  # Create inventory
PATCH  /inventory/product/{productId}/quantity?quantityChange={change}  # Adjust quantity
PATCH  /inventory/product/{productId}/reserve?quantity={qty}  # Reserve inventory
PATCH  /inventory/product/{productId}/release?quantity={qty}  # Release inventory
PATCH  /inventory/product/{productId}/confirm?quantity={qty}  # Confirm inventory
```

#### Example Requests

**Create Inventory:**
```bash
curl -X POST "http://localhost:8084/inventory/product/1?initialQuantity=60"
```

**Reserve Inventory:**
```bash
curl -X PATCH "http://localhost:8084/inventory/product/1/reserve?quantity=5"
```

### Payment Service (Port 8085)

```http
GET    /payments                    # Get all payments
GET    /payments/{id}               # Get payment by ID
GET    /payments/transaction/{transactionId}  # Get by transaction ID
GET    /payments/order/{orderId}    # Get payments by order
GET    /payments/user/{userId}      # Get payments by user
POST   /payments                    # Process payment
POST   /payments/{id}/refund        # Refund payment
```

#### Example Requests

**Process Payment:**
```bash
curl -X POST http://localhost:8085/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1,
    "amount": 5999.98,
    "paymentMethod": "CREDIT_CARD"
  }'
```

---

## 🧪 Running PowerShell Test Scripts

### Prerequisites
- PowerShell 5.1 or later
- Services must be running (Docker Compose or Kubernetes)
- Port forwarding configured (for Kubernetes)

### 1. Test Health Endpoints

**Script:** `test-health-endpoints.ps1`

**Run Command:**
```powershell
.\test-health-endpoints.ps1
```

**What it tests:**
- Eureka Server health (port 8761)
- Config Server health (port 8888)
- API Gateway health (port 8080)
- All microservices health (ports 8081-8085)
- Zipkin (port 9411)

**Expected Output:**
```
========================================
HEALTH ENDPOINTS TESTING
========================================

1. INFRASTRUCTURE SERVICES
=========================

[PASS] - Eureka Server
  URL: http://localhost:8761
  Status: 200 | Status: UP

[PASS] - Config Server Health
  URL: http://localhost:8888/actuator/health
  Status: 200 | Status: UP

...
```

### 2. Run All API Tests

**Script:** `run-all-api-tests.ps1`

**Run Command:**
```powershell
.\run-all-api-tests.ps1
```

**What it tests:**
- Health checks for all services
- User registration and authentication
- Product CRUD operations
- Inventory management
- Order creation and management
- Payment processing
- Cleanup operations

**Output:**
- Console output with PASS/FAIL status
- JSON summary saved to `api-test-summary.json`

**Example:**
```powershell
[PASS] User - Register (POST http://localhost:8081/users/register)
[PASS] User - Login (POST http://localhost:8081/users/login)
[PASS] Product - Create (POST http://localhost:8082/products)
...
```

### 3. Test Swagger URLs

**Script:** `test-swagger-urls.ps1`

**Run Command:**
```powershell
.\test-swagger-urls.ps1
```

**What it tests:**
- Swagger UI accessibility for all microservices
- API documentation endpoints

**Expected Output:**
```
========================================
SWAGGER UI ACCESSIBILITY TEST
========================================

User Service - Testing: http://localhost:8081/swagger-ui/index.html
  [PASS] Swagger UI is accessible
  Swagger URL: http://localhost:8081/swagger-ui/index.html
  API Docs: http://localhost:8081/v3/api-docs
...
```

### 4. Verify Setup

**Script:** `verify-setup.ps1`

**Run Command:**
```powershell
.\verify-setup.ps1
```

**What it checks:**
- Prerequisites (Docker, kubectl, Git)
- Docker Compose services status
- Kubernetes cluster connectivity
- Namespace and pods status
- Health endpoints
- GitHub Actions setup

**Expected Output:**
```
========================================
ECOMMERCE MICROSERVICES - SETUP VERIFICATION
========================================

1. CHECKING PREREQUISITES
=========================
[OK] Docker is installed
[OK] kubectl is installed
[OK] Git is installed

2. CHECKING DOCKER COMPOSE SERVICES
====================================
[OK] Docker Compose is running

3. CHECKING KUBERNETES CLUSTER
==============================
[OK] kubectl is configured
[OK] Kubernetes cluster is accessible
[OK] 'ecommerce' namespace exists
...
```

### Running Scripts with Error Handling

**Continue on errors:**
```powershell
$ErrorActionPreference = "Continue"
.\test-health-endpoints.ps1
```

**Stop on errors:**
```powershell
$ErrorActionPreference = "Stop"
.\run-all-api-tests.ps1
```

**Run with verbose output:**
```powershell
.\test-health-endpoints.ps1 -Verbose
```

---

## 🐳 Docker Commands

### Basic Docker Commands

**Check Docker version:**
```bash
docker --version
docker version
```

**Check Docker daemon:**
```bash
docker info
docker ps
```

### Docker Compose Commands

**Start all services:**
```bash
docker compose up -d
```

**Start specific services:**
```bash
docker compose up -d eureka-server config-server
docker compose up -d user-service product-service
```

**Stop all services:**
```bash
docker compose down
```

**Stop and remove volumes:**
```bash
docker compose down -v
```

**View running services:**
```bash
docker compose ps
```

**View logs:**
```bash
# All services
docker compose logs

# Specific service
docker compose logs user-service
docker compose logs -f user-service  # Follow logs
```

**Rebuild and start:**
```bash
docker compose up -d --build
```

**Restart a service:**
```bash
docker compose restart user-service
```

**Scale services:**
```bash
docker compose up -d --scale user-service=3
```

### Docker Image Commands

**List images:**
```bash
docker images
docker images | grep ecommerce
```

**Build image:**
```bash
# Build from project root
docker build -f user-service/Dockerfile -t ecommerce/user-service:1.0.0 .

# Build all services
docker build -f api-gateway/Dockerfile -t ecommerce/api-gateway:1.0.0 .
docker build -f user-service/Dockerfile -t ecommerce/user-service:1.0.0 .
docker build -f product-service/Dockerfile -t ecommerce/product-service:1.0.0 .
docker build -f order-service/Dockerfile -t ecommerce/order-service:1.0.0 .
docker build -f inventory-service/Dockerfile -t ecommerce/inventory-service:1.0.0 .
docker build -f payment-service/Dockerfile -t ecommerce/payment-service:1.0.0 .
docker build -f config-server/Dockerfile -t ecommerce/config-server:1.0.0 .
docker build -f eureka-server/Dockerfile -t ecommerce/eureka-server:1.0.0 .
```

**Remove images:**
```bash
docker rmi ecommerce/user-service:1.0.0
docker image prune -a  # Remove all unused images
```

**Tag image:**
```bash
docker tag ecommerce/user-service:1.0.0 ecommerce/user-service:latest
```

### Docker Container Commands

**List containers:**
```bash
docker ps                    # Running containers
docker ps -a                 # All containers
docker ps --filter "name=user"  # Filter by name
```

**Execute commands in container:**
```bash
docker exec -it user-service bash
docker exec user-service ls /app
```

**View container logs:**
```bash
docker logs user-service
docker logs -f user-service  # Follow logs
docker logs --tail 100 user-service  # Last 100 lines
```

**Inspect container:**
```bash
docker inspect user-service
docker inspect --format='{{.NetworkSettings.IPAddress}}' user-service
```

**Stop/Start containers:**
```bash
docker stop user-service
docker start user-service
docker restart user-service
```

**Remove containers:**
```bash
docker rm user-service
docker container prune  # Remove all stopped containers
```

### Docker Network Commands

**List networks:**
```bash
docker network ls
```

**Inspect network:**
```bash
docker network inspect ecommerce-network
```

**Create network:**
```bash
docker network create ecommerce-network
```

### Docker Volume Commands

**List volumes:**
```bash
docker volume ls
```

**Inspect volume:**
```bash
docker volume inspect mysql_data
```

**Remove volumes:**
```bash
docker volume rm mysql_data
docker volume prune  # Remove unused volumes
```

### Minikube Docker Integration

**Configure Docker to use minikube's daemon:**
```powershell
# PowerShell
minikube docker-env | Invoke-Expression

# Or use the PowerShell-specific command
& minikube -p minikube docker-env --shell powershell | Invoke-Expression
```

**Load image into minikube:**
```bash
minikube image load ecommerce/user-service:1.0.0
minikube image load ecommerce/product-service:1.0.0
minikube image load ecommerce/order-service:1.0.0
minikube image load ecommerce/inventory-service:1.0.0
minikube image load ecommerce/payment-service:1.0.0
minikube image load ecommerce/api-gateway:1.0.0
minikube image load ecommerce/config-server:1.0.0
minikube image load ecommerce/eureka-server:1.0.0
```

**List images in minikube:**
```bash
minikube image ls | grep ecommerce
```

**Build and load script:**
```powershell
# Use the provided script
.\k8s\build-and-load-images.ps1
```

---

## ☸️ Kubernetes Commands

### Cluster Management

**Check cluster status:**
```bash
kubectl cluster-info
kubectl get nodes
kubectl get nodes -o wide
```

**Check kubectl version:**
```bash
kubectl version --client
kubectl version
```

**Get current context:**
```bash
kubectl config current-context
kubectl config get-contexts
```

**Switch context:**
```bash
kubectl config use-context minikube
```

### Namespace Commands

**List namespaces:**
```bash
kubectl get namespaces
kubectl get ns
```

**Create namespace:**
```bash
kubectl create namespace ecommerce
```

**Apply namespace:**
```bash
kubectl apply -f k8s/namespace.yaml
```

**Set default namespace:**
```bash
kubectl config set-context --current --namespace=ecommerce
```

### Deployment Commands

**Apply all deployments:**
```bash
kubectl apply -f k8s/ -n ecommerce
```

**Apply specific deployment:**
```bash
kubectl apply -f k8s/user-service-deployment.yaml -n ecommerce
```

**List deployments:**
```bash
kubectl get deployments -n ecommerce
kubectl get deploy -n ecommerce
```

**Describe deployment:**
```bash
kubectl describe deployment user-service -n ecommerce
```

**Scale deployment:**
```bash
kubectl scale deployment user-service --replicas=3 -n ecommerce
```

**Update deployment:**
```bash
kubectl set image deployment/user-service user-service=ecommerce/user-service:1.0.1 -n ecommerce
```

**Rollout status:**
```bash
kubectl rollout status deployment/user-service -n ecommerce
```

**Rollback deployment:**
```bash
kubectl rollout undo deployment/user-service -n ecommerce
```

**Delete deployment:**
```bash
kubectl delete deployment user-service -n ecommerce
```

### Pod Commands

**List pods:**
```bash
kubectl get pods -n ecommerce
kubectl get pods -n ecommerce -o wide
kubectl get pods -n ecommerce -l app=user-service
```

**Describe pod:**
```bash
kubectl describe pod user-service-xxx -n ecommerce
```

**View pod logs:**
```bash
kubectl logs user-service-xxx -n ecommerce
kubectl logs -f user-service-xxx -n ecommerce  # Follow logs
kubectl logs --tail=100 user-service-xxx -n ecommerce
kubectl logs -l app=user-service -n ecommerce  # All pods with label
```

**Execute command in pod:**
```bash
kubectl exec -it user-service-xxx -n ecommerce -- bash
kubectl exec user-service-xxx -n ecommerce -- ls /app
```

**Delete pod:**
```bash
kubectl delete pod user-service-xxx -n ecommerce
```

**Get pod events:**
```bash
kubectl get events -n ecommerce --sort-by='.lastTimestamp'
```

### Service Commands

**List services:**
```bash
kubectl get services -n ecommerce
kubectl get svc -n ecommerce
```

**Describe service:**
```bash
kubectl describe service user-service -n ecommerce
```

**Get service endpoints:**
```bash
kubectl get endpoints -n ecommerce
kubectl get endpoints user-service -n ecommerce
```

**Port forward to service:**
```bash
kubectl port-forward service/user-service 8081:8081 -n ecommerce
kubectl port-forward service/api-gateway 8080:8080 -n ecommerce
```

### ConfigMap and Secret Commands

**List configmaps:**
```bash
kubectl get configmaps -n ecommerce
kubectl get cm -n ecommerce
```

**Describe configmap:**
```bash
kubectl describe configmap user-service-config -n ecommerce
```

**Get configmap data:**
```bash
kubectl get configmap user-service-config -n ecommerce -o yaml
```

**List secrets:**
```bash
kubectl get secrets -n ecommerce
```

**Describe secret:**
```bash
kubectl describe secret ecommerce-secrets -n ecommerce
```

### Ingress Commands

**List ingress:**
```bash
kubectl get ingress -n ecommerce
kubectl get ing -n ecommerce
```

**Describe ingress:**
```bash
kubectl describe ingress ecommerce-ingress -n ecommerce
```

### Resource Management

**Get all resources:**
```bash
kubectl get all -n ecommerce
```

**Get resources with labels:**
```bash
kubectl get all -n ecommerce -l app=user-service
```

**Watch resources:**
```bash
kubectl get pods -n ecommerce -w
kubectl get deployments -n ecommerce -w
```

**Get resource YAML:**
```bash
kubectl get deployment user-service -n ecommerce -o yaml
kubectl get pod user-service-xxx -n ecommerce -o yaml
```

**Edit resource:**
```bash
kubectl edit deployment user-service -n ecommerce
```

### Minikube Commands

**Start minikube:**
```bash
minikube start
```

**Stop minikube:**
```bash
minikube stop
```

**Check minikube status:**
```bash
minikube status
```

**Get minikube IP:**
```bash
minikube ip
```

**Open minikube dashboard:**
```bash
minikube dashboard
```

**SSH into minikube:**
```bash
minikube ssh
```

**Delete minikube cluster:**
```bash
minikube delete
```

---

## 🔧 Troubleshooting Commands

### Pod Issues

**Check pod status:**
```bash
kubectl get pods -n ecommerce
kubectl get pods -n ecommerce --field-selector=status.phase!=Running
```

**Check pod events:**
```bash
kubectl describe pod <pod-name> -n ecommerce
kubectl get events -n ecommerce --sort-by='.lastTimestamp' | grep <pod-name>
```

**Check pod logs:**
```bash
kubectl logs <pod-name> -n ecommerce
kubectl logs <pod-name> -n ecommerce --previous  # Previous container
kubectl logs <pod-name> -n ecommerce --tail=50
```

**Check pod resource usage:**
```bash
kubectl top pod <pod-name> -n ecommerce
kubectl top pods -n ecommerce
```

### Image Issues

**Check image pull errors:**
```bash
kubectl describe pod <pod-name> -n ecommerce | grep -A 10 "Events:"
```

**Verify images in minikube:**
```bash
minikube ssh
docker images | grep ecommerce
exit
```

**Load image into minikube:**
```bash
minikube image load ecommerce/user-service:1.0.0
```

### Network Issues

**Check service endpoints:**
```bash
kubectl get endpoints -n ecommerce
kubectl describe service <service-name> -n ecommerce
```

**Test service connectivity:**
```bash
kubectl run -it --rm debug --image=busybox --restart=Never -n ecommerce -- wget -O- http://user-service:8081/actuator/health
```

**Check DNS resolution:**
```bash
kubectl run -it --rm debug --image=busybox --restart=Never -n ecommerce -- nslookup user-service
```

### Configuration Issues

**Check configmap:**
```bash
kubectl get configmap -n ecommerce
kubectl describe configmap <configmap-name> -n ecommerce
kubectl get configmap <configmap-name> -n ecommerce -o yaml
```

**Check secrets:**
```bash
kubectl get secrets -n ecommerce
kubectl describe secret <secret-name> -n ecommerce
```

**Verify environment variables:**
```bash
kubectl exec <pod-name> -n ecommerce -- env | grep SPRING
```

### Database Connectivity

**Test MySQL connection:**
```bash
kubectl run -it --rm mysql-client --image=mysql:8.0 --restart=Never -n ecommerce -- mysql -h mysql -u root -p
```

**Check MySQL logs:**
```bash
kubectl logs -l app=mysql -n ecommerce
```

### Common Issues and Solutions

**Pod in ImagePullBackOff:**
```bash
# Check image name
kubectl describe pod <pod-name> -n ecommerce

# Load image into minikube
minikube image load <image-name>

# Or set imagePullPolicy to Never for local images
```

**Pod in CrashLoopBackOff:**
```bash
# Check logs
kubectl logs <pod-name> -n ecommerce --previous

# Check events
kubectl describe pod <pod-name> -n ecommerce

# Check resource limits
kubectl top pod <pod-name> -n ecommerce
```

**Pod in Pending:**
```bash
# Check events
kubectl describe pod <pod-name> -n ecommerce

# Check node resources
kubectl describe node

# Check PVC status
kubectl get pvc -n ecommerce
```

**Service has no endpoints:**
```bash
# Check pod labels match service selector
kubectl get pods -n ecommerce --show-labels
kubectl describe service <service-name> -n ecommerce

# Check if pods are running
kubectl get pods -n ecommerce
```

---

## 📊 Monitoring & Observability URLs

### Prometheus

**Local (Docker Compose):**
- URL: http://localhost:9090
- Metrics endpoint: http://localhost:9090/api/v1/query
- Targets: http://localhost:9090/targets
- Alerts: http://localhost:9090/alerts

**Kubernetes (Port Forward):**
```bash
kubectl port-forward service/prometheus 9090:9090 -n monitoring
```
- URL: http://localhost:9090

**Prometheus Metrics Endpoints (per service):**
- User Service: http://localhost:8081/actuator/prometheus
- Product Service: http://localhost:8082/actuator/prometheus
- Order Service: http://localhost:8083/actuator/prometheus
- Inventory Service: http://localhost:8084/actuator/prometheus
- Payment Service: http://localhost:8085/actuator/prometheus
- API Gateway: http://localhost:8080/actuator/prometheus

**Example Prometheus Queries:**
```
# HTTP requests total
http_server_requests_seconds_count

# JVM memory used
jvm_memory_used_bytes

# Custom business metrics
user_registrations_total
product_created_total
orders_created_total
payments_completed_total
```

### Grafana

**Local (Docker Compose):**
- URL: http://localhost:3000
- Username: `admin`
- Password: `admin`

**Kubernetes (Port Forward):**
```bash
kubectl port-forward service/grafana 3000:3000 -n monitoring
```
- URL: http://localhost:3000

**Grafana Dashboards:**
- E-Commerce Overview: http://localhost:3000/d/ecommerce-overview
- Service Metrics: http://localhost:3000/d/service-metrics
- Business Metrics: http://localhost:3000/d/business-metrics

### Zipkin (Distributed Tracing)

**Local (Docker Compose):**
- URL: http://localhost:9411
- API: http://localhost:9411/api/v2/traces

**Kubernetes (Port Forward):**
```bash
kubectl port-forward service/zipkin 9411:9411 -n observability
```
- URL: http://localhost:9411

### OTEL Collector

**Metrics Endpoint:**
- URL: http://localhost:8889/metrics

**OTLP Endpoints:**
- gRPC: localhost:4317
- HTTP: localhost:4318

### Elasticsearch

**Local (Docker Compose):**
- URL: http://localhost:9200
- Health: http://localhost:9200/_cluster/health
- Indices: http://localhost:9200/_cat/indices

**Kubernetes (Port Forward):**
```bash
kubectl port-forward service/elasticsearch 9200:9200 -n observability
```

### Logstash

**Local (Docker Compose):**
- Beats input: localhost:5044
- Monitoring: http://localhost:9600

---

## 📚 Swagger UI URLs

### Local Access (Docker Compose or Port Forward)

| Service | Swagger UI URL | API Docs URL |
|---------|---------------|--------------|
| User Service | http://localhost:8081/swagger-ui/index.html | http://localhost:8081/v3/api-docs |
| Product Service | http://localhost:8082/swagger-ui/index.html | http://localhost:8082/v3/api-docs |
| Order Service | http://localhost:8083/swagger-ui/index.html | http://localhost:8083/v3/api-docs |
| Inventory Service | http://localhost:8084/swagger-ui/index.html | http://localhost:8084/v3/api-docs |
| Payment Service | http://localhost:8085/swagger-ui/index.html | http://localhost:8085/v3/api-docs |
| API Gateway | http://localhost:8080/swagger-ui/index.html | http://localhost:8080/v3/api-docs |

### Kubernetes Access (via Port Forward)

**User Service:**
```bash
kubectl port-forward service/user-service 8081:8081 -n ecommerce
# Then access: http://localhost:8081/swagger-ui/index.html
```

**Product Service:**
```bash
kubectl port-forward service/product-service 8082:8082 -n ecommerce
# Then access: http://localhost:8082/swagger-ui/index.html
```

**Order Service:**
```bash
kubectl port-forward service/order-service 8083:8083 -n ecommerce
# Then access: http://localhost:8083/swagger-ui/index.html
```

**Inventory Service:**
```bash
kubectl port-forward service/inventory-service 8084:8084 -n ecommerce
# Then access: http://localhost:8084/swagger-ui/index.html
```

**Payment Service:**
```bash
kubectl port-forward service/payment-service 8085:8085 -n ecommerce
# Then access: http://localhost:8085/swagger-ui/index.html
```

**API Gateway:**
```bash
kubectl port-forward service/api-gateway 8080:8080 -n ecommerce
# Then access: http://localhost:8080/swagger-ui/index.html
```

### Test Swagger URLs Script

Run the provided script to test all Swagger URLs:
```powershell
.\test-swagger-urls.ps1
```

---

## 🏥 Health Check Endpoints

### Infrastructure Services

| Service | Health Endpoint |
|---------|----------------|
| Eureka Server | http://localhost:8761/actuator/health |
| Config Server | http://localhost:8888/actuator/health |
| API Gateway | http://localhost:8080/actuator/health |

### Microservices

| Service | Health Endpoint |
|---------|----------------|
| User Service | http://localhost:8081/actuator/health |
| Product Service | http://localhost:8082/actuator/health |
| Order Service | http://localhost:8083/actuator/health |
| Inventory Service | http://localhost:8084/actuator/health |
| Payment Service | http://localhost:8085/actuator/health |

### Detailed Health Endpoints

**Liveness Probe:**
- http://localhost:8081/actuator/health/liveness

**Readiness Probe:**
- http://localhost:8081/actuator/health/readiness

**Info Endpoint:**
- http://localhost:8081/actuator/info

**Metrics Endpoint:**
- http://localhost:8081/actuator/metrics

**Prometheus Metrics:**
- http://localhost:8081/actuator/prometheus

### Test Health Endpoints Script

Run the provided script to test all health endpoints:
```powershell
.\test-health-endpoints.ps1
```

### Manual Health Check

**Using curl:**
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8085/actuator/health
```

**Using PowerShell:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing
```

---

## 🔌 Port Forwarding

### Kubernetes Port Forwarding Commands

**User Service:**
```bash
kubectl port-forward service/user-service 8081:8081 -n ecommerce
```

**Product Service:**
```bash
kubectl port-forward service/product-service 8082:8082 -n ecommerce
```

**Order Service:**
```bash
kubectl port-forward service/order-service 8083:8083 -n ecommerce
```

**Inventory Service:**
```bash
kubectl port-forward service/inventory-service 8084:8084 -n ecommerce
```

**Payment Service:**
```bash
kubectl port-forward service/payment-service 8085:8085 -n ecommerce
```

**API Gateway:**
```bash
kubectl port-forward service/api-gateway 8080:8080 -n ecommerce
```

**Config Server:**
```bash
kubectl port-forward service/config-server 8888:8888 -n ecommerce
```

**Eureka Server:**
```bash
kubectl port-forward service/eureka-server 8761:8761 -n ecommerce
```

**Prometheus:**
```bash
kubectl port-forward service/prometheus 9090:9090 -n monitoring
```

**Grafana:**
```bash
kubectl port-forward service/grafana 3000:3000 -n monitoring
```

**Zipkin:**
```bash
kubectl port-forward service/zipkin 9411:9411 -n observability
```

### Port Forward to Pod (Alternative)

**Forward to specific pod:**
```bash
kubectl port-forward pod/user-service-xxx 8081:8081 -n ecommerce
```

### Background Port Forwarding

**Run in background (Linux/Mac):**
```bash
kubectl port-forward service/user-service 8081:8081 -n ecommerce &
```

**Run in background (PowerShell):**
```powershell
Start-Job -ScriptBlock { kubectl port-forward service/user-service 8081:8081 -n ecommerce }
```

### Multiple Port Forwards Script

Create a script to forward all ports:

**port-forward-all.ps1:**
```powershell
# Port forward all services
Start-Job -ScriptBlock { kubectl port-forward service/user-service 8081:8081 -n ecommerce }
Start-Job -ScriptBlock { kubectl port-forward service/product-service 8082:8082 -n ecommerce }
Start-Job -ScriptBlock { kubectl port-forward service/order-service 8083:8083 -n ecommerce }
Start-Job -ScriptBlock { kubectl port-forward service/inventory-service 8084:8084 -n ecommerce }
Start-Job -ScriptBlock { kubectl port-forward service/payment-service 8085:8085 -n ecommerce }
Start-Job -ScriptBlock { kubectl port-forward service/api-gateway 8080:8080 -n ecommerce }
Start-Job -ScriptBlock { kubectl port-forward service/config-server 8888:8888 -n ecommerce }
Start-Job -ScriptBlock { kubectl port-forward service/eureka-server 8761:8761 -n ecommerce }

Write-Host "All port forwards started. Use Get-Job to see status, Stop-Job to stop."
```

---

## ⏱️ Sleep/Wait Commands

### PowerShell Sleep Commands

**Sleep for seconds:**
```powershell
Start-Sleep -Seconds 10
Start-Sleep -Seconds 30
Start-Sleep -Seconds 60
```

**Sleep for milliseconds:**
```powershell
Start-Sleep -Milliseconds 500
Start-Sleep -Milliseconds 1000
```

### Bash Sleep Commands

**Sleep for seconds:**
```bash
sleep 10
sleep 30
sleep 60
```

**Sleep for minutes:**
```bash
sleep 60  # 1 minute
sleep 120  # 2 minutes
```

### Wait for Service to be Ready

**PowerShell - Wait for HTTP endpoint:**
```powershell
$maxAttempts = 30
$attempt = 0
$url = "http://localhost:8081/actuator/health"

while ($attempt -lt $maxAttempts) {
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Host "Service is ready!"
            break
        }
    } catch {
        $attempt++
        Write-Host "Waiting for service... ($attempt/$maxAttempts)"
        Start-Sleep -Seconds 2
    }
}
```

**Bash - Wait for HTTP endpoint:**
```bash
until curl -f http://localhost:8081/actuator/health; do
    echo "Waiting for service..."
    sleep 2
done
```

### Wait for Kubernetes Pod to be Ready

**PowerShell:**
```powershell
kubectl wait --for=condition=ready pod -l app=user-service -n ecommerce --timeout=300s
```

**Bash:**
```bash
kubectl wait --for=condition=ready pod -l app=user-service -n ecommerce --timeout=300s
```

**Wait for all pods:**
```bash
kubectl wait --for=condition=ready pod --all -n ecommerce --timeout=300s
```

### Wait for Deployment to be Ready

**PowerShell:**
```powershell
kubectl rollout status deployment/user-service -n ecommerce --timeout=300s
```

**Bash:**
```bash
kubectl rollout status deployment/user-service -n ecommerce --timeout=300s
```

### Retry Commands

**PowerShell retry function:**
```powershell
function Retry-Command {
    param(
        [scriptblock]$Command,
        [int]$MaxAttempts = 5,
        [int]$DelaySeconds = 2
    )
    
    $attempt = 1
    while ($attempt -le $MaxAttempts) {
        try {
            & $Command
            return
        } catch {
            if ($attempt -eq $MaxAttempts) {
                throw
            }
            Write-Host "Attempt $attempt failed. Retrying in $DelaySeconds seconds..."
            Start-Sleep -Seconds $DelaySeconds
            $attempt++
        }
    }
}

# Usage
Retry-Command -Command { kubectl get pods -n ecommerce } -MaxAttempts 5
```

---

## 📝 Quick Reference

### Most Used Commands

**Check all pods:**
```bash
kubectl get pods -n ecommerce
```

**View logs:**
```bash
kubectl logs -f <pod-name> -n ecommerce
```

**Port forward:**
```bash
kubectl port-forward service/user-service 8081:8081 -n ecommerce
```

**Test health:**
```powershell
.\test-health-endpoints.ps1
```

**Run API tests:**
```powershell
.\run-all-api-tests.ps1
```

**Check services:**
```bash
kubectl get svc -n ecommerce
```

**Restart deployment:**
```bash
kubectl rollout restart deployment/user-service -n ecommerce
```

---

## 🎯 Common Workflows

### 1. Deploy to Kubernetes

```bash
# 1. Start minikube
minikube start

# 2. Configure Docker
minikube docker-env | Invoke-Expression

# 3. Build and load images
.\k8s\build-and-load-images.ps1

# 4. Apply deployments
kubectl apply -f k8s/ -n ecommerce

# 5. Wait for pods
kubectl wait --for=condition=ready pod --all -n ecommerce --timeout=300s

# 6. Port forward services
kubectl port-forward service/api-gateway 8080:8080 -n ecommerce
```

### 2. Test All Services

```powershell
# 1. Port forward all services (run in separate terminals)
kubectl port-forward service/user-service 8081:8081 -n ecommerce
kubectl port-forward service/product-service 8082:8082 -n ecommerce
# ... etc

# 2. Test health
.\test-health-endpoints.ps1

# 3. Test APIs
.\run-all-api-tests.ps1

# 4. Test Swagger
.\test-swagger-urls.ps1
```

### 3. Troubleshoot Issues

```bash
# 1. Check pod status
kubectl get pods -n ecommerce

# 2. Check logs
kubectl logs <pod-name> -n ecommerce

# 3. Describe pod
kubectl describe pod <pod-name> -n ecommerce

# 4. Check events
kubectl get events -n ecommerce --sort-by='.lastTimestamp'

# 5. Check service endpoints
kubectl get endpoints -n ecommerce
```

---

**Last Updated:** $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

**For more information, see:**
- `KUBERNETES_DEPLOYMENT_GUIDE.md`
- `OBSERVABILITY_GUIDE.md`
- `DEPLOYMENT.md`
- `PRODUCTION_SETUP.md`

