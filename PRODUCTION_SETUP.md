# Production Setup Guide

This document describes the production-ready configurations for the E-commerce Microservices application.

## Docker Compose Enhancements

### Healthchecks
All services now have healthchecks configured:
- **Infrastructure services** (MySQL, Redis, Kafka, etc.): Native healthcheck commands
- **Spring Boot services**: HTTP healthchecks using `/actuator/health` endpoint
- **Start periods**: Configured to allow services time to start before healthcheck begins

### Dependencies
- All `depends_on` relationships now use `condition: service_healthy`
- Services wait for dependencies to be healthy before starting
- Prevents cascading failures during startup

### Resource Limits
- **CPU limits**: 1-2 CPUs per service
- **Memory limits**: 512MB - 2GB per service
- **Reservations**: Minimum guaranteed resources
- Prevents resource exhaustion and ensures fair scheduling

### Networks
- Custom bridge network with subnet configuration
- Isolated network for all services
- Improved security and performance

## Kubernetes Production Manifests

### Deployments
Each service includes:
- **Replicas**: Minimum 2 for high availability
- **Rolling Update Strategy**: Zero-downtime deployments
- **Pod Anti-Affinity**: Distributes pods across nodes
- **Resource Requests/Limits**: CPU and memory constraints
- **Probes**:
  - `livenessProbe`: Detects and restarts unhealthy pods
  - `readinessProbe`: Determines when pod can receive traffic
  - `startupProbe`: Allows slow-starting services time to initialize

### Services
- **Type**: ClusterIP (internal only)
- **Ports**: Named ports for better service discovery
- **Selectors**: Match deployment labels

### Horizontal Pod Autoscaler (HPA)
- **Min Replicas**: 2
- **Max Replicas**: 10
- **Metrics**: CPU (70%) and Memory (80%) utilization
- **Scaling Behavior**: 
  - Scale up: Aggressive (100% or 2 pods per 15s)
  - Scale down: Conservative (50% per 60s, 5min stabilization)

### Pod Disruption Budget (PDB)
- **Min Available**: 1 pod
- Ensures service availability during node maintenance
- Prevents accidental service disruption

### Secrets
- **JWT Secret**: For authentication
- **Database Credentials**: Username and password
- **Kafka/REDIS**: Connection details
- Stored as Kubernetes Secrets (base64 encoded)

### ConfigMaps
- Service-specific configurations
- Database connection URLs
- Server ports
- Application names

### Ingress
- **Controller**: Nginx Ingress
- **TLS**: Configured for HTTPS (requires cert-manager)
- **Rate Limiting**: 100 requests per minute
- **Hosts**: Separate subdomains for each service
  - `api.ecommerce.local` → API Gateway
  - `user.ecommerce.local` → User Service
  - `product.ecommerce.local` → Product Service
  - `order.ecommerce.local` → Order Service
  - `inventory.ecommerce.local` → Inventory Service
  - `payment.ecommerce.local` → Payment Service

### ServiceMonitors
- Prometheus ServiceMonitor resources
- Scrapes `/actuator/prometheus` endpoint
- 30-second scrape interval
- Enables Prometheus to discover and scrape metrics

## GitHub Actions CI/CD

### CI Pipeline (`.github/workflows/ci.yml`)
**Triggers**: Push to main/develop, Pull Requests

**Steps**:
1. **Checkout**: Get source code
2. **Java Setup**: JDK 17 with Gradle caching
3. **Build**: Compile all services
4. **Test**: Run unit tests with reporting
5. **Docker Build**: Build all service images
6. **Security Scan**: Trivy vulnerability scanning
7. **Push Images**: Push to GitHub Container Registry

**Image Tags**:
- Branch name
- PR number
- Semantic version
- Git SHA

### CD Pipeline (`.github/workflows/cd.yml`)
**Triggers**: Push to main, Version tags, Manual dispatch

**Steps**:
1. **Kubernetes Setup**: Configure kubectl and Helm
2. **Namespace**: Create/verify namespace
3. **Secrets/ConfigMaps**: Apply configuration
4. **Update Images**: Replace image tags with build SHA
5. **Deploy Infrastructure**: MySQL, Redis, Kafka, Eureka, Config Server
6. **Deploy Services**: All microservices
7. **Deploy Observability**: OpenTelemetry, ServiceMonitors
8. **Deploy Ingress**: External access configuration
9. **Health Check**: Wait for all deployments
10. **Rollback**: Automatic rollback on failure

## Deployment Instructions

### Prerequisites
1. Kubernetes cluster (1.24+)
2. Nginx Ingress Controller installed
3. Prometheus Operator (for ServiceMonitors)
4. Cert-Manager (for TLS certificates)
5. kubectl configured with cluster access

### Steps

1. **Create Secrets**:
   ```bash
   kubectl apply -f k8s/secrets.yaml
   ```

2. **Create ConfigMaps**:
   ```bash
   kubectl apply -f k8s/configmaps.yaml
   ```

3. **Deploy Infrastructure**:
   ```bash
   kubectl apply -f k8s/namespace.yaml
   kubectl apply -f k8s/mysql-deployment.yaml
   kubectl apply -f k8s/redis-deployment.yaml
   kubectl apply -f k8s/kafka-deployment.yaml
   kubectl apply -f k8s/eureka-deployment.yaml
   kubectl apply -f k8s/config-server-deployment.yaml
   ```

4. **Deploy Services**:
   ```bash
   kubectl apply -f k8s/user-service-deployment.yaml
   kubectl apply -f k8s/product-service-deployment.yaml
   kubectl apply -f k8s/order-service-deployment.yaml
   kubectl apply -f k8s/inventory-service-deployment.yaml
   kubectl apply -f k8s/payment-service-deployment.yaml
   kubectl apply -f k8s/api-gateway-deployment.yaml
   ```

5. **Deploy Observability**:
   ```bash
   kubectl apply -f k8s/otel/otel-collector.yaml
   kubectl apply -f k8s/servicemonitors.yaml
   ```

6. **Deploy Ingress**:
   ```bash
   kubectl apply -f k8s/ingress.yaml
   ```

### Verify Deployment
```bash
# Check pods
kubectl get pods -n ecommerce

# Check services
kubectl get svc -n ecommerce

# Check ingress
kubectl get ingress -n ecommerce

# Check HPA
kubectl get hpa -n ecommerce

# Check PDB
kubectl get pdb -n ecommerce
```

## Notes

### Healthcheck Compatibility
- Docker Compose healthchecks use `curl` which may not be available in all base images
- For production, consider:
  - Adding curl to Dockerfiles
  - Using `wget` instead
  - Using Spring Boot Actuator's native health endpoint

### Secrets Management
- **Current**: Plain text in `secrets.yaml` (for development)
- **Production**: Use:
  - External Secrets Operator
  - HashiCorp Vault
  - AWS Secrets Manager / Azure Key Vault
  - Sealed Secrets

### Image Registry
- CI/CD pushes to GitHub Container Registry (ghcr.io)
- Update image references in deployments to match your registry
- Ensure proper authentication for private registries

### Monitoring
- ServiceMonitors require Prometheus Operator
- Grafana dashboards should be configured separately
- Alert rules are in `observability/alerts/prometheus-rules.yml`

### Scaling
- HPA automatically scales based on CPU/Memory
- Adjust thresholds in HPA manifests as needed
- Consider custom metrics for more intelligent scaling

### Security
- TLS certificates via cert-manager
- Network policies recommended for pod-to-pod communication
- RBAC should be configured for service accounts
- Image scanning in CI pipeline (Trivy)

## Troubleshooting

### Pods Not Starting
- Check resource limits vs. node capacity
- Verify secrets/configmaps exist
- Check image pull secrets for private registries

### Health Checks Failing
- Verify actuator endpoints are enabled
- Check network policies
- Ensure probes use correct paths

### HPA Not Scaling
- Verify metrics-server is installed
- Check HPA status: `kubectl describe hpa <name>`
- Ensure resource requests are set

### Ingress Not Working
- Verify Nginx Ingress Controller is running
- Check ingress class matches
- Verify DNS/hosts file configuration

