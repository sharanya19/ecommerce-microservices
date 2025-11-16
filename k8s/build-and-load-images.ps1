# Script to build and load Docker images into minikube
# This script builds all microservice images and loads them into minikube's Docker daemon

Write-Host "Building and loading Docker images into minikube..." -ForegroundColor Green

# Set Docker environment to use minikube's Docker daemon
Write-Host "`nConfiguring Docker to use minikube's daemon..." -ForegroundColor Yellow
& minikube docker-env | Invoke-Expression

# Navigate to project root
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

# List of services to build
$services = @(
    "config-server",
    "eureka-server",
    "api-gateway",
    "user-service",
    "product-service",
    "inventory-service",
    "order-service",
    "payment-service"
)

foreach ($service in $services) {
    Write-Host "`nBuilding $service..." -ForegroundColor Cyan
    
    $servicePath = Join-Path $projectRoot $service
    $dockerfilePath = Join-Path $servicePath "Dockerfile"
    
    if (Test-Path $dockerfilePath) {
        # Build the Docker image using project root as context (Dockerfiles expect root-level files)
        $imageName = "ecommerce/$service`:1.0.0"
        Write-Host "  Building image: $imageName" -ForegroundColor Gray
        Write-Host "  Using Dockerfile: $dockerfilePath" -ForegroundColor Gray
        Write-Host "  Build context: $projectRoot" -ForegroundColor Gray
        
        # Build with project root as context and specify the Dockerfile
        docker build -f $dockerfilePath -t $imageName $projectRoot
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✓ Successfully built $imageName" -ForegroundColor Green
        } else {
            Write-Host "  ✗ Failed to build $imageName" -ForegroundColor Red
        }
    } else {
        Write-Host "  ✗ Dockerfile not found: $dockerfilePath" -ForegroundColor Red
    }
}

Write-Host "`n✓ All images built and loaded into minikube!" -ForegroundColor Green
Write-Host "`nYou can now deploy the services using:" -ForegroundColor Yellow
Write-Host "  kubectl apply -f k8s/ -n ecommerce" -ForegroundColor White

