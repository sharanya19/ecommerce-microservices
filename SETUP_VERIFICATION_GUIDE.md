# Step-by-Step Setup Verification Guide

This guide walks you through verifying CI/CD, Kubernetes, and health checks for the E-commerce Microservices application.

## Prerequisites Checklist

Before starting, ensure you have:
- [ ] Docker Desktop installed and running
- [ ] kubectl installed
- [ ] A Kubernetes cluster (minikube, kind, or cloud cluster)
- [ ] Git configured
- [ ] GitHub repository access

---

## Part 1: Verify Docker Compose Setup

### Step 1.1: Check Docker is Running
```powershell
docker --version
docker ps
```

**Expected Output**: Docker version and running containers list

### Step 1.2: Start Docker Compose Services
```powershell
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices
docker compose up -d
```

**Expected Output**: All services starting

### Step 1.3: Verify Services are Running
```powershell
docker compose ps
```

**Expected Output**: All services showing "Up" status

### Step 1.4: Check Service Health
```powershell
# Run the verification script
powershell -ExecutionPolicy Bypass -File .\verify-setup.ps1

# Or manually check each service
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8888/actuator/health   # Config Server
curl http://localhost:8080/actuator/health   # API Gateway
curl http://localhost:8081/actuator/health   # User Service
curl http://localhost:8082/actuator/health  # Product Service
curl http://localhost:8083/actuator/health  # Order Service
curl http://localhost:8084/actuator/health  # Inventory Service
curl http://localhost:8085/actuator/health  # Payment Service
```

**Expected Output**: JSON responses with `"status":"UP"`

### Step 1.5: View Service Logs (if issues)
```powershell
docker compose logs user-service
docker compose logs product-service
# etc.
```

---

## Part 2: Verify Kubernetes Setup

### Step 2.1: Check kubectl Installation
```powershell
kubectl version --client
```

**Expected Output**: kubectl version information

### Step 2.2: Set Up Local Kubernetes Cluster (if needed)

#### Option A: Using Minikube
```powershell
# Install minikube (if not installed)
# Download from: https://minikube.sigs.k8s.io/docs/start/

# Start minikube
minikube start

# Verify cluster
kubectl cluster-info
```

#### Option B: Using Kind (Kubernetes in Docker)
```powershell
# Install kind (if not installed)
# Download from: https://kind.sigs.k8s.io/docs/user/quick-start/

# Create cluster
kind create cluster --name ecommerce

# Verify cluster
kubectl cluster-info
```

#### Option C: Using Docker Desktop Kubernetes
1. Open Docker Desktop
2. Go to Settings → Kubernetes
3. Enable Kubernetes
4. Click "Apply & Restart"

### Step 2.3: Verify Cluster Connection
```powershell
kubectl cluster-info
kubectl get nodes
```

**Expected Output**: Cluster information and node list

### Step 2.4: Create Namespace
```powershell
kubectl apply -f k8s/namespace.yaml
kubectl get namespace ecommerce
```

**Expected Output**: Namespace created and listed

### Step 2.5: Create Secrets and ConfigMaps
```powershell
# Create secrets
kubectl apply -f k8s/secrets.yaml

# Create configmaps
kubectl apply -f k8s/configmaps.yaml

# Verify
kubectl get secrets -n ecommerce
kubectl get configmaps -n ecommerce
```

**Expected Output**: Secrets and ConfigMaps listed

### Step 2.6: Deploy Infrastructure Services
```powershell
# Deploy in order
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/redis-deployment.yaml
kubectl apply -f k8s/kafka-deployment.yaml
kubectl apply -f k8s/eureka-deployment.yaml
kubectl apply -f k8s/config-server-deployment.yaml

# Wait for them to be ready
kubectl wait --for=condition=available --timeout=300s deployment/mysql -n ecommerce
kubectl wait --for=condition=available --timeout=300s deployment/redis -n ecommerce
# etc.
```

### Step 2.7: Deploy Microservices
```powershell
# Update image references first (if using local images)
# For local testing with minikube:
minikube image load ecommerce/user-service:1.0.0
minikube image load ecommerce/product-service:1.0.0
# etc.

# Or build images directly in minikube
eval $(minikube docker-env)
docker compose build

# Deploy services
kubectl apply -f k8s/user-service-deployment.yaml
kubectl apply -f k8s/product-service-deployment.yaml
kubectl apply -f k8s/order-service-deployment.yaml
kubectl apply -f k8s/inventory-service-deployment.yaml
kubectl apply -f k8s/payment-service-deployment.yaml
kubectl apply -f k8s/api-gateway-deployment.yaml
```

### Step 2.8: Verify Kubernetes Deployments
```powershell
# Check pods
kubectl get pods -n ecommerce

# Check services
kubectl get svc -n ecommerce

# Check deployments
kubectl get deployments -n ecommerce

# Check HPA
kubectl get hpa -n ecommerce

# Check PDB
kubectl get pdb -n ecommerce
```

**Expected Output**: All resources showing as "Running" or "Available"

### Step 2.9: Check Pod Logs
```powershell
# Get pod name
kubectl get pods -n ecommerce

# View logs
kubectl logs <pod-name> -n ecommerce

# Follow logs
kubectl logs -f <pod-name> -n ecommerce
```

### Step 2.10: Port Forward for Testing
```powershell
# Port forward to access services locally
kubectl port-forward svc/user-service 8081:8081 -n ecommerce
kubectl port-forward svc/product-service 8082:8082 -n ecommerce
# etc.

# In another terminal, test
curl http://localhost:8081/actuator/health
```

---

## Part 3: Verify CI/CD Pipeline

### Step 3.1: Check GitHub Repository
```powershell
# Verify you're in the right directory
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices

# Check Git remote
git remote -v
```

**Expected Output**: GitHub repository URL

### Step 3.2: Verify Workflow Files Exist
```powershell
# Check CI workflow
Get-Content .github\workflows\ci.yml

# Check CD workflow
Get-Content .github\workflows\cd.yml
```

### Step 3.3: Push Changes to Trigger CI
```powershell
# Make a small change (or just push existing changes)
git add .
git commit -m "Trigger CI/CD pipeline"
git push origin main
```

### Step 3.4: Check GitHub Actions
1. Go to your GitHub repository
2. Click on the **"Actions"** tab
3. You should see:
   - **CI Pipeline** running (on push/PR)
   - **CD Pipeline** running (on push to main)

### Step 3.5: Monitor CI Pipeline
In GitHub Actions:
- Click on the running workflow
- Watch each step execute:
  - ✅ Checkout code
  - ✅ Set up JDK 17
  - ✅ Build with Gradle
  - ✅ Run tests
  - ✅ Build Docker images
  - ✅ Security scan
  - ✅ Push images

**Expected**: All steps should pass (green checkmarks)

### Step 3.6: Monitor CD Pipeline
In GitHub Actions:
- Click on the CD workflow
- Watch deployment steps:
  - ✅ Kubernetes setup
  - ✅ Create namespace
  - ✅ Deploy secrets/configmaps
  - ✅ Deploy infrastructure
  - ✅ Deploy services
  - ✅ Health checks

**Expected**: All steps should pass

### Step 3.7: Verify Images in Registry
1. Go to GitHub repository
2. Click **"Packages"** (on the right side)
3. You should see Docker images:
   - `ecommerce-user-service`
   - `ecommerce-product-service`
   - etc.

---

## Part 4: Comprehensive Health Check

### Step 4.1: Run Automated Verification
```powershell
powershell -ExecutionPolicy Bypass -File .\verify-setup.ps1
```

This script checks:
- ✅ Prerequisites (Docker, kubectl, Git)
- ✅ Docker Compose services
- ✅ Kubernetes cluster
- ✅ Health endpoints
- ✅ GitHub Actions status

### Step 4.2: Manual Health Check Script
```powershell
# Run the health endpoint test
powershell -ExecutionPolicy Bypass -File .\test-health-endpoints.ps1
```

**Expected Output**: All 10 endpoints showing `[PASS]`

### Step 4.3: Test API Endpoints
```powershell
# Test User Service
curl http://localhost:8081/users/register -Method POST -ContentType "application/json" -Body '{"username":"test","email":"test@test.com","password":"test123","firstName":"Test","lastName":"User","role":"CUSTOMER"}'

# Test Product Service
curl http://localhost:8082/products

# Test through API Gateway
curl http://localhost:8080/api/users
```

---

## Part 5: Troubleshooting

### Issue: Docker Compose Services Not Starting
```powershell
# Check logs
docker compose logs

# Restart services
docker compose down
docker compose up -d

# Check resource usage
docker stats
```

### Issue: Kubernetes Pods Not Starting
```powershell
# Describe pod for details
kubectl describe pod <pod-name> -n ecommerce

# Check events
kubectl get events -n ecommerce --sort-by='.lastTimestamp'

# Check resource quotas
kubectl describe quota -n ecommerce
```

### Issue: CI/CD Pipeline Failing
1. Check GitHub Actions logs
2. Common issues:
   - **Build fails**: Check Java version, Gradle issues
   - **Docker build fails**: Check Dockerfile syntax
   - **Push fails**: Check registry permissions
   - **Deploy fails**: Check kubectl configuration

### Issue: Health Checks Failing
```powershell
# Check if services are actually running
docker compose ps

# Check service logs
docker compose logs <service-name>

# Check if ports are available
netstat -an | findstr "8081"
```

---

## Quick Verification Commands

### All-in-One Check
```powershell
# Docker Compose
docker compose ps
docker compose logs --tail=20

# Kubernetes
kubectl get all -n ecommerce

# Health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8085/actuator/health

# GitHub Actions
# Visit: https://github.com/<your-username>/<repo-name>/actions
```

---

## Success Criteria

✅ **Docker Compose**: All 15+ services running and healthy  
✅ **Kubernetes**: All pods in "Running" state, services accessible  
✅ **CI Pipeline**: Builds, tests, and pushes images successfully  
✅ **CD Pipeline**: Deploys to Kubernetes successfully  
✅ **Health Checks**: All endpoints return 200 OK  

---

## Next Steps After Verification

1. **Monitor**: Set up Prometheus/Grafana dashboards
2. **Scale**: Test HPA by generating load
3. **Security**: Review and update secrets
4. **Backup**: Set up database backups
5. **Documentation**: Update runbooks for your team

---

## Need Help?

- Check `PRODUCTION_SETUP.md` for detailed configuration
- Review service logs for specific errors
- Check GitHub Actions logs for CI/CD issues
- Verify all prerequisites are installed correctly

