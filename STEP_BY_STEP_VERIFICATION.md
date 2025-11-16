# Step-by-Step Verification Guide

## Current Status Summary

Based on the verification script, here's what we found:
- ✅ **Docker Compose**: All services running (18 containers)
- ✅ **Health Endpoints**: 8/8 services healthy
- ✅ **GitHub Repository**: Connected
- ⚠️ **Kubernetes**: Not configured yet

---

## Part 1: Verify Docker Compose (Already Working ✅)

### Step 1.1: Check Current Status
```powershell
# Run the verification script
powershell -ExecutionPolicy Bypass -File .\verify-setup.ps1
```

**Expected**: All 8 health endpoints showing `[OK]`

### Step 1.2: Test Individual Services
```powershell
# Test each service manually
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8888/actuator/health  # Config Server
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Product Service
curl http://localhost:8083/actuator/health  # Order Service
curl http://localhost:8084/actuator/health  # Inventory Service
curl http://localhost:8085/actuator/health  # Payment Service
```

### Step 1.3: View Service Logs (if needed)
```powershell
docker compose logs user-service --tail=50
docker compose logs product-service --tail=50
```

---

## Part 2: Verify CI/CD Pipeline

### Step 2.1: Check GitHub Actions Workflow Files
```powershell
# Verify workflow files exist
Get-ChildItem .github\workflows\
```

**Expected**: Should see `ci.yml` and `cd.yml`

### Step 2.2: Push Changes to Trigger CI
```powershell
# Check current status
git status

# Add all new files
git add .

# Commit changes
git commit -m "Add production setup: Docker Compose healthchecks, K8s manifests, CI/CD"

# Push to trigger CI/CD
git push origin main
```

### Step 2.3: Monitor CI Pipeline
1. **Go to GitHub**: https://github.com/sharanya19/ecommerce-microservices
2. **Click "Actions" tab** (top menu)
3. **Watch the workflow run**:
   - You should see "CI Pipeline" running
   - Click on it to see detailed steps
   - Each step should show:
     - ✅ Checkout code
     - ✅ Set up JDK 17
     - ✅ Build with Gradle
     - ✅ Run tests
     - ✅ Build Docker images
     - ✅ Security scan
     - ✅ Push images (if on main branch)

### Step 2.4: Check Docker Images in Registry
1. **Go to**: https://github.com/sharanya19/ecommerce-microservices
2. **Click "Packages"** (right sidebar)
3. **You should see**:
   - `ecommerce-user-service`
   - `ecommerce-product-service`
   - `ecommerce-order-service`
   - `ecommerce-inventory-service`
   - `ecommerce-payment-service`
   - `ecommerce-api-gateway`

### Step 2.5: Verify CD Pipeline (Deployment)
The CD pipeline runs automatically when you push to `main` branch. To check:
1. **Go to Actions tab**
2. **Look for "CD Pipeline"**
3. **Click on it** to see deployment steps

**Note**: CD pipeline requires Kubernetes cluster to be set up first (see Part 3)

---

## Part 3: Set Up and Verify Kubernetes

### Step 3.1: Choose Your Kubernetes Setup

#### Option A: Docker Desktop Kubernetes (Easiest)
1. **Open Docker Desktop**
2. **Go to Settings** (gear icon)
3. **Click "Kubernetes"** in left menu
4. **Check "Enable Kubernetes"**
5. **Click "Apply & Restart"**
6. **Wait for Kubernetes to start** (green indicator)

#### Option B: Minikube (Local Development)
```powershell
# Install minikube (if not installed)
# Download from: https://minikube.sigs.k8s.io/docs/start/

# Start minikube
minikube start

# Verify
kubectl cluster-info
```

#### Option C: Kind (Kubernetes in Docker)
```powershell
# Install kind (if not installed)
# Download from: https://kind.sigs.k8s.io/docs/user/quick-start/

# Create cluster
kind create cluster --name ecommerce

# Verify
kubectl cluster-info
```

### Step 3.2: Verify Kubernetes Connection
```powershell
# Check cluster info
kubectl cluster-info

# Check nodes
kubectl get nodes

# Expected: Should show your cluster nodes
```

### Step 3.3: Create Namespace
```powershell
# Apply namespace
kubectl apply -f k8s/namespace.yaml

# Verify
kubectl get namespace ecommerce
```

**Expected**: Namespace should be created

### Step 3.4: Create Secrets and ConfigMaps
```powershell
# Create secrets
kubectl apply -f k8s/secrets.yaml

# Create configmaps
kubectl apply -f k8s/configmaps.yaml

# Verify
kubectl get secrets -n ecommerce
kubectl get configmaps -n ecommerce
```

**Expected**: Should see `ecommerce-secrets` and service configmaps

### Step 3.5: Build and Load Docker Images (for local K8s)

#### If using Minikube:
```powershell
# Set Docker environment to minikube
minikube docker-env | Invoke-Expression

# Build images
docker compose build

# Or load existing images
minikube image load ecommerce-microservices-user-service:latest
minikube image load ecommerce-microservices-product-service:latest
# etc.
```

#### If using Docker Desktop or Kind:
```powershell
# Images are already available, just update deployment files
# Edit k8s/*-deployment.yaml files to use local image names
```

### Step 3.6: Update Image References in Deployments

For local testing, you need to update image names in deployment files:

```powershell
# Update user-service deployment
(Get-Content k8s/user-service-deployment.yaml) -replace 'image: ecommerce/user-service:1.0.0', 'image: ecommerce-microservices-user-service:latest' | Set-Content k8s/user-service-deployment.yaml

# Repeat for other services or use a script
```

### Step 3.7: Deploy Infrastructure Services
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
```

### Step 3.8: Deploy Microservices
```powershell
# Deploy all services
kubectl apply -f k8s/user-service-deployment.yaml
kubectl apply -f k8s/product-service-deployment.yaml
kubectl apply -f k8s/order-service-deployment.yaml
kubectl apply -f k8s/inventory-service-deployment.yaml
kubectl apply -f k8s/payment-service-deployment.yaml
kubectl apply -f k8s/api-gateway-deployment.yaml
```

### Step 3.9: Verify Kubernetes Deployments
```powershell
# Check pods
kubectl get pods -n ecommerce

# Expected: All pods should be in "Running" state

# Check services
kubectl get svc -n ecommerce

# Check deployments
kubectl get deployments -n ecommerce

# Check HPA
kubectl get hpa -n ecommerce

# Check PDB
kubectl get pdb -n ecommerce
```

### Step 3.10: Check Pod Logs
```powershell
# Get pod names
kubectl get pods -n ecommerce

# View logs for a specific pod
kubectl logs <pod-name> -n ecommerce

# Example:
kubectl logs user-service-7d8f9c4b5-abc123 -n ecommerce
```

### Step 3.11: Port Forward for Testing
```powershell
# Port forward to access services locally
kubectl port-forward svc/user-service 8081:8081 -n ecommerce

# In another terminal, test
curl http://localhost:8081/actuator/health
```

### Step 3.12: Run Verification Script Again
```powershell
powershell -ExecutionPolicy Bypass -File .\verify-setup.ps1
```

**Expected**: Kubernetes section should now show `[OK]` status

---

## Part 4: Comprehensive Health Check

### Step 4.1: Run Automated Health Test
```powershell
# Run health endpoint test
powershell -ExecutionPolicy Bypass -File .\test-health-endpoints.ps1
```

**Expected**: All 10 endpoints should show `[PASS]`

### Step 4.2: Test API Endpoints
```powershell
# Test User Service Registration
$body = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
    role = "CUSTOMER"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/users/register" -Method POST -ContentType "application/json" -Body $body

# Test Product Service
Invoke-RestMethod -Uri "http://localhost:8082/products" -Method GET

# Test through API Gateway
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET
```

### Step 4.3: Check Metrics
```powershell
# Check Prometheus metrics
curl http://localhost:9090

# Check service metrics
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8082/actuator/prometheus
```

### Step 4.4: Check Grafana Dashboards
1. **Open**: http://localhost:3000
2. **Login**: admin / admin
3. **Check dashboards**:
   - E-commerce Overview
   - Business Metrics

---

## Part 5: Verify CI/CD End-to-End

### Step 5.1: Make a Code Change
```powershell
# Make a small change to trigger CI
# Edit any file, for example:
Add-Content -Path "test.txt" -Value "CI/CD test"

# Commit and push
git add test.txt
git commit -m "Test CI/CD pipeline"
git push origin main
```

### Step 5.2: Monitor CI Pipeline
1. **Go to**: https://github.com/sharanya19/ecommerce-microservices/actions
2. **Click on the running workflow**
3. **Watch each step**:
   - ✅ Checkout
   - ✅ Build
   - ✅ Test
   - ✅ Build Docker images
   - ✅ Push to registry

### Step 5.3: Verify Images Updated
1. **Go to Packages**: https://github.com/sharanya19/ecommerce-microservices/packages
2. **Check image tags** - should see new SHA-based tags

### Step 5.4: Monitor CD Pipeline (if K8s is set up)
1. **Go to Actions tab**
2. **Click on CD Pipeline**
3. **Watch deployment**:
   - ✅ Kubernetes setup
   - ✅ Deploy secrets/configmaps
   - ✅ Deploy services
   - ✅ Health checks

---

## Quick Reference Commands

### Docker Compose
```powershell
docker compose ps                    # List services
docker compose logs <service>         # View logs
docker compose restart <service>      # Restart service
docker compose up -d                  # Start all services
docker compose down                   # Stop all services
```

### Kubernetes
```powershell
kubectl get all -n ecommerce          # Get all resources
kubectl describe pod <name> -n ecommerce  # Pod details
kubectl logs <pod-name> -n ecommerce  # View logs
kubectl port-forward svc/<service> <port> -n ecommerce  # Port forward
kubectl rollout restart deployment/<name> -n ecommerce  # Restart deployment
```

### Health Checks
```powershell
.\verify-setup.ps1                    # Full verification
.\test-health-endpoints.ps1            # Health endpoint test
```

---

## Troubleshooting

### Issue: CI Pipeline Fails
- **Check**: GitHub Actions logs
- **Common fixes**:
  - Ensure Java 17 is available
  - Check Gradle build errors
  - Verify Docker build context

### Issue: Kubernetes Pods Not Starting
- **Check**: `kubectl describe pod <name> -n ecommerce`
- **Common fixes**:
  - Verify image names are correct
  - Check resource limits vs node capacity
  - Verify secrets/configmaps exist

### Issue: Health Checks Failing
- **Check**: Service logs
- **Common fixes**:
  - Verify dependencies are running
  - Check database connections
  - Verify network connectivity

---

## Success Criteria Checklist

- [ ] Docker Compose: All services running
- [ ] Health Endpoints: 8/8 services healthy
- [ ] CI Pipeline: Builds and pushes images successfully
- [ ] CD Pipeline: Deploys to Kubernetes (if K8s set up)
- [ ] Kubernetes: All pods in "Running" state
- [ ] API Endpoints: Can register users, create products, etc.
- [ ] Metrics: Prometheus scraping metrics
- [ ] Dashboards: Grafana showing data

---

## Next Steps After Verification

1. **Monitor**: Set up alerts in Prometheus
2. **Scale**: Test HPA by generating load
3. **Security**: Review and update secrets
4. **Backup**: Set up database backups
5. **Documentation**: Create runbooks for your team

---

## Need Help?

- Review `PRODUCTION_SETUP.md` for detailed configuration
- Check service logs for specific errors
- Review GitHub Actions logs for CI/CD issues
- Verify all prerequisites are installed correctly

