# Swagger API Testing Guide

## 📋 Table of Contents
1. [Swagger URLs](#swagger-urls)
2. [Prerequisites](#prerequisites)
3. [Step-by-Step Testing Guide](#step-by-step-testing-guide)
4. [API Endpoints by Service](#api-endpoints-by-service)
5. [Authentication Setup](#authentication-setup)
6. [Testing Workflow](#testing-workflow)

---

## 🔗 Swagger URLs

All services have Swagger UI integrated. Access them using the following URLs:

| Service | Swagger UI URL | API Docs URL |
|---------|---------------|--------------|
| **User Service** | http://localhost:8081/swagger-ui/index.html | http://localhost:8081/v3/api-docs |
| **Product Service** | http://localhost:8082/swagger-ui/index.html | http://localhost:8082/v3/api-docs |
| **Order Service** | http://localhost:8083/swagger-ui/index.html | http://localhost:8083/v3/api-docs |
| **Inventory Service** | http://localhost:8084/swagger-ui/index.html | http://localhost:8084/v3/api-docs |
| **Payment Service** | http://localhost:8085/swagger-ui/index.html | http://localhost:8085/v3/api-docs |

**Note**: You can also access services through the API Gateway at:
- API Gateway: http://localhost:8080
- Routes: `/api/users`, `/api/products`, `/api/orders`, `/api/inventory`, `/api/payments`

---

## ✅ Prerequisites

1. **All services must be running** (verify with health checks)
2. **Open browser** (Chrome, Firefox, or Edge recommended)
3. **No additional tools required** - Swagger UI runs in the browser

---

## 📝 Step-by-Step Testing Guide

### Step 1: Access Swagger UI

1. Open your web browser
2. Navigate to any service's Swagger UI URL (e.g., `http://localhost:8081/swagger-ui/index.html`)
3. You should see the Swagger UI interface with all available APIs

### Step 2: Set Up Authentication (JWT Token)

**IMPORTANT**: Most APIs require JWT authentication. Follow these steps:

1. **Go to User Service Swagger UI**: http://localhost:8081/swagger-ui/index.html
2. **Find the `/users/login` endpoint** (POST method)
3. **Click "Try it out"**
4. **Enter login credentials**:
   ```json
   {
     "username": "testuser",
     "password": "password123"
   }
   ```
   *(If user doesn't exist, register first using `/users/register`)*
5. **Click "Execute"**
6. **Copy the JWT token** from the response (it will be in the `token` field)
7. **Click the "Authorize" button** (🔒 lock icon) at the top right of Swagger UI
8. **Paste the token** in the "Value" field
9. **Click "Authorize"** then "Close"
10. **The token is now set** for all API calls in this Swagger session

### Step 3: Test APIs

1. **Expand any API endpoint** by clicking on it
2. **Click "Try it out"**
3. **Fill in the required parameters** (path variables, query parameters, request body)
4. **Click "Execute"**
5. **View the response** (status code, headers, and body)

---

## 🔌 API Endpoints by Service

### 1. User Service (Port 8081)

#### Base URL: `http://localhost:8081/api/users`

| Method | Endpoint | Description | Auth Required | Parameters |
|--------|----------|-------------|---------------|-------------|
| POST | `/register` | Register a new user | ❌ No | **Body**: `{ "username": "string", "email": "string", "password": "string" }` |
| POST | `/login` | Login and get JWT token | ❌ No | **Body**: `{ "username": "string", "password": "string" }` |
| GET | `/{id}` | Get user by ID | ✅ Yes | **Path**: `id` (Long) |
| GET | `/username/{username}` | Get user by username | ✅ Yes | **Path**: `username` (String) |
| GET | `/` | Get all users | ✅ Yes | None |
| PUT | `/{id}` | Update user | ✅ Yes | **Path**: `id` (Long)<br>**Body**: `{ "username": "string", "email": "string", "password": "string" }` |
| DELETE | `/{id}` | Delete user | ✅ Yes | **Path**: `id` (Long) |

**Example Request Bodies:**

**Register User:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Login:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

---

### 2. Product Service (Port 8082)

#### Base URL: `http://localhost:8082/api/products`

| Method | Endpoint | Description | Auth Required | Parameters |
|--------|----------|-------------|---------------|-------------|
| GET | `/{id}` | Get product by ID | ✅ Yes | **Path**: `id` (Long) |
| GET | `/` | Get all products | ✅ Yes | **Query**: `category` (String, optional)<br>**Query**: `search` (String, optional) |
| POST | `/` | Create a new product | ✅ Yes | **Body**: `{ "name": "string", "description": "string", "price": 0.0, "stock": 0, "category": "string" }` |
| PUT | `/{id}` | Update product | ✅ Yes | **Path**: `id` (Long)<br>**Body**: `{ "name": "string", "description": "string", "price": 0.0, "stock": 0, "category": "string" }` |
| PATCH | `/{id}/stock` | Update product stock | ✅ Yes | **Path**: `id` (Long)<br>**Query**: `quantity` (Integer) |
| DELETE | `/{id}` | Delete product | ✅ Yes | **Path**: `id` (Long) |

**Example Request Bodies:**

**Create Product:**
```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics"
}
```

**Update Stock:**
- Path: `/products/1/stock?quantity=100`

**Get Products by Category:**
- URL: `/products?category=Electronics`

**Search Products:**
- URL: `/products?search=laptop`

---

### 3. Order Service (Port 8083)

#### Base URL: `http://localhost:8083/api/orders`

| Method | Endpoint | Description | Auth Required | Parameters |
|--------|----------|-------------|---------------|-------------|
| GET | `/{id}` | Get order by ID | ✅ Yes | **Path**: `id` (Long) |
| GET | `/user/{userId}` | Get orders by user ID | ✅ Yes | **Path**: `userId` (Long) |
| GET | `/` | Get all orders | ✅ Yes | None |
| POST | `/` | Create a new order | ✅ Yes | **Body**: `{ "userId": 0, "items": [ { "productId": 0, "quantity": 0 } ] }` |
| PATCH | `/{id}/status` | Update order status | ✅ Yes | **Path**: `id` (Long)<br>**Query**: `status` (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED) |
| PATCH | `/{id}/payment-status` | Update payment status | ✅ Yes | **Path**: `id` (Long)<br>**Query**: `paymentStatus` (PENDING, PAID, FAILED, REFUNDED) |

**Example Request Bodies:**

**Create Order:**
```json
{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**Update Order Status:**
- Path: `/orders/1/status?status=CONFIRMED`
- Valid statuses: `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`

**Update Payment Status:**
- Path: `/orders/1/payment-status?paymentStatus=PAID`
- Valid statuses: `PENDING`, `PAID`, `FAILED`, `REFUNDED`

---

### 4. Inventory Service (Port 8084)

#### Base URL: `http://localhost:8084/api/inventory`

| Method | Endpoint | Description | Auth Required | Parameters |
|--------|----------|-------------|---------------|-------------|
| GET | `/product/{productId}` | Get inventory by product ID | ✅ Yes | **Path**: `productId` (Long) |
| GET | `/` | Get all inventory | ✅ Yes | None |
| POST | `/product/{productId}` | Create inventory for product | ✅ Yes | **Path**: `productId` (Long)<br>**Query**: `initialQuantity` (Integer) |
| PATCH | `/product/{productId}/quantity` | Update quantity | ✅ Yes | **Path**: `productId` (Long)<br>**Query**: `quantityChange` (Integer) |
| PATCH | `/product/{productId}/reserve` | Reserve quantity | ✅ Yes | **Path**: `productId` (Long)<br>**Query**: `quantity` (Integer) |
| PATCH | `/product/{productId}/release` | Release reservation | ✅ Yes | **Path**: `productId` (Long)<br>**Query**: `quantity` (Integer) |
| PATCH | `/product/{productId}/confirm` | Confirm reservation | ✅ Yes | **Path**: `productId` (Long)<br>**Query**: `quantity` (Integer) |

**Example Requests:**

**Create Inventory:**
- Path: `/inventory/product/1?initialQuantity=100`

**Update Quantity:**
- Path: `/inventory/product/1/quantity?quantityChange=50`
- (Positive for increase, negative for decrease)

**Reserve Quantity:**
- Path: `/inventory/product/1/reserve?quantity=10`

**Release Reservation:**
- Path: `/inventory/product/1/release?quantity=10`

**Confirm Reservation:**
- Path: `/inventory/product/1/confirm?quantity=10`

---

### 5. Payment Service (Port 8085)

#### Base URL: `http://localhost:8085/api/payments`

| Method | Endpoint | Description | Auth Required | Parameters |
|--------|----------|-------------|---------------|-------------|
| GET | `/{id}` | Get payment by ID | ✅ Yes | **Path**: `id` (Long) |
| GET | `/transaction/{transactionId}` | Get payment by transaction ID | ✅ Yes | **Path**: `transactionId` (String) |
| GET | `/order/{orderId}` | Get payments by order ID | ✅ Yes | **Path**: `orderId` (Long) |
| GET | `/user/{userId}` | Get payments by user ID | ✅ Yes | **Path**: `userId` (Long) |
| GET | `/` | Get all payments | ✅ Yes | None |
| POST | `/` | Process payment | ✅ Yes | **Body**: `{ "orderId": 0, "userId": 0, "amount": 0.0, "paymentMethod": "string" }` |
| POST | `/{id}/refund` | Refund payment | ✅ Yes | **Path**: `id` (Long) |

**Example Request Bodies:**

**Process Payment:**
```json
{
  "orderId": 1,
  "userId": 1,
  "amount": 199.98,
  "paymentMethod": "CREDIT_CARD"
}
```

**Valid Payment Methods:**
- `CREDIT_CARD`
- `DEBIT_CARD`
- `PAYPAL`
- `BANK_TRANSFER`

---

## 🔐 Authentication Setup

### Method 1: Using Swagger UI Authorize Button (Recommended)

1. Login using `/users/login` endpoint
2. Copy the JWT token from response
3. Click the **"Authorize"** button (🔒) at the top right
4. Enter: `Bearer <your-token>` or just `<your-token>`
5. Click "Authorize" and "Close"
6. All subsequent API calls will include the token automatically

### Method 2: Manual Header (Alternative)

If the Authorize button doesn't work, you can manually add the header:

1. In Swagger UI, expand any endpoint
2. Look for "Parameters" section
3. Some endpoints may show "Headers" or you can add custom headers
4. Add header: `Authorization: Bearer <your-token>`

---

## 🧪 Complete Testing Workflow

### Recommended Testing Order:

1. **User Service** (http://localhost:8081/swagger-ui/index.html)
   - ✅ Register a new user (`POST /users/register`)
   - ✅ Login (`POST /users/login`) - **Save the token!**
   - ✅ Authorize in Swagger UI
   - ✅ Get all users (`GET /users`)
   - ✅ Get user by ID (`GET /users/{id}`)

2. **Product Service** (http://localhost:8082/swagger-ui/index.html)
   - ✅ Authorize with the same token
   - ✅ Create a product (`POST /products`)
   - ✅ Get all products (`GET /products`)
   - ✅ Get product by ID (`GET /products/{id}`)
   - ✅ Update product stock (`PATCH /products/{id}/stock`)

3. **Inventory Service** (http://localhost:8084/swagger-ui/index.html)
   - ✅ Authorize with the same token
   - ✅ Create inventory (`POST /inventory/product/{productId}`)
   - ✅ Get inventory by product (`GET /inventory/product/{productId}`)
   - ✅ Reserve quantity (`PATCH /inventory/product/{productId}/reserve`)

4. **Order Service** (http://localhost:8083/swagger-ui/index.html)
   - ✅ Authorize with the same token
   - ✅ Create an order (`POST /orders`)
   - ✅ Get order by ID (`GET /orders/{id}`)
   - ✅ Get orders by user (`GET /orders/user/{userId}`)
   - ✅ Update order status (`PATCH /orders/{id}/status`)

5. **Payment Service** (http://localhost:8085/swagger-ui/index.html)
   - ✅ Authorize with the same token
   - ✅ Process payment (`POST /payments`)
   - ✅ Get payment by ID (`GET /payments/{id}`)
   - ✅ Get payments by order (`GET /payments/order/{orderId}`)

---

## 📊 Response Examples

### Successful Login Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com"
}
```

### Product Response:
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics"
}
```

### Order Response:
```json
{
  "id": 1,
  "userId": 1,
  "totalAmount": 1999.98,
  "status": "PENDING",
  "paymentStatus": "PENDING",
  "orderItems": [
    {
      "productId": 1,
      "productName": "Laptop",
      "quantity": 2,
      "price": 999.99,
      "subtotal": 1999.98
    }
  ],
  "createdAt": "2025-11-16T12:00:00"
}
```

---

## 🚨 Common Issues & Solutions

### Issue 1: "401 Unauthorized"
**Solution**: Make sure you've:
- Logged in successfully
- Copied the token correctly
- Clicked "Authorize" in Swagger UI
- Token hasn't expired (tokens expire after 24 hours)

### Issue 2: "404 Not Found"
**Solution**: 
- Verify the service is running (check health endpoint)
- Check the correct port number
- Ensure the endpoint path is correct

### Issue 3: "500 Internal Server Error"
**Solution**:
- Check service logs
- Verify database connections
- Ensure required data exists (e.g., product exists before creating order)

### Issue 4: Swagger UI Not Loading
**Solution**:
- Verify service is running: `http://localhost:PORT/actuator/health`
- Try accessing API docs directly: `http://localhost:PORT/v3/api-docs`
- Check browser console for errors

---

## 💡 Tips for Effective Testing

1. **Start with User Service**: Always register/login first to get a token
2. **Use the same token**: JWT tokens work across all services
3. **Test in order**: User → Product → Inventory → Order → Payment
4. **Save IDs**: Note down created IDs (user ID, product ID, etc.) for subsequent tests
5. **Check responses**: Verify response status codes and data
6. **Test error cases**: Try invalid data to test validation
7. **Use filters**: Use query parameters for filtering (category, search, etc.)

---

## 🔗 Quick Links

- **Eureka Dashboard**: http://localhost:8761
- **Zipkin Tracing**: http://localhost:9411
- **Health Checks**: Use the test script: `.\test-health-endpoints.ps1`

---

## 📝 Notes

- All timestamps are in ISO 8601 format
- All monetary values are in decimal format (e.g., 99.99)
- JWT tokens are valid for 24 hours
- Some endpoints may require specific data to exist first (e.g., product must exist before creating inventory)

---

**Happy Testing! 🎉**

