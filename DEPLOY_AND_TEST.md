# Deploy and Test - Step by Step Guide

## ✅ Your Cluster Status
- **Cluster**: minikube
- **Status**: Ready ✅
- **Version**: v1.34.0
- **All system pods**: Running ✅

## 🚀 Step 1: Deploy the Application

### Option A: Automated Deployment (Recommended)

```powershell
# Navigate to k8s directory
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices\k8s

# Run deployment script
.\deploy.ps1 -ImageTag latest
```

### Option B: Manual Deployment

```powershell
# Navigate to k8s directory
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices\k8s

# 1. Create namespace
kubectl apply -f namespace.yaml

# 2. Create secrets
kubectl apply -f secrets.yaml

# 3. Create configmaps
kubectl apply -f configmaps.yaml

# 4. Deploy infrastructure (in order)
kubectl apply -f mysql-deployment.yaml
kubectl apply -f redis-deployment.yaml
kubectl apply -f kafka-deployment.yaml
kubectl apply -f eureka-deployment.yaml
kubectl apply -f config-server-deployment.yaml

# 5. Wait for infrastructure to be ready (2-3 minutes)
kubectl get pods -n ecommerce -w
# Press Ctrl+C when infrastructure pods are Running

# 6. Deploy microservices
kubectl apply -f user-service-deployment.yaml
kubectl apply -f product-service-deployment.yaml
kubectl apply -f order-service-deployment.yaml
kubectl apply -f inventory-service-deployment.yaml
kubectl apply -f payment-service-deployment.yaml
kubectl apply -f api-gateway-deployment.yaml
```

## 📊 Step 2: Monitor Deployment

```powershell
# Watch all pods starting up
kubectl get pods -n ecommerce -w

# Or check status periodically
kubectl get pods -n ecommerce

# Check specific service
kubectl get pods -n ecommerce | grep user-service
```

**Expected Output:**
```
NAME                              READY   STATUS    RESTARTS   AGE
mysql-xxx                         1/1     Running   0          2m
redis-xxx                         1/1     Running   0          2m
kafka-xxx                         1/1     Running   0          2m
eureka-server-xxx                 1/1     Running   0          2m
config-server-xxx                 1/1     Running   0          2m
user-service-xxx                  1/1     Running   0          1m
product-service-xxx               1/1     Running   0          1m
...
```

**Wait until all pods show:**
- STATUS: `Running`
- READY: `1/1` or `2/2`

## 🌐 Step 3: Access Services (Port Forwarding)

Open **multiple PowerShell windows** (one for each service):

### Terminal 1: User Service
```powershell
kubectl port-forward -n ecommerce service/user-service 8081:8081
```

### Terminal 2: Product Service
```powershell
kubectl port-forward -n ecommerce service/product-service 8082:8082
```

### Terminal 3: Order Service
```powershell
kubectl port-forward -n ecommerce service/order-service 8083:8083
```

### Terminal 4: Inventory Service
```powershell
kubectl port-forward -n ecommerce service/inventory-service 8084:8084
```

### Terminal 5: Payment Service
```powershell
kubectl port-forward -n ecommerce service/payment-service 8085:8085
```

### Terminal 6: API Gateway
```powershell
kubectl port-forward -n ecommerce service/api-gateway 8080:8080
```

## 🧪 Step 4: Test the Services

### Test 1: Health Checks

Open a **new PowerShell window** and test each service:

```powershell
# User Service
curl http://localhost:8081/actuator/health

# Product Service
curl http://localhost:8082/actuator/health

# Order Service
curl http://localhost:8083/actuator/health

# Inventory Service
curl http://localhost:8084/actuator/health

# Payment Service
curl http://localhost:8085/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

**Expected Response:**
```json
{"status":"UP"}
```

### Test 2: User Service APIs

```powershell
# Register a new user
curl -X POST http://localhost:8081/users/register `
  -H "Content-Type: application/json" `
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "role": "CUSTOMER"
  }'

# Login
curl -X POST http://localhost:8081/users/login `
  -H "Content-Type: application/json" `
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Get all users (use token from login)
curl http://localhost:8081/users `
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Test 3: Product Service APIs

```powershell
# Get all products
curl http://localhost:8082/products

# Create a product
curl -X POST http://localhost:8082/products `
  -H "Content-Type: application/json" `
  -d '{
    "name": "Test Product",
    "description": "Test Description",
    "price": 100.00,
    "stock": 50,
    "category": "Electronics",
    "sku": "TEST-001",
    "status": "ACTIVE"
  }'
```

### Test 4: Swagger UI

Open in browser:
- User Service: http://localhost:8081/swagger-ui/index.html
- Product Service: http://localhost:8082/swagger-ui/index.html
- Order Service: http://localhost:8083/swagger-ui/index.html
- Inventory Service: http://localhost:8084/swagger-ui/index.html
- Payment Service: http://localhost:8085/swagger-ui/index.html
- API Gateway: http://localhost:8080/swagger-ui/index.html

## 🔍 Step 5: Verify Everything is Working

### Check Pod Logs

```powershell
# Check user-service logs
kubectl logs -n ecommerce -l app=user-service --tail=50

# Check product-service logs
kubectl logs -n ecommerce -l app=product-service --tail=50

# Check all services
kubectl get pods -n ecommerce
```

### Check Services

```powershell
# List all services
kubectl get services -n ecommerce

# Get service details
kubectl describe service user-service -n ecommerce
```

### Check Deployments

```powershell
# List all deployments
kubectl get deployments -n ecommerce

# Check deployment status
kubectl rollout status deployment/user-service -n ecommerce
```

## 🐛 Troubleshooting

### Pods Not Starting

```powershell
# Check pod status
kubectl describe pod <pod-name> -n ecommerce

# Check logs
kubectl logs <pod-name> -n ecommerce

# Common issues:
# - ImagePullBackOff: Image not found or registry access issue
# - CrashLoopBackOff: Application error, check logs
# - Pending: Resource constraints or node issues
```

### Image Pull Errors

If you see `ImagePullBackOff`:
1. Check if images exist in your registry
2. Update image references in deployment files
3. For minikube, you might need to load images:
   ```powershell
   # Load Docker images into minikube
   minikube image load ghcr.io/sharanya19/ecommerce-user-service:latest
   ```

### Port Forward Not Working

```powershell
# Check if service exists
kubectl get services -n ecommerce

# Check if pods are running
kubectl get pods -n ecommerce

# Try different port
kubectl port-forward -n ecommerce service/user-service 8081:8081 --address 0.0.0.0
```

### Database Connection Issues

```powershell
# Check MySQL pod
kubectl get pods -n ecommerce | grep mysql
kubectl logs -n ecommerce <mysql-pod-name>

# Test MySQL connection
kubectl run -it --rm mysql-client --image=mysql:8.0 --restart=Never -- mysql -h mysql -uroot -prootpassword
```

## 📊 Quick Status Commands

```powershell
# Get everything in namespace
kubectl get all -n ecommerce

# Get pods with wide output
kubectl get pods -n ecommerce -o wide

# Get resource usage
kubectl top pods -n ecommerce

# Get events
kubectl get events -n ecommerce --sort-by='.lastTimestamp'
```

## ✅ Success Criteria

Your deployment is successful when:
- ✅ All pods show `Running` status
- ✅ All pods show `READY 1/1` or `2/2`
- ✅ Health endpoints return `{"status":"UP"}`
- ✅ You can access Swagger UI
- ✅ You can register users and create products
- ✅ No errors in pod logs

## 🎯 Next Steps After Testing

1. **Set up Ingress** for external access
2. **Configure monitoring** with Prometheus/Grafana
3. **Set up autoscaling** (HPA is already configured)
4. **Test load balancing** (multiple replicas)
5. **Test rolling updates**

---

**Ready to deploy?** Run: `cd k8s && .\deploy.ps1`

