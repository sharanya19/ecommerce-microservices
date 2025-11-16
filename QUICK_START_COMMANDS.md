# Quick Start Commands - How to Run the Application

## 🎯 Choose Your Deployment Method

You have **two options** to run this application:

1. **🐳 Docker Compose** (Recommended for Development) - **EASIER**
2. **☸️ Kubernetes** (For Production/Advanced Users) - **MORE COMPLEX**

---

## 🐳 Option 1: Docker Compose (EASIEST - Recommended)

### Prerequisites
- Docker Desktop installed and running
- Ports 3306, 6379, 8761, 8888, 8080-8085 available

### Step-by-Step Commands

#### 1. Navigate to Project Directory
```powershell
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices
```

#### 2. Start All Services (One Command!)
```powershell
docker compose up -d --build
```

**That's it!** This single command will:
- Build all Docker images
- Start all services (MySQL, Redis, Kafka, Eureka, Config Server, all microservices)
- Run everything in the background

#### 3. Check Status
```powershell
# See all running containers
docker compose ps

# View logs
docker compose logs -f
```

#### 4. Verify Services Are Running

**Eureka Dashboard:**
- Open browser: http://localhost:8761
- Should show all registered services

**API Gateway:**
- Health: http://localhost:8080/actuator/health
- Test: http://localhost:8080/api/users

**All Services Health:**
```powershell
.\test-health-endpoints.ps1
```

#### 5. Stop Services (When Done)
```powershell
# Stop all services
docker compose down

# Stop and remove all data
docker compose down -v
```

### Alternative: Start Services in Stages

If you want more control:

```powershell
# Step 1: Start infrastructure first
docker compose up -d mysql redis zookeeper kafka

# Wait 30-60 seconds, then:

# Step 2: Start infrastructure services
docker compose up -d eureka-server config-server

# Wait 30 seconds, then:

# Step 3: Start all microservices
docker compose up -d --build
```

---

## ☸️ Option 2: Kubernetes (For Production/Advanced)

### Prerequisites
- Minikube installed OR Kubernetes cluster running
- kubectl installed
- Docker installed

### Step-by-Step Commands

#### 1. Start Minikube (If using Minikube)
```powershell
minikube start
```

#### 2. Configure Docker for Minikube
```powershell
minikube docker-env | Invoke-Expression
```

#### 3. Build and Load Images
```powershell
# Option A: Use the automated script
.\k8s\build-and-load-images.ps1

# Option B: Build manually
docker build -f config-server/Dockerfile -t ecommerce/config-server:1.0.0 .
docker build -f eureka-server/Dockerfile -t ecommerce/eureka-server:1.0.0 .
docker build -f api-gateway/Dockerfile -t ecommerce/api-gateway:1.0.0 .
docker build -f user-service/Dockerfile -t ecommerce/user-service:1.0.0 .
docker build -f product-service/Dockerfile -t ecommerce/product-service:1.0.0 .
docker build -f inventory-service/Dockerfile -t ecommerce/inventory-service:1.0.0 .
docker build -f order-service/Dockerfile -t ecommerce/order-service:1.0.0 .
docker build -f payment-service/Dockerfile -t ecommerce/payment-service:1.0.0 .

# Load images into minikube
minikube image load ecommerce/config-server:1.0.0
minikube image load ecommerce/eureka-server:1.0.0
minikube image load ecommerce/api-gateway:1.0.0
minikube image load ecommerce/user-service:1.0.0
minikube image load ecommerce/product-service:1.0.0
minikube image load ecommerce/inventory-service:1.0.0
minikube image load ecommerce/order-service:1.0.0
minikube image load ecommerce/payment-service:1.0.0
```

#### 4. Deploy to Kubernetes
```powershell
# Deploy everything
kubectl apply -f k8s/ -n ecommerce

# Or use the deployment script
.\k8s\deploy.ps1
```

#### 5. Wait for Pods to Start
```powershell
# Watch pods (press Ctrl+C to stop)
kubectl get pods -n ecommerce -w

# Or wait for all pods to be ready
kubectl wait --for=condition=ready pod --all -n ecommerce --timeout=300s
```

#### 6. Port Forward to Access Services
```powershell
# Open new terminal windows for each service:

# Terminal 1: API Gateway
kubectl port-forward service/api-gateway 8080:8080 -n ecommerce

# Terminal 2: User Service
kubectl port-forward service/user-service 8081:8081 -n ecommerce

# Terminal 3: Product Service
kubectl port-forward service/product-service 8082:8082 -n ecommerce

# Terminal 4: Eureka
kubectl port-forward service/eureka-server 8761:8761 -n ecommerce
```

#### 7. Verify Services
- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080/actuator/health

#### 8. Clean Up (When Done)
```powershell
# Delete all deployments
kubectl delete -f k8s/ -n ecommerce

# Or delete namespace (removes everything)
kubectl delete namespace ecommerce
```

---

## 📊 Comparison: Docker Compose vs Kubernetes

| Feature | Docker Compose | Kubernetes |
|---------|---------------|------------|
| **Ease of Use** | ⭐⭐⭐⭐⭐ Very Easy | ⭐⭐ More Complex |
| **Setup Time** | 2-5 minutes | 10-20 minutes |
| **Best For** | Development, Testing | Production, Scaling |
| **Commands** | 1-2 commands | 5-10 commands |
| **Port Forwarding** | Automatic | Manual (per service) |
| **Scaling** | Manual | Automatic |
| **Resource Usage** | Lower | Higher |

---

## 🎯 Recommendation

### For First Time / Development:
**Use Docker Compose** - Just run:
```powershell
docker compose up -d --build
```

### For Production / Learning Kubernetes:
**Use Kubernetes** - Follow the Kubernetes steps above

---

## ✅ Quick Verification Commands

### Docker Compose:
```powershell
# Check all services
docker compose ps

# Test health
.\test-health-endpoints.ps1

# Run API tests
.\run-all-api-tests.ps1
```

### Kubernetes:
```powershell
# Check all pods
kubectl get pods -n ecommerce

# Check services
kubectl get svc -n ecommerce

# Check endpoints
kubectl get endpoints -n ecommerce
```

---

## 🆘 Troubleshooting

### Docker Compose Issues:

**Services not starting:**
```powershell
# Check logs
docker compose logs <service-name>

# Restart
docker compose restart <service-name>

# Rebuild
docker compose up -d --build
```

**Port already in use:**
```powershell
# Find what's using the port
netstat -ano | findstr :8080

# Stop Docker Compose
docker compose down
```

### Kubernetes Issues:

**Pods not starting:**
```powershell
# Check pod status
kubectl describe pod <pod-name> -n ecommerce

# Check logs
kubectl logs <pod-name> -n ecommerce

# Check events
kubectl get events -n ecommerce --sort-by='.lastTimestamp'
```

**Images not found:**
```powershell
# Verify images in minikube
minikube ssh
docker images | grep ecommerce
exit

# Reload images
minikube image load ecommerce/user-service:1.0.0
```

---

## 📚 More Information

- **Complete Commands Guide**: See `COMPLETE_API_AND_COMMANDS_GUIDE.md`
- **Docker Details**: See `DEPLOYMENT.md`
- **Kubernetes Details**: See `KUBERNETES_DEPLOYMENT_GUIDE.md`
- **API Testing**: See `SWAGGER_API_TESTING_GUIDE.md`

---

## 🚀 TL;DR - Fastest Way to Start

**Just want to run it? Use Docker Compose:**

```powershell
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices
docker compose up -d --build
```

Wait 2-5 minutes, then:
- Open http://localhost:8761 (Eureka)
- Open http://localhost:8080/actuator/health (API Gateway)

**Done!** 🎉

