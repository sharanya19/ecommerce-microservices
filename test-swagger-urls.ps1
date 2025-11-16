# Test Swagger URLs
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "SWAGGER UI ACCESSIBILITY TEST" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$services = @(
    @{Name="User Service"; Port=8081},
    @{Name="Product Service"; Port=8082},
    @{Name="Order Service"; Port=8083},
    @{Name="Inventory Service"; Port=8084},
    @{Name="Payment Service"; Port=8085}
)

foreach ($svc in $services) {
    $url = "http://localhost:$($svc.Port)/swagger-ui/index.html"
    Write-Host "$($svc.Name) - Testing: $url" -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
        Write-Host "  [PASS] Swagger UI is accessible" -ForegroundColor Green
        Write-Host "  Swagger URL: $url" -ForegroundColor Gray
        Write-Host "  API Docs: http://localhost:$($svc.Port)/v3/api-docs`n" -ForegroundColor Gray
    } catch {
        Write-Host "  [FAIL] Swagger UI not accessible" -ForegroundColor Red
        Write-Host "  Error: $($_.Exception.Message)`n" -ForegroundColor Gray
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing Complete!" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

