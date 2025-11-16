# Script to load Docker images into minikube
# This script saves images from local Docker and loads them into minikube

Write-Host "Loading Docker images into minikube..." -ForegroundColor Green

# List of images to load
$images = @(
    "ecommerce/config-server:1.0.0",
    "ecommerce/eureka-server:1.0.0",
    "ecommerce/api-gateway:1.0.0",
    "ecommerce/user-service:1.0.0",
    "ecommerce/product-service:1.0.0",
    "ecommerce/inventory-service:1.0.0",
    "ecommerce/order-service:1.0.0",
    "ecommerce/payment-service:1.0.0"
)

# Try to find minikube
$minikubePath = $null
$possiblePaths = @(
    "minikube",
    "$env:USERPROFILE\.minikube\bin\minikube.exe",
    "$env:LOCALAPPDATA\Programs\minikube\minikube.exe",
    "C:\Program Files\Kubernetes\Minikube\minikube.exe"
)

foreach ($path in $possiblePaths) {
    if (Get-Command $path -ErrorAction SilentlyContinue) {
        $minikubePath = $path
        break
    }
}

if (-not $minikubePath) {
    Write-Host "`nMinikube not found in PATH. Please run the following commands manually:" -ForegroundColor Yellow
    Write-Host "`n1. Configure Docker to use minikube's daemon:" -ForegroundColor Cyan
    Write-Host "   minikube docker-env | Invoke-Expression" -ForegroundColor White
    Write-Host "`n2. Then load each image:" -ForegroundColor Cyan
    foreach ($image in $images) {
        Write-Host "   minikube image load $image" -ForegroundColor White
    }
    Write-Host "`nOr if minikube is installed elsewhere, add it to your PATH." -ForegroundColor Yellow
    exit 1
}

Write-Host "`nFound minikube at: $minikubePath" -ForegroundColor Green

# Load each image
foreach ($image in $images) {
    Write-Host "`nLoading $image..." -ForegroundColor Cyan
    
    # Check if image exists locally
    $imageExists = docker images $image --format "{{.Repository}}:{{.Tag}}" 2>$null
    if (-not $imageExists) {
        Write-Host "  ✗ Image not found locally: $image" -ForegroundColor Red
        continue
    }
    
    # Load image into minikube
    & $minikubePath image load $image
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ Successfully loaded $image" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Failed to load $image" -ForegroundColor Red
    }
}

Write-Host "`n✓ Image loading completed!" -ForegroundColor Green
Write-Host "`nYou can now check pod status with:" -ForegroundColor Yellow
Write-Host "  kubectl get pods -n ecommerce" -ForegroundColor White



