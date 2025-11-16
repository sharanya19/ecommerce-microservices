# Verification Script for CI/CD, Kubernetes, and Health Checks
$ErrorActionPreference = "Continue"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "ECOMMERCE MICROSERVICES - SETUP VERIFICATION" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

function Test-Command {
    param([string]$Command, [string]$Name)
    try {
        $null = Get-Command $Command -ErrorAction Stop
        Write-Host "[OK] $Name is installed" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "[FAIL] $Name is NOT installed" -ForegroundColor Red
        return $false
    }
}

function Test-Service {
    param([string]$Name, [string]$Url)
    try {
        $response = Invoke-WebRequest -Uri $Url -Method GET -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
        Write-Host "[OK] $Name is healthy" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "[FAIL] $Name is NOT responding" -ForegroundColor Red
        return $false
    }
}

# 1. Check Prerequisites
Write-Host "1. CHECKING PREREQUISITES" -ForegroundColor Yellow
Write-Host "=========================" -ForegroundColor Yellow
Write-Host ""

$dockerOk = Test-Command "docker" "Docker"
$kubectlOk = Test-Command "kubectl" "kubectl"
$gitOk = Test-Command "git" "Git"

# 2. Check Docker Compose
Write-Host ""
Write-Host "2. CHECKING DOCKER COMPOSE SERVICES" -ForegroundColor Yellow
Write-Host "====================================" -ForegroundColor Yellow
Write-Host ""

if ($dockerOk) {
    try {
        $result = docker compose ps 2>&1
        if ($LASTEXITCODE -eq 0 -and $result -match "Up") {
            Write-Host "[OK] Docker Compose is running" -ForegroundColor Green
            Write-Host ""
            Write-Host "Running Containers:" -ForegroundColor Cyan
            docker compose ps
        } else {
            Write-Host "[INFO] Docker Compose services not running" -ForegroundColor Yellow
            Write-Host "   Run: docker compose up -d" -ForegroundColor Gray
        }
    } catch {
        Write-Host "[FAIL] Docker Compose not accessible" -ForegroundColor Red
    }
} else {
    Write-Host "[INFO] Docker not installed - skipping Docker Compose check" -ForegroundColor Yellow
}

# 3. Check Kubernetes
Write-Host ""
Write-Host "3. CHECKING KUBERNETES CLUSTER" -ForegroundColor Yellow
Write-Host "==============================" -ForegroundColor Yellow
Write-Host ""

if ($kubectlOk) {
    try {
        $kubectlVersion = kubectl version --client --short 2>&1
        Write-Host "[OK] kubectl is configured" -ForegroundColor Green
        Write-Host "   $kubectlVersion" -ForegroundColor Gray
        
        try {
            $clusterInfo = kubectl cluster-info 2>&1
            if ($LASTEXITCODE -eq 0) {
                Write-Host "[OK] Kubernetes cluster is accessible" -ForegroundColor Green
                
                # Check namespace
                $namespace = kubectl get namespace ecommerce 2>&1
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "[OK] 'ecommerce' namespace exists" -ForegroundColor Green
                    
                    # Check pods
                    Write-Host ""
                    Write-Host "Pods in ecommerce namespace:" -ForegroundColor Cyan
                    kubectl get pods -n ecommerce
                    
                    # Check services
                    Write-Host ""
                    Write-Host "Services in ecommerce namespace:" -ForegroundColor Cyan
                    kubectl get svc -n ecommerce
                } else {
                    Write-Host "[INFO] 'ecommerce' namespace does not exist" -ForegroundColor Yellow
                    Write-Host "   Run: kubectl apply -f k8s/namespace.yaml" -ForegroundColor Gray
                }
            } else {
                Write-Host "[FAIL] Cannot connect to Kubernetes cluster" -ForegroundColor Red
                Write-Host "   Make sure kubectl is configured correctly" -ForegroundColor Gray
            }
        } catch {
            Write-Host "[FAIL] Error checking Kubernetes cluster" -ForegroundColor Red
        }
    } catch {
        Write-Host "[FAIL] kubectl not working properly" -ForegroundColor Red
    }
} else {
    Write-Host "[INFO] kubectl not installed - skipping Kubernetes check" -ForegroundColor Yellow
}

# 4. Check Health Endpoints
Write-Host ""
Write-Host "4. CHECKING HEALTH ENDPOINTS" -ForegroundColor Yellow
Write-Host "============================" -ForegroundColor Yellow
Write-Host ""

$services = @(
    @{Name="Eureka Server"; Url="http://localhost:8761/actuator/health"},
    @{Name="Config Server"; Url="http://localhost:8888/actuator/health"},
    @{Name="API Gateway"; Url="http://localhost:8080/actuator/health"},
    @{Name="User Service"; Url="http://localhost:8081/actuator/health"},
    @{Name="Product Service"; Url="http://localhost:8082/actuator/health"},
    @{Name="Order Service"; Url="http://localhost:8083/actuator/health"},
    @{Name="Inventory Service"; Url="http://localhost:8084/actuator/health"},
    @{Name="Payment Service"; Url="http://localhost:8085/actuator/health"}
)

$healthyCount = 0
foreach ($service in $services) {
    if (Test-Service $service.Name $service.Url) {
        $healthyCount++
    }
}

Write-Host ""
$color = if ($healthyCount -eq $services.Count) { "Green" } else { "Yellow" }
Write-Host "Health Summary: $healthyCount/$($services.Count) services healthy" -ForegroundColor $color

# 5. Check GitHub Actions
Write-Host ""
Write-Host "5. CHECKING GITHUB ACTIONS" -ForegroundColor Yellow
Write-Host "==========================" -ForegroundColor Yellow
Write-Host ""

if ($gitOk) {
    try {
        $remote = git remote get-url origin 2>&1
        if ($remote -like "*github.com*") {
            Write-Host "[OK] GitHub repository detected" -ForegroundColor Green
            Write-Host "   $remote" -ForegroundColor Gray
            Write-Host ""
            Write-Host "To check CI/CD status:" -ForegroundColor Cyan
            $repoPath = ($remote -split '/')[-2..-1] -join '/'
            $repoPath = $repoPath -replace '\.git$', ''
            Write-Host "   1. Go to: https://github.com/$repoPath/actions" -ForegroundColor Gray
            Write-Host "   2. Check workflow runs" -ForegroundColor Gray
            Write-Host "   3. Verify CI/CD pipelines are running" -ForegroundColor Gray
        } else {
            Write-Host "[INFO] Not a GitHub repository" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "[INFO] Could not determine Git remote" -ForegroundColor Yellow
    }
} else {
    Write-Host "[INFO] Git not installed" -ForegroundColor Yellow
}

# 6. Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "VERIFICATION COMPLETE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "1. If Docker Compose not running: docker compose up -d" -ForegroundColor White
Write-Host "2. If Kubernetes not set up: Follow K8s setup guide" -ForegroundColor White
Write-Host "3. If CI/CD not working: Check GitHub Actions tab" -ForegroundColor White
Write-Host "4. Review PRODUCTION_SETUP.md for detailed instructions" -ForegroundColor White
Write-Host ""
