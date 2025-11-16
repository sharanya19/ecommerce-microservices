# Image Loading Instructions for Minikube

## Status

✅ **All Docker images have been built successfully:**
- ecommerce/config-server:1.0.0
- ecommerce/eureka-server:1.0.0
- ecommerce/api-gateway:1.0.0
- ecommerce/user-service:1.0.0
- ecommerce/product-service:1.0.0
- ecommerce/inventory-service:1.0.0
- ecommerce/order-service:1.0.0
- ecommerce/payment-service:1.0.0

✅ **All deployments have been created in Kubernetes**

⚠️ **Images need to be loaded into minikube's Docker daemon**

## Steps to Load Images

Since minikube is not in your PATH, you need to run these commands manually:

### Option 1: If minikube is installed but not in PATH

1. Find minikube executable (common locations):
   - `%USERPROFILE%\.minikube\bin\minikube.exe`
   - `%LOCALAPPDATA%\Programs\minikube\minikube.exe`
   - Or add minikube to your PATH

2. Configure Docker to use minikube's daemon:
   ```powershell
   minikube docker-env | Invoke-Expression
   ```

3. Load each image:
   ```powershell
   minikube image load ecommerce/config-server:1.0.0
   minikube image load ecommerce/eureka-server:1.0.0
   minikube image load ecommerce/api-gateway:1.0.0
   minikube image load ecommerce/user-service:1.0.0
   minikube image load ecommerce/product-service:1.0.0
   minikube image load ecommerce/inventory-service:1.0.0
   minikube image load ecommerce/order-service:1.0.0
   minikube image load ecommerce/payment-service:1.0.0
   ```

### Option 2: Using Docker Save/Load (Alternative)

If you can't access minikube directly, you can use this workaround:

```powershell
# Save images to tar files
docker save ecommerce/config-server:1.0.0 -o config-server.tar
docker save ecommerce/eureka-server:1.0.0 -o eureka-server.tar
docker save ecommerce/api-gateway:1.0.0 -o api-gateway.tar
docker save ecommerce/user-service:1.0.0 -o user-service.tar
docker save ecommerce/product-service:1.0.0 -o product-service.tar
docker save ecommerce/inventory-service:1.0.0 -o inventory-service.tar
docker save ecommerce/order-service:1.0.0 -o order-service.tar
docker save ecommerce/payment-service:1.0.0 -o payment-service.tar

# Then configure minikube docker-env and load
minikube docker-env | Invoke-Expression
docker load -i config-server.tar
docker load -i eureka-server.tar
docker load -i api-gateway.tar
docker load -i user-service.tar
docker load -i product-service.tar
docker load -i inventory-service.tar
docker load -i order-service.tar
docker load -i payment-service.tar
```

## Verify Images are Loaded

After loading images, verify they're available in minikube:

```powershell
# Configure minikube docker-env first
minikube docker-env | Invoke-Expression

# List images
docker images | Select-String "ecommerce"
```

## Check Pod Status

After loading images, check if pods are starting:

```powershell
kubectl get pods -n ecommerce
```

Pods should transition from `ErrImageNeverPull` to `Running` status.

## Troubleshooting

If pods still show `ErrImageNeverPull`:
1. Verify images are loaded: `minikube docker-env | Invoke-Expression ; docker images | Select-String "ecommerce"`
2. Check pod events: `kubectl describe pod <pod-name> -n ecommerce`
3. Ensure `imagePullPolicy: Never` is set in deployments (already done)



