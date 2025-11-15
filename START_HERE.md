# Quick Start Guide

## Prerequisites Check

Before running the application, ensure:

1. **Docker Desktop is Running**
   - Open Docker Desktop application
   - Wait until it shows "Docker Desktop is running"
   - Verify with: `docker ps`

2. **Ports Available**
   - Port 3306 (MySQL)
   - Port 6379 (Redis)
   - Port 9092 (Kafka)
   - Port 8761 (Eureka)
   - Port 8888 (Config Server)
   - Port 8080 (API Gateway)
   - Ports 8081-8085 (Services)

## Step-by-Step Startup

### Step 1: Start Docker Desktop
- Open Docker Desktop application
- Wait for it to fully start

### Step 2: Start Infrastructure Services
```bash
cd C:\Users\Guest-pro\Desktop\ecommerce-microservices
docker compose up -d mysql redis zookeeper kafka
```

Wait 30-60 seconds for services to be ready.

### Step 3: Build and Start Application Services
```bash
# Build and start all services (this will take several minutes on first run)
docker compose up -d --build
```

### Step 4: Check Service Status
```bash
# Check all containers
docker compose ps

# Check logs
docker compose logs -f eureka-server
```

### Step 5: Verify Services

1. **Eureka Dashboard**: http://localhost:8761
   - Should show all registered services

2. **API Gateway Health**: http://localhost:8080/actuator/health

3. **Test API**:
   ```bash
   # Get all users
   curl http://localhost:8080/api/users
   
   # Get all products
   curl http://localhost:8080/api/products
   ```

## Troubleshooting

### If services fail to start:

1. **Check Docker is running**:
   ```bash
   docker ps
   ```

2. **Check logs**:
   ```bash
   docker compose logs service-name
   ```

3. **Restart services**:
   ```bash
   docker compose down
   docker compose up -d --build
   ```

### Common Issues:

- **Port already in use**: Stop the service using that port
- **Build fails**: Check Docker has enough resources (4GB+ RAM)
- **Services not registering**: Wait 1-2 minutes for Eureka registration

## Expected Startup Time

- First build: 10-15 minutes (downloading images and building)
- Subsequent starts: 2-3 minutes

## Service URLs

- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080
- Config Server: http://localhost:8888
- User Service: http://localhost:8081
- Product Service: http://localhost:8082
- Order Service: http://localhost:8083
- Inventory Service: http://localhost:8084
- Payment Service: http://localhost:8085

