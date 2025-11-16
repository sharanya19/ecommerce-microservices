# Corrected API Endpoints

## Issue Fixed: 403 Forbidden Error

The security configuration has been updated to match the actual controller endpoints.

## ✅ Correct Endpoints

### User Service (Port 8081)

**Base URL**: `http://localhost:8081`

| Endpoint | Method | Full URL | Status |
|----------|--------|----------|--------|
| Register User | POST | `http://localhost:8081/users/register` | ✅ Fixed |
| Login | POST | `http://localhost:8081/users/login` | ✅ Fixed |
| Get User by ID | GET | `http://localhost:8081/users/{id}` | ✅ Requires Auth |
| Get All Users | GET | `http://localhost:8081/users` | ✅ Requires Auth |

### Corrected cURL Command

**Register User:**
```bash
curl -X 'POST' \
  'http://localhost:8081/users/register' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User",
  "phone": "98325623789",
  "address": "hyd",
  "role": "CUSTOMER"
}'
```

**Note**: You don't need to include:
- `id: 0` (auto-generated)
- `createdAt` (auto-generated)
- `updatedAt` (auto-generated)

**Login:**
```bash
curl -X 'POST' \
  'http://localhost:8081/users/login' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "username": "testuser",
  "password": "password123"
}'
```

## 🔧 What Was Fixed

The security configuration was checking for `/api/users/register` but the controller endpoint is `/users/register`. The security config has been updated to allow:
- `/users/register`
- `/users/login`

## 📝 Using Swagger UI

1. Open: http://localhost:8081/swagger-ui/index.html
2. Find the `/users/register` endpoint
3. Click "Try it out"
4. Use this minimal request body:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User",
  "role": "CUSTOMER"
}
```

The service should now accept registration requests without the 403 error!

