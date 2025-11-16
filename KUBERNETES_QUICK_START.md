# Kubernetes Quick Start Guide

## ✅ Step 1: Kubernetes Enabled!

Great! You've enabled Kubernetes in Docker Desktop. Now let's verify and deploy.

## 🔍 Step 2: Verify Kubernetes is Running

Open PowerShell and run:

```powershell
# Check kubectl version
kubectl version --client

# Check if cluster is accessible (may take a minute after enabling)
kubectl cluster-info

# Check nodes
kubectl get nodes
```

**If you get "You must be logged in to the server" error:**
- Wait 1-2 minutes for Kubernetes to fully start
- Restart Docker Desktop if needed
- Try the commands again

## 🚀 Step 3: Deploy the Application

Once Kubernetes is running, navigate to the k8s folder and deploy:

```powershell
# Navigate to k8s directory
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices\k8s

# Run automated deployment
.\deploy.ps1 -ImageTag latest
```

## 📋 What the Script Does

1. ✅ Verifies kubectl is installed
2. ✅ Checks cluster connectivity
3. ✅ Updates image references automatically
4. ✅ Creates namespace
5. ✅ Creates secrets and configmaps
6. ✅ Deploys infrastructure (MySQL, Redis, Kafka, Eureka, Config Server)
7. ✅ Deploys all microservices
8. ✅ Deploys observability components

## ⏱️ Expected Timeline

- **Kubernetes startup**: 1-2 minutes after enabling
- **Infrastructure deployment**: 2-3 minutes
- **Microservices deployment**: 3-5 minutes
- **Total**: ~5-10 minutes for full deployment

## 🔍 Step 4: Monitor Deployment

After running the script, monitor the deployment:

```powershell
# Watch all pods (press Ctrl+C to stop)
kubectl get pods -n ecommerce -w

# Check specific service
kubectl get pods -n ecommerce | grep user-service

# View logs
kubectl logs -n ecommerce <pod-name>
```

## 🌐 Step 5: Access Services

Once pods are running, access services via port-forward:

```powershell
# User Service
kubectl port-forward -n ecommerce service/user-service 8081:8081

# Product Service (in new terminal)
kubectl port-forward -n ecommerce service/product-service 8082:8082

# API Gateway (in new terminal)
kubectl port-forward -n ecommerce service/api-gateway 8080:8080
```

Then access:
- User Service: http://localhost:8081
- Product Service: http://localhost:8082
- API Gateway: http://localhost:8080

## 🛠️ Troubleshooting

### Kubernetes Not Starting

1. **Check Docker Desktop Status**
   - Ensure Docker Desktop is running
   - Check if Kubernetes shows "Running" status

2. **Restart Docker Desktop**
   - Right-click Docker Desktop icon → Quit
   - Restart Docker Desktop
   - Wait for Kubernetes to start

3. **Reset Kubernetes** (if needed)
   - Docker Desktop → Settings → Kubernetes
   - Uncheck "Enable Kubernetes"
   - Click "Apply & Restart"
   - Re-enable Kubernetes
   - Click "Apply & Restart"

### Pods Not Starting

```powershell
# Check pod status
kubectl describe pod <pod-name> -n ecommerce

# Check logs
kubectl logs <pod-name> -n ecommerce

# Common issues:
# - Image pull errors: Update image references
# - CrashLoopBackOff: Check application logs
# - Pending: Check resource limits
```

### Image Pull Errors

The deployment script automatically updates image references. If you see image pull errors:

1. Check if images exist in your registry:
   ```powershell
   # Check GitHub Container Registry
   # Go to: https://github.com/sharanya19/ecommerce-microservices/packages
   ```

2. Update image tag in deployment script:
   ```powershell
   .\deploy.ps1 -ImageTag <your-commit-sha>
   ```

## 📊 Quick Status Check

```powershell
# Get everything
kubectl get all -n ecommerce

# Get pods with status
kubectl get pods -n ecommerce

# Get services
kubectl get services -n ecommerce

# Get deployments
kubectl get deployments -n ecommerce
```

## 🎯 Next Steps After Deployment

1. **Verify Health**
   ```powershell
   kubectl get pods -n ecommerce
   # All pods should show "Running" status
   ```

2. **Test Services**
   - Port-forward to services
   - Access health endpoints
   - Test API calls

3. **Set Up Ingress** (optional)
   - For external access
   - See `ingress.yaml` for configuration

4. **Monitor with Prometheus/Grafana** (optional)
   - If observability is deployed
   - Access Grafana dashboard

## 📚 Full Documentation

- **Complete Guide**: [KUBERNETES_DEPLOYMENT_GUIDE.md](../KUBERNETES_DEPLOYMENT_GUIDE.md)
- **K8s README**: [k8s/README.md](k8s/README.md)

## ✅ Checklist

- [x] Kubernetes enabled in Docker Desktop
- [ ] Kubernetes cluster running (verify with `kubectl get nodes`)
- [ ] Navigate to k8s directory
- [ ] Run deployment script
- [ ] Wait for pods to be Running
- [ ] Port-forward to access services
- [ ] Test API endpoints

---

**Ready to deploy?** Run: `cd k8s && .\deploy.ps1`

