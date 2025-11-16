# Health Endpoint Testing Script
$ErrorActionPreference = "Continue"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "HEALTH ENDPOINTS TESTING" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$baseUrl = "http://localhost"
$results = @()

function Test-HealthEndpoint {
    param(
        [string]$Name,
        [string]$Url
    )
    
    try {
        $response = Invoke-WebRequest -Uri $Url -Method GET -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
        $status = "[PASS]"
        $color = "Green"
        $details = "Status: $($response.StatusCode)"
        
        if ($response.Content) {
            try {
                $json = $response.Content | ConvertFrom-Json
                $details += " | Status: $($json.status)"
                if ($json.components) {
                    $components = $json.components.PSObject.Properties.Name -join ", "
                    $details += " | Components: $components"
                }
            } catch {
                $details += " | Response received"
            }
        }
    } catch {
        $status = "[FAIL]"
        $color = "Red"
        $details = $_.Exception.Message
    }
    
    Write-Host "$status - $Name" -ForegroundColor $color
    Write-Host "  URL: $Url" -ForegroundColor Gray
    Write-Host "  $details`n" -ForegroundColor Gray
    
    return @{
        Name = $Name
        Status = $status
        Details = $details
    }
}

# 1. Infrastructure Services
Write-Host "1. INFRASTRUCTURE SERVICES" -ForegroundColor Yellow
Write-Host "=========================`n" -ForegroundColor Yellow

$results += Test-HealthEndpoint "Eureka Server" "${baseUrl}:8761"
$results += Test-HealthEndpoint "Eureka Actuator Health" "${baseUrl}:8761/actuator/health"
$results += Test-HealthEndpoint "Config Server Health" "${baseUrl}:8888/actuator/health"
$results += Test-HealthEndpoint "API Gateway Health" "${baseUrl}:8080/actuator/health"
$results += Test-HealthEndpoint "Zipkin" "${baseUrl}:9411"

# 2. Microservices
Write-Host "`n2. MICROSERVICES HEALTH" -ForegroundColor Yellow
Write-Host "=======================`n" -ForegroundColor Yellow

$results += Test-HealthEndpoint "User Service Health" "${baseUrl}:8081/actuator/health"
$results += Test-HealthEndpoint "Product Service Health" "${baseUrl}:8082/actuator/health"
$results += Test-HealthEndpoint "Order Service Health" "${baseUrl}:8083/actuator/health"
$results += Test-HealthEndpoint "Inventory Service Health" "${baseUrl}:8084/actuator/health"
$results += Test-HealthEndpoint "Payment Service Health" "${baseUrl}:8085/actuator/health"

# Summary
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$passed = ($results | Where-Object { $_.Status -like "*PASS*" }).Count
$failed = ($results | Where-Object { $_.Status -like "*FAIL*" }).Count
$total = $results.Count

Write-Host "Total Tests: $total" -ForegroundColor White
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor $(if ($failed -gt 0) { "Red" } else { "Green" })
Write-Host "`nSuccess Rate: $([math]::Round(($passed/$total)*100, 2))%`n" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Yellow" })

if ($failed -gt 0) {
    Write-Host "Failed Tests:" -ForegroundColor Red
    $results | Where-Object { $_.Status -like "*FAIL*" } | ForEach-Object {
        Write-Host "  - $($_.Name): $($_.Details)" -ForegroundColor Red
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Testing Complete!" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

