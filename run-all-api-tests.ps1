Param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Invoke-TestApi {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$Method,
        [Parameter(Mandatory = $true)][string]$Url,
        [object]$Body = $null,
        [hashtable]$Headers = @{}
    )

    $result = [ordered]@{
        Name       = $Name
        Method     = $Method
        Url        = $Url
        Success    = $false
        StatusCode = $null
        Message    = $null
        Data       = $null
    }

    try {
        $params = @{
            Method = $Method
            Uri    = $Url
            Headers = $Headers
        }

        if ($null -ne $Body) {
            $params.Body = $Body | ConvertTo-Json -Depth 6
            $params.ContentType = 'application/json'
        }

        $response = Invoke-RestMethod @params
        $result.Success = $true
        $result.Message = 'OK'
        $result.Data    = $response
        Write-Host "[PASS] $Name ($Method $Url)" -ForegroundColor Green
    }
    catch {
        $statusCode = $null
        if ($_.Exception.Response -and $_.Exception.Response.StatusCode) {
            $statusCode = [int]$_.Exception.Response.StatusCode.value__
        }
        $result.Success = $false
        $result.StatusCode = $statusCode
        $result.Message = $_.Exception.Message
        Write-Host "[FAIL] $Name ($Method $Url) -> $($_.Exception.Message)" -ForegroundColor Red
    }

    return $result
}

$results = @()
$base = "http://localhost"

# --- Health Checks ---
$servicePorts = @{
    "User Service Health"      = 8081
    "Product Service Health"   = 8082
    "Order Service Health"     = 8083
    "Inventory Service Health" = 8084
    "Payment Service Health"   = 8085
}

foreach ($entry in $servicePorts.GetEnumerator()) {
    $results += Invoke-TestApi -Name $entry.Key -Method 'GET' -Url "$($base):$($entry.Value)/actuator/health"
}

# --- User Service ---
$username = "apitester$([System.Guid]::NewGuid().ToString('N').Substring(0,6))"
$userRegisterBody = @{
    username  = $username
    email     = "$username@example.test"
    password  = 'Password123!'
    firstName = 'API'
    lastName  = 'Tester'
    phone     = '9999999999'
    address   = 'Hyderabad'
    role      = 'CUSTOMER'
}
$registerResult = Invoke-TestApi -Name 'User - Register' -Method 'POST' -Url "$($base):8081/users/register" -Body $userRegisterBody
$results += $registerResult
$userId = if ($registerResult.Success) { $registerResult.Data.id } else { $null }

$loginBody = @{
    username = $username
    password = 'Password123!'
}
$loginResult = Invoke-TestApi -Name 'User - Login' -Method 'POST' -Url "$($base):8081/users/login" -Body $loginBody
$results += $loginResult
$token = if ($loginResult.Success) { $loginResult.Data.token } else { $null }
$authHeaders = if ($token) { @{ Authorization = "Bearer $token" } } else { @{} }

if ($userId) {
    $results += Invoke-TestApi -Name 'User - Get All' -Method 'GET' -Url "$($base):8081/users" -Headers $authHeaders
    $results += Invoke-TestApi -Name 'User - Get By Id' -Method 'GET' -Url "$($base):8081/users/$userId" -Headers $authHeaders
    $results += Invoke-TestApi -Name 'User - Get By Username' -Method 'GET' -Url "$($base):8081/users/username/$username" -Headers $authHeaders

    $updateBody = @{
        id        = $userId
        username  = $username
        email     = "$username@example.test"
        password  = 'Password123!'
        firstName = 'API'
        lastName  = 'Tester'
        phone     = '1111111111'
        address   = 'Hyderabad Updated'
        role      = 'CUSTOMER'
    }
    $results += Invoke-TestApi -Name 'User - Update' -Method 'PUT' -Url "$($base):8081/users/$userId" -Body $updateBody -Headers $authHeaders
}

# --- Product Service ---
$productBody = @{
    name        = 'API Test Product'
    description = 'Product created during automated API testing'
    price       = 2999.99
    stock       = 75
    category    = 'Accessories'
    imageUrl    = 'https://example.test/product-image.png'
    sku         = 'API-TEST-001'
    status      = 'ACTIVE'
}
$productResult = Invoke-TestApi -Name 'Product - Create' -Method 'POST' -Url "$($base):8082/products" -Body $productBody
$results += $productResult
$productId = if ($productResult.Success) { $productResult.Data.id } else { $null }

if ($productId) {
    $results += Invoke-TestApi -Name 'Product - Get By Id' -Method 'GET' -Url "$($base):8082/products/$productId"
    $results += Invoke-TestApi -Name 'Product - Search' -Method 'GET' -Url "$($base):8082/products?search=API"
    $results += Invoke-TestApi -Name 'Product - By Category' -Method 'GET' -Url "$($base):8082/products?category=Accessories"
    $results += Invoke-TestApi -Name 'Product - Update Stock' -Method 'PATCH' -Url "$($base):8082/products/$productId/stock?quantity=120"

    $updateProductBody = @{
        id          = $productId
        name        = 'API Test Product Updated'
        description = 'Updated description'
        price       = 2499.99
        stock       = 120
        category    = 'Accessories'
        imageUrl    = 'https://example.test/product-image-updated.png'
        sku         = 'API-TEST-001'
        status      = 'ACTIVE'
    }
    $results += Invoke-TestApi -Name 'Product - Update' -Method 'PUT' -Url "$($base):8082/products/$productId" -Body $updateProductBody
    $results += Invoke-TestApi -Name 'Product - List All' -Method 'GET' -Url "$($base):8082/products"
}

# --- Inventory Service ---
if ($productId) {
    $results += Invoke-TestApi -Name 'Inventory - Create For Product' -Method 'POST' -Url "$($base):8084/inventory/product/${productId}?initialQuantity=60"
    $results += Invoke-TestApi -Name 'Inventory - Get By Product' -Method 'GET' -Url "$($base):8084/inventory/product/${productId}"
    $results += Invoke-TestApi -Name 'Inventory - Get All' -Method 'GET' -Url "$($base):8084/inventory"
    $results += Invoke-TestApi -Name 'Inventory - Adjust Quantity' -Method 'PATCH' -Url "$($base):8084/inventory/product/${productId}/quantity?quantityChange=10"
    $results += Invoke-TestApi -Name 'Inventory - Reserve' -Method 'PATCH' -Url "$($base):8084/inventory/product/${productId}/reserve?quantity=5"
    $results += Invoke-TestApi -Name 'Inventory - Release' -Method 'PATCH' -Url "$($base):8084/inventory/product/${productId}/release?quantity=2"
    $results += Invoke-TestApi -Name 'Inventory - Confirm' -Method 'PATCH' -Url "$($base):8084/inventory/product/${productId}/confirm?quantity=3"
}

# --- Order Service ---
$orderId = $null
if ($userId -and $productId) {
    $orderBody = @{
        userId = $userId
        items  = @(
            @{
                productId = $productId
                quantity  = 2
            }
        )
        shippingAddress = 'Hyderabad'
        billingAddress  = 'Hyderabad'
    }
    $orderResult = Invoke-TestApi -Name 'Order - Create' -Method 'POST' -Url "$($base):8083/orders" -Body $orderBody
    $results += $orderResult
    $orderId = if ($orderResult.Success) { $orderResult.Data.id } else { $null }
}

if ($orderId) {
    $results += Invoke-TestApi -Name 'Order - Get By Id' -Method 'GET' -Url "$($base):8083/orders/$orderId"
    $results += Invoke-TestApi -Name 'Order - List All' -Method 'GET' -Url "$($base):8083/orders"
    $results += Invoke-TestApi -Name 'Order - By User' -Method 'GET' -Url "$($base):8083/orders/user/$userId"
    $results += Invoke-TestApi -Name 'Order - Update Status' -Method 'PATCH' -Url "$($base):8083/orders/${orderId}/status?status=SHIPPED"
    $results += Invoke-TestApi -Name 'Order - Update Payment Status' -Method 'PATCH' -Url "$($base):8083/orders/${orderId}/payment-status?paymentStatus=PAID"
}

# --- Payment Service ---
$paymentId = $null
$transactionId = $null
if ($orderId -and $userId) {
    $paymentBody = @{
        orderId       = $orderId
        userId        = $userId
        amount        = 5999.98
        paymentMethod = 'CREDIT_CARD'
    }
    $paymentResult = Invoke-TestApi -Name 'Payment - Process' -Method 'POST' -Url "$($base):8085/payments" -Body $paymentBody
    $results += $paymentResult
    if ($paymentResult.Success) {
        $paymentId = $paymentResult.Data.id
        $transactionId = $paymentResult.Data.transactionId
    }
}

if ($paymentId) {
    $results += Invoke-TestApi -Name 'Payment - Get By Id' -Method 'GET' -Url "$($base):8085/payments/$paymentId"
    if ($transactionId) {
        $results += Invoke-TestApi -Name 'Payment - By Transaction' -Method 'GET' -Url "$($base):8085/payments/transaction/$transactionId"
    }
    $results += Invoke-TestApi -Name 'Payment - By Order' -Method 'GET' -Url "$($base):8085/payments/order/$orderId"
    $results += Invoke-TestApi -Name 'Payment - By User' -Method 'GET' -Url "$($base):8085/payments/user/$userId"
    $results += Invoke-TestApi -Name 'Payment - List All' -Method 'GET' -Url "$($base):8085/payments"
    $results += Invoke-TestApi -Name 'Payment - Refund' -Method 'POST' -Url "$($base):8085/payments/$paymentId/refund"
}

# --- Cleanup ---
if ($productId) {
    $results += Invoke-TestApi -Name 'Product - Delete' -Method 'DELETE' -Url "$($base):8082/products/$productId"
}
if ($userId) {
    $results += Invoke-TestApi -Name 'User - Delete' -Method 'DELETE' -Url "$($base):8081/users/$userId" -Headers $authHeaders
}

# --- Summary Output ---
$passedCount = ($results | Where-Object { $_.Success } | Measure-Object).Count
$failedCount = ($results | Where-Object { -not $_.Success } | Measure-Object).Count
$totalCount  = ($results | Measure-Object).Count

$summary = @{
    Timestamp = (Get-Date).ToString('u')
    Results   = $results
    Passed    = $passedCount
    Failed    = $failedCount
    Total     = $totalCount
}

$summary | ConvertTo-Json -Depth 6 | Set-Content -Path api-test-summary.json

Write-Host "`n===================================" -ForegroundColor Cyan
Write-Host "API TEST SUMMARY" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ("Total : {0}" -f $summary.Total)
Write-Host ("Passed: {0}" -f $summary.Passed) -ForegroundColor Green
Write-Host ("Failed: {0}" -f $summary.Failed) -ForegroundColor ($(if ($summary.Failed -eq 0) { 'Green' } else { 'Red' }))
Write-Host "Detailed results saved to api-test-summary.json"

