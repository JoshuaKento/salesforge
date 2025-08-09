# 🧪 Authentication Endpoints Testing Guide

## Prerequisites
- Start PostgreSQL: `docker-compose up -d postgres`
- Clean the database: `docker-compose exec postgres psql -U sfa_user -d sfa_db -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"`
- Start the app: `DB_URL=jdbc:postgresql://localhost:5432/sfa_db DB_USERNAME=sfa_user DB_PASSWORD=sfa_password ./gradlew :web:bootRun --args="--spring.profiles.active=dev"`

## Test Endpoints

### 1. Health Check
```bash
curl -X GET http://localhost:8080/api/v1/auth/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "service": "Authentication Service",
  "timestamp": "2025-08-08T..."
}
```

### 2. User Registration
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

**Expected Response (Success):**
```json
{
  "message": "User registered successfully",
  "userId": 1,
  "email": "john.doe@example.com"
}
```

**Expected Response (Error - User Exists):**
```json
{
  "error": "User with email john.doe@example.com already exists"
}
```

### 3. User Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

**Expected Response (Success):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "email": "john.doe@example.com",
  "firstName": "John", 
  "lastName": "Doe",
  "role": "SALES_REP",
  "expiresAt": "2025-08-08T15:13:27.123"
}
```

**Expected Response (Error - Invalid Credentials):**
```json
{
  "error": "Invalid email or password"
}
```

### 4. Test Protected Endpoint (Future Use)
```bash
# After getting token from login, test protected endpoint:
TOKEN="your-jwt-token-here"

curl -X GET http://localhost:8080/api/v1/leads \
  -H "Authorization: Bearer $TOKEN"
```

## Testing Scenarios

### Scenario 1: Happy Path
1. Register new user → Should get 201 Created
2. Login with same credentials → Should get JWT token
3. Use token for protected endpoints → Should work

### Scenario 2: Error Cases
1. Register with invalid email → Should get validation error
2. Register with mismatched passwords → Should get error
3. Register same email twice → Should get "user exists" error
4. Login with wrong password → Should get unauthorized
5. Access protected endpoint without token → Should get 401

### Scenario 3: Validation Testing
1. Register with short password → Should get validation error
2. Register without required fields → Should get validation errors
3. Register with invalid email format → Should get validation error

## PowerShell Testing Script

```powershell
# PowerShell script for Windows testing
$baseUrl = "http://localhost:8080/api/v1/auth"

# Test health check
Write-Host "Testing health endpoint..."
Invoke-RestMethod -Uri "$baseUrl/health" -Method Get

# Test registration
Write-Host "Testing user registration..."
$registerData = @{
    firstName = "Jane"
    lastName = "Smith"
    email = "jane.smith@example.com"
    password = "password123"
    confirmPassword = "password123"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$baseUrl/register" -Method Post -Body $registerData -ContentType "application/json"
    Write-Host "Registration successful: $($registerResponse | ConvertTo-Json)"
} catch {
    Write-Host "Registration error: $($_.Exception.Message)"
}

# Test login
Write-Host "Testing user login..."
$loginData = @{
    email = "jane.smith@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/login" -Method Post -Body $loginData -ContentType "application/json"
    Write-Host "Login successful! Token: $($loginResponse.accessToken.Substring(0,20))..."
} catch {
    Write-Host "Login error: $($_.Exception.Message)"
}
```

## Using Postman Collection

Import this collection into Postman:

```json
{
  "info": {
    "name": "SalesForge Auth Tests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{base_url}}/auth/health",
          "host": ["{{base_url}}"],
          "path": ["auth", "health"]
        }
      }
    },
    {
      "name": "Register User",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"firstName\": \"Test\",\n  \"lastName\": \"User\",\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\",\n  \"confirmPassword\": \"password123\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/auth/register",
          "host": ["{{base_url}}"],
          "path": ["auth", "register"]
        }
      }
    },
    {
      "name": "Login User",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/auth/login",
          "host": ["{{base_url}}"],
          "path": ["auth", "login"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api/v1"
    }
  ]
}
```

## Quick Database Reset

```bash
# Reset PostgreSQL database for fresh testing
docker-compose exec postgres psql -U sfa_user -d sfa_db -c "
  DROP SCHEMA public CASCADE; 
  CREATE SCHEMA public;
  GRANT ALL ON SCHEMA public TO sfa_user;
  GRANT ALL ON SCHEMA public TO public;
"
```