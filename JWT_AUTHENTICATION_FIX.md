# JWT Authentication 403 Error - Issue & Fix

## 🔴 Problem

Getting **403 Forbidden** errors when trying to access authenticated endpoints (GET /users/{id}, GET /users, etc.) even with a valid JWT token.

### Root Cause

The `SecurityConfig` was configured to require authentication for protected endpoints, but **no JWT authentication filter was implemented** to actually validate the JWT tokens from the Authorization header.

## ✅ Solution

Created a `JwtAuthenticationFilter` that:
1. Extracts JWT token from `Authorization: Bearer <token>` header
2. Validates the token signature and expiration
3. Sets the authentication in Spring Security context
4. Allows the request to proceed if token is valid

### Files Created/Modified

1. **Created**: `user-service/src/main/java/com/ecommerce/user/filter/JwtAuthenticationFilter.java`
   - Extends `OncePerRequestFilter`
   - Validates JWT tokens
   - Sets authentication context

2. **Modified**: `user-service/src/main/java/com/ecommerce/user/config/SecurityConfig.java`
   - Added `@Autowired JwtAuthenticationFilter`
   - Added filter to security chain: `.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`

## 🔧 How It Works

1. **Request comes in** with `Authorization: Bearer <token>` header
2. **JwtAuthenticationFilter** intercepts the request
3. **Extracts token** from header (removes "Bearer " prefix)
4. **Validates token**:
   - Checks signature (via `extractUsername()`)
   - Checks expiration (via `isTokenExpired()`)
5. **Sets authentication** in SecurityContext if valid
6. **Request proceeds** to controller

## 📝 Testing

After the service rebuilds and starts:

1. **Login** to get a token:
   ```bash
   POST http://localhost:8081/users/login
   {
     "username": "sharanya",
     "password": "password"
   }
   ```

2. **Use the token** in Authorization header:
   ```bash
   GET http://localhost:8081/users/1
   Authorization: Bearer <your-token>
   ```

3. **Should now work** without 403 errors!

## ⚠️ Important Notes

- Token must be in format: `Bearer <token>` (with space after Bearer)
- Token must not be expired
- Token signature must be valid (signed with correct secret key)
- The filter runs before Spring Security's authentication filter

## 🔄 Next Steps

1. Wait for user-service to fully start (check health endpoint)
2. Test the authenticated endpoints with your JWT token
3. All authenticated endpoints should now work correctly

