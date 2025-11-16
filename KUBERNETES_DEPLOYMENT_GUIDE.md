# Kubernetes Deployment Guide - Step by Step

## 📋 Prerequisites & Installation

### Option 1: Docker Desktop (Recommended for Windows/Mac)

**Installation Steps:**
1. Download Docker Desktop: https://www.docker.com/products/docker-desktop
2. Install Docker Desktop
3. Open Docker Desktop
4. Go to **Settings** → **Kubernetes**
5. Enable **"Enable Kubernetes"**
6. Click **"Apply & Restart"**
7. Wait for Kubernetes to start (green indicator)

**Verify Installation:**
```powershell
kubectl version --client
kubectl cluster-info
```

### Option 2: Minikube (Alternative)

**Installation Steps:**
1. Download minikube: https://minikube.sigs.k8s.io/docs/start/
2. Install kubectl: https://kubernetes.io/docs/tasks/tools/
3. Start minikube:
   ```powershell
   minikube start
   ```

**Verify Installation:**
```powershell
kubectl get nodes
```

### Option 3: Kind (Kubernetes in Docker)

**Installation Steps:**
1. Install kind: https://kind.sigs.k8s.io/docs/user/quick-start/
2. Create cluster:
   ```powershell
   kind create cluster --name ecommerce
   ```

---

## 🚀 Step-by-Step Deployment

### Step 1: Verify Kubernetes is Running

```powershell
# Check if kubectl is installed
kubectl version --client

# Check cluster status
kubectl cluster-info

# Check nodes
kubectl get nodes
```

**Expected Output:**
```
NAME             STATUS   ROLES           AGE   VERSION
docker-desktop   Ready    control-plane   5m    v1.28.0
```

---

### Step 2: Update Image References (IMPORTANT!)

Your Kubernetes manifests reference local images like `ecommerce/user-service:1.0.0`. You need to update them to use your GitHub Container Registry images.

**Update all deployment files:**

```powershell
# Navigate to k8s directory
cd k8s

# Update image references (replace with your actual registry)
# Format: ghcr.io/sharanya19/ecommerce-{service-name}:{tag}
```

**Quick Update Script (PowerShell):**
```powershell
# Update all deployment files
$imageTag = "latest"  # or use specific commit SHA
$registry = "ghcr.io/sharanya19/ecommerce"

$services = @("user-service", "product-service", "order-service", "inventory-service", "payment-service", "api-gateway")

foreach ($service in $services) {
    $file = "$service-deployment.yaml"
    if (Test-Path $file) {
        (Get-Content $file) -replace "image: ecommerce/$service:1.0.0", "image: $registry-$service`:$imageTag" | Set-Content $file
        Write-Host "Updated $file" -ForegroundColor Green
    }
}
```

**Or manually update each file:**
- Change `image: ecommerce/user-service:1.0.0` 
- To: `image: ghcr.io/sharanya19/ecommerce-user-service:latest`

---

### Step 3: Create Namespace

```powershell
kubectl apply -f k8s/namespace.yaml
```

**Verify:**
```powershell
kubectl get namespace ecommerce
```

---

### Step 4: Create Secrets

```powershell
kubectl apply -f k8s/secrets.yaml
```

**Verify:**
```powershell
kubectl get secrets -n ecommerce
```

**Note:** Update `secrets.yaml` with your actual secrets before applying!

---

### Step 5: Create ConfigMaps

```powershell
kubectl apply -f k8s/configmaps.yaml
```

**Verify:**
```powershell
kubectl get configmaps -n ecommerce
```

---

### Step 6: Deploy Infrastructure Services

**Deploy in order (dependencies first):**

```powershell
# 1. MySQL
kubectl apply -f k8s/mysql-deployment.yaml

# 2. Redis
kubectl apply -f k8s/redis-deployment.yaml

# 3. Kafka (if using)
kubectl apply -f k8s/kafka-deployment.yaml

# 4. Eureka Server
kubectl apply -f k8s/eureka-deployment.yaml

# 5. Config Server
kubectl apply -f k8s/config-server-deployment.yaml
```

**Wait for infrastructure to be ready:**
```powershell
# Check pod status
kubectl get pods -n ecommerce -w

# Wait until all infrastructure pods are "Running"
# Press Ctrl+C to stop watching
```

**Verify infrastructure:**
```powershell
kubectl get pods -n ecommerce
kubectl get services -n ecommerce
```

---

### Step 7: Deploy Microservices

**Deploy all microservices:**

```powershell
# Deploy all services
kubectl apply -f k8s/user-service-deployment.yaml
kubectl apply -f k8s/product-service-deployment.yaml
kubectl apply -f k8s/order-service-deployment.yaml
kubectl apply -f k8s/inventory-service-deployment.yaml
kubectl apply -f k8s/payment-service-deployment.yaml
kubectl apply -f k8s/api-gateway-deployment.yaml
```

**Or deploy all at once:**
```powershell
kubectl apply -f k8s/*-deployment.yaml
```

**Wait for services to be ready:**
```powershell
kubectl get pods -n ecommerce -w
```

---

### Step 8: Deploy Observability (Optional)

```powershell
# Deploy OpenTelemetry Collector
kubectl apply -f k8s/otel/otel-collector.yaml

# Deploy ServiceMonitors (if Prometheus Operator is installed)
kubectl apply -f k8s/servicemonitors.yaml
```

---

### Step 9: Deploy Ingress (Optional)

```powershell
# Install NGINX Ingress Controller first (if not installed)
# For Docker Desktop, it's usually pre-installed

# Deploy Ingress
kubectl apply -f k8s/ingress.yaml
```

**Note:** Update `ingress.yaml` with your domain name or use `localhost` for local testing.

---

## 🔍 Verification & Monitoring

### Check Pod Status

```powershell
# Get all pods
kubectl get pods -n ecommerce

# Get detailed pod information
kubectl get pods -n ecommerce -o wide

# Check pod logs
kubectl logs -n ecommerce <pod-name>

# Check logs for a specific service
kubectl logs -n ecommerce -l app=user-service --tail=50
```

### Check Services

```powershell
# Get all services
kubectl get services -n ecommerce

# Get service details
kubectl describe service user-service -n ecommerce
```

### Check Deployments

```powershell
# Get all deployments
kubectl get deployments -n ecommerce

# Check deployment status
kubectl describe deployment user-service -n ecommerce

# Check rollout status
kubectl rollout status deployment/user-service -n ecommerce
```

### Port Forwarding (Access Services Locally)

```powershell
# Port forward to access services
kubectl port-forward -n ecommerce service/user-service 8081:8081
kubectl port-forward -n ecommerce service/product-service 8082:8082
kubectl port-forward -n ecommerce service/order-service 8083:8083
kubectl port-forward -n ecommerce service/inventory-service 8084:8084
kubectl port-forward -n ecommerce service/payment-service 8085:8085
kubectl port-forward -n ecommerce service/api-gateway 8080:8080
```

**Then access:**
- User Service: http://localhost:8081
- Product Service: http://localhost:8082
- Order Service: http://localhost:8083
- Inventory Service: http://localhost:8084
- Payment Service: http://localhost:8085
- API Gateway: http://localhost:8080

---

## 🛠️ Troubleshooting

### Pods Not Starting

```powershell
# Check pod events
kubectl describe pod <pod-name> -n ecommerce

# Check pod logs
kubectl logs <pod-name> -n ecommerce

# Common issues:
# - Image pull errors: Check image name and registry access
# - CrashLoopBackOff: Check application logs
# - Pending: Check resource limits and node capacity
```

### Image Pull Errors

```powershell
# If using private registry (GHCR), create image pull secret:
kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username=<your-username> \
  --docker-password=<your-token> \
  --namespace=ecommerce

# Then update deployments to use the secret:
# Add to deployment spec:
# imagePullSecrets:
# - name: ghcr-secret
```

### Services Not Accessible

```powershell
# Check service endpoints
kubectl get endpoints -n ecommerce

# Check service selector matches pod labels
kubectl get pods -n ecommerce --show-labels
kubectl get service user-service -n ecommerce -o yaml
```

### Database Connection Issues

```powershell
# Check MySQL pod
kubectl get pods -n ecommerce | grep mysql
kubectl logs -n ecommerce <mysql-pod-name>

# Test MySQL connection from a pod
kubectl run -it --rm mysql-client --image=mysql:8.0 --restart=Never -- mysql -h mysql -uroot -prootpassword
```

---

## 📝 Quick Reference Commands

```powershell
# Get everything in namespace
kubectl get all -n ecommerce

# Delete a deployment
kubectl delete deployment user-service -n ecommerce

# Restart a deployment
kubectl rollout restart deployment/user-service -n ecommerce

# Scale a deployment
kubectl scale deployment user-service --replicas=3 -n ecommerce

# Get resource usage
kubectl top pods -n ecommerce
kubectl top nodes

# Delete everything in namespace
kubectl delete all --all -n ecommerce

# Delete namespace (removes everything)
kubectl delete namespace ecommerce
```

---

## 🎯 Deployment Checklist

- [ ] Kubernetes cluster running
- [ ] kubectl installed and configured
- [ ] Updated image references in deployment files
- [ ] Updated secrets.yaml with actual values
- [ ] Namespace created
- [ ] Secrets created
- [ ] ConfigMaps created
- [ ] Infrastructure services deployed (MySQL, Redis, Kafka, Eureka, Config Server)
- [ ] Infrastructure services running
- [ ] Microservices deployed
- [ ] Microservices running
- [ ] Services accessible via port-forward or ingress
- [ ] Health checks passing

---

## 🚀 Next Steps

1. **Set up Ingress** for external access
2. **Configure LoadBalancer** (if using cloud provider)
3. **Set up monitoring** with Prometheus and Grafana
4. **Configure autoscaling** (HPA is already in deployments)
5. **Set up CI/CD** to automatically deploy to Kubernetes

---

## 📚 Additional Resources

- Kubernetes Documentation: https://kubernetes.io/docs/
- kubectl Cheat Sheet: https://kubernetes.io/docs/reference/kubectl/cheatsheet/
- Docker Desktop Kubernetes: https://docs.docker.com/desktop/kubernetes/

---

**Need Help?** Check the troubleshooting section or review pod logs for specific errors.

