# Kubernetes Deployment Script for Windows PowerShell
# This script automates the deployment of the ecommerce microservices to Kubernetes

param(
    [string]$ImageTag = "latest",
    [string]$Registry = "ghcr.io/sharanya19/ecommerce",
    [switch]$SkipInfrastructure = $false,
    [switch]$SkipServices = $false
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Kubernetes Deployment Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if kubectl is installed
Write-Host "Checking kubectl installation..." -ForegroundColor Yellow
try {
    $kubectlVersion = kubectl version --client 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "kubectl not found"
    }
    Write-Host "[OK] kubectl is installed" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] kubectl is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install kubectl: https://kubernetes.io/docs/tasks/tools/" -ForegroundColor Yellow
    exit 1
}

# Check if cluster is accessible
Write-Host "Checking Kubernetes cluster..." -ForegroundColor Yellow
try {
    kubectl cluster-info | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Cannot connect to cluster"
    }
    Write-Host "[OK] Kubernetes cluster is accessible" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Cannot connect to Kubernetes cluster" -ForegroundColor Red
    Write-Host "Please ensure Kubernetes is running (Docker Desktop, minikube, etc.)" -ForegroundColor Yellow
    exit 1
}

# Update image references in deployment files
Write-Host "Updating image references..." -ForegroundColor Yellow
$services = @("user-service", "product-service", "order-service", "inventory-service", "payment-service", "api-gateway")

foreach ($service in $services) {
    $file = "$service-deployment.yaml"
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        $content = $content -replace "image: ecommerce/$service:1\.0\.0", "image: $registry-$service`:$ImageTag"
        $content = $content -replace "image: ecommerce/$service:latest", "image: $registry-$service`:$ImageTag"
        Set-Content -Path $file -Value $content -NoNewline
        Write-Host "  [OK] Updated $file" -ForegroundColor Green
    }
}

# Create namespace
Write-Host "Creating namespace..." -ForegroundColor Yellow
kubectl apply -f namespace.yaml
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Namespace created" -ForegroundColor Green
} else {
    Write-Host "[WARNING] Namespace may already exist" -ForegroundColor Yellow
}

# Create secrets
Write-Host "Creating secrets..." -ForegroundColor Yellow
kubectl apply -f secrets.yaml
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Secrets created" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Failed to create secrets" -ForegroundColor Red
}

# Create configmaps
Write-Host "Creating configmaps..." -ForegroundColor Yellow
kubectl apply -f configmaps.yaml
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] ConfigMaps created" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Failed to create configmaps" -ForegroundColor Red
}

# Deploy infrastructure
if (-not $SkipInfrastructure) {
    Write-Host "Deploying infrastructure services..." -ForegroundColor Yellow
    
    $infraServices = @(
        "mysql-deployment.yaml",
        "redis-deployment.yaml",
        "kafka-deployment.yaml",
        "eureka-deployment.yaml",
        "config-server-deployment.yaml"
    )
    
    foreach ($service in $infraServices) {
        if (Test-Path $service) {
            Write-Host "  Deploying $service..." -ForegroundColor Cyan
            kubectl apply -f $service
            if ($LASTEXITCODE -eq 0) {
                Write-Host "    [OK] $service deployed" -ForegroundColor Green
            } else {
                Write-Host "    [ERROR] Failed to deploy $service" -ForegroundColor Red
            }
        }
    }
    
    Write-Host "Waiting for infrastructure to be ready..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
    
    Write-Host "Infrastructure status:" -ForegroundColor Cyan
    kubectl get pods -n ecommerce | Select-String -Pattern "mysql|redis|kafka|eureka|config-server"
}

# Deploy microservices
if (-not $SkipServices) {
    Write-Host "Deploying microservices..." -ForegroundColor Yellow
    
    foreach ($service in $services) {
        $file = "$service-deployment.yaml"
        if (Test-Path $file) {
            Write-Host "  Deploying $service..." -ForegroundColor Cyan
            kubectl apply -f $file
            if ($LASTEXITCODE -eq 0) {
                Write-Host "    [OK] $service deployed" -ForegroundColor Green
            } else {
                Write-Host "    [ERROR] Failed to deploy $service" -ForegroundColor Red
            }
        }
    }
}

# Deploy observability
Write-Host "Deploying observability components..." -ForegroundColor Yellow
if (Test-Path "otel/otel-collector.yaml") {
    kubectl apply -f otel/otel-collector.yaml
    Write-Host "[OK] OpenTelemetry Collector deployed" -ForegroundColor Green
}

# Deploy ingress
Write-Host "Deploying ingress..." -ForegroundColor Yellow
if (Test-Path "ingress.yaml") {
    kubectl apply -f ingress.yaml
    Write-Host "[OK] Ingress deployed" -ForegroundColor Green
}

# Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Deployment Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Checking deployment status..." -ForegroundColor Yellow
kubectl get pods -n ecommerce

Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "1. Wait for all pods to be 'Running': kubectl get pods -n ecommerce -w" -ForegroundColor White
Write-Host "2. Check pod logs: kubectl logs -n ecommerce <pod-name>" -ForegroundColor White
Write-Host "3. Port forward to access services: kubectl port-forward -n ecommerce service/user-service 8081:8081" -ForegroundColor White
Write-Host "4. Check services: kubectl get services -n ecommerce" -ForegroundColor White
Write-Host ""

Write-Host "Deployment completed!" -ForegroundColor Green

