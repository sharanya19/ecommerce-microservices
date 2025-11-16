# Fixing Kubernetes Deployment Issues

## Issues Identified

1. **ImagePullBackOff**: Images from `ghcr.io/sharanya19/ecommerce-*:latest` don't exist or require authentication
2. **InvalidImageName**: Some deployments had malformed image names
3. **Local Images**: `ecommerce/*:1.0.0` images need to be built locally for minikube

## Solution Applied

All deployment files have been updated to:
- Use local image names: `ecommerce/<service>:1.0.0`
- Set `imagePullPolicy: Never` to use local images only

## Steps to Fix

### 1. Build and Load Images into Minikube

```powershell
# Configure Docker to use minikube's daemon
minikube docker-env | Invoke-Expression

# Build and load all images
.\k8s\build-and-load-images.ps1
```

Or build manually:

```powershell
# Set minikube Docker environment
minikube docker-env | Invoke-Expression

# Build each service (from project root)
docker build -f api-gateway/Dockerfile -t ecommerce/api-gateway:1.0.0 .
docker build -f user-service/Dockerfile -t ecommerce/user-service:1.0.0 .
docker build -f product-service/Dockerfile -t ecommerce/product-service:1.0.0 .
docker build -f inventory-service/Dockerfile -t ecommerce/inventory-service:1.0.0 .
docker build -f order-service/Dockerfile -t ecommerce/order-service:1.0.0 .
docker build -f payment-service/Dockerfile -t ecommerce/payment-service:1.0.0 .
docker build -f config-server/Dockerfile -t ecommerce/config-server:1.0.0 .
docker build -f eureka-server/Dockerfile -t ecommerce/eureka-server:1.0.0 .
```

### 2. Verify Images are Built

```powershell
# List images in minikube
minikube image ls | Select-String "ecommerce"
```

### 3. Redeploy Services

```powershell
# Apply all deployments
kubectl apply -f k8s/ -n ecommerce

# Watch pods come up
kubectl get pods -n ecommerce -w
```

### 4. Verify Deployment

```powershell
# Check pod status
kubectl get pods -n ecommerce

# Check services
kubectl get services -n ecommerce

# Check endpoints
kubectl get endpoints -n ecommerce
```

## Expected Results

After building images and redeploying:
- All pods should be in `Running` state
- Services should have endpoints
- Port-forwarding should work

## Troubleshooting

If pods still fail:
1. Check pod logs: `kubectl logs <pod-name> -n ecommerce`
2. Check pod events: `kubectl describe pod <pod-name> -n ecommerce`
3. Verify images exist: `minikube image ls`
4. Ensure minikube Docker daemon is being used: `minikube docker-env | Invoke-Expression`



