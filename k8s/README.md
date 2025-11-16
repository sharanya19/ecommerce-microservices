# Kubernetes Deployment Files

This directory contains all Kubernetes manifests for deploying the ecommerce microservices.

## 📁 File Structure

```
k8s/
├── namespace.yaml                    # Namespace definition
├── secrets.yaml                      # Secrets (JWT, DB passwords, etc.)
├── configmaps.yaml                   # Service configurations
├── mysql-deployment.yaml             # MySQL database
├── redis-deployment.yaml              # Redis cache
├── kafka-deployment.yaml              # Kafka message broker
├── eureka-deployment.yaml             # Eureka service discovery
├── config-server-deployment.yaml      # Config server
├── user-service-deployment.yaml       # User microservice
├── product-service-deployment.yaml    # Product microservice
├── order-service-deployment.yaml      # Order microservice
├── inventory-service-deployment.yaml  # Inventory microservice
├── payment-service-deployment.yaml    # Payment microservice
├── api-gateway-deployment.yaml        # API Gateway
├── ingress.yaml                       # Ingress for external access
├── servicemonitors.yaml               # Prometheus ServiceMonitors
└── otel/
    ├── otel-collector.yaml            # OpenTelemetry Collector
    └── otel-collector-servicemonitor.yaml
```

## 🚀 Quick Start

### Option 1: Automated Deployment (Recommended)

```powershell
# Navigate to k8s directory
cd k8s

# Run deployment script
.\deploy.ps1 -ImageTag latest

# Or with specific tag
.\deploy.ps1 -ImageTag 7428eec
```

### Option 2: Manual Deployment

See [KUBERNETES_DEPLOYMENT_GUIDE.md](../KUBERNETES_DEPLOYMENT_GUIDE.md) for detailed step-by-step instructions.

## 📋 Prerequisites

1. **Kubernetes Cluster** (one of):
   - Docker Desktop with Kubernetes enabled
   - Minikube
   - Kind
   - Cloud Kubernetes (GKE, EKS, AKS)

2. **kubectl** installed and configured

3. **Docker Images** pushed to registry:
   - Update image references in deployment files
   - Default: `ghcr.io/sharanya19/ecommerce-{service}:{tag}`

## 🔧 Configuration

### Update Image References

Before deploying, update image references in deployment files:

```yaml
# Change from:
image: ecommerce/user-service:1.0.0

# To:
image: ghcr.io/sharanya19/ecommerce-user-service:latest
```

### Update Secrets

Edit `secrets.yaml` with your actual secrets:

```yaml
stringData:
  jwt.secret: your-actual-jwt-secret-here
  db.password: your-db-password
```

### Update ConfigMaps

Edit `configmaps.yaml` if you need to change service configurations.

## 📊 Deployment Order

1. **Namespace** → `namespace.yaml`
2. **Secrets** → `secrets.yaml`
3. **ConfigMaps** → `configmaps.yaml`
4. **Infrastructure**:
   - MySQL
   - Redis
   - Kafka
   - Eureka
   - Config Server
5. **Microservices**:
   - User Service
   - Product Service
   - Order Service
   - Inventory Service
   - Payment Service
   - API Gateway
6. **Observability** → `otel/otel-collector.yaml`
7. **Ingress** → `ingress.yaml` (optional)

## 🔍 Verification

```powershell
# Check all resources
kubectl get all -n ecommerce

# Check pods
kubectl get pods -n ecommerce

# Check services
kubectl get services -n ecommerce

# Check deployments
kubectl get deployments -n ecommerce
```

## 🛠️ Troubleshooting

### Pods Not Starting

```powershell
# Check pod status
kubectl describe pod <pod-name> -n ecommerce

# Check logs
kubectl logs <pod-name> -n ecommerce
```

### Image Pull Errors

Update image references to use your registry:
- GitHub Container Registry: `ghcr.io/username/ecommerce-{service}:{tag}`
- Docker Hub: `username/ecommerce-{service}:{tag}`

### Database Connection Issues

```powershell
# Check MySQL pod
kubectl get pods -n ecommerce | grep mysql
kubectl logs -n ecommerce <mysql-pod-name>
```

## 📚 Documentation

- **Full Deployment Guide**: [KUBERNETES_DEPLOYMENT_GUIDE.md](../KUBERNETES_DEPLOYMENT_GUIDE.md)
- **Production Setup**: [PRODUCTION_SETUP.md](../PRODUCTION_SETUP.md)

## 🔗 Quick Commands

```powershell
# Deploy everything
kubectl apply -f .

# Delete everything
kubectl delete -f .

# Port forward to access services
kubectl port-forward -n ecommerce service/user-service 8081:8081

# Scale a service
kubectl scale deployment user-service --replicas=3 -n ecommerce

# Restart a service
kubectl rollout restart deployment/user-service -n ecommerce
```

