# SalesForge API Documentation

## Overview
SalesForge provides a RESTful API for managing sales leads, accounts, contacts, opportunities, and user authentication. The API follows REST conventions and returns JSON responses.

**Base URL:** `http://localhost:8080/api/v1`

**API Documentation:** `http://localhost:8080/swagger-ui.html`

**OpenAPI Spec:** `http://localhost:8080/v3/api-docs`

## Authentication

The API uses JWT (JSON Web Token) based authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

## User Roles
- **ADMIN**: Full system access
- **MANAGER**: Sales management and reporting access
- **SALES_REP**: Basic CRUD operations for assigned leads/accounts

---

## Authentication Endpoints

### Register User
```http
POST /api/v1/auth/register
```

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "confirmPassword": "securePassword123"
}
```

**Response (201):**
```json
{
  "message": "User registered successfully",
  "userId": 1,
  "email": "john.doe@example.com"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "password": "securePassword123",
    "confirmPassword": "securePassword123"
  }'
```

### Login User
```http
POST /api/v1/auth/login
```

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response (200):**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe", 
  "role": "SALES_REP",
  "expiresAt": "2025-08-10T10:30:00"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }'
```

### Logout User
```http
POST /api/v1/auth/logout
```

**Response (200):**
```json
{
  "message": "Logged out successfully"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Auth Health Check
```http
GET /api/v1/auth/health
```

**Response (200):**
```json
{
  "status": "UP",
  "service": "Authentication Service", 
  "timestamp": "2025-08-09T19:30:00"
}
```

---

## Lead Management Endpoints

### Get All Leads
```http
GET /api/v1/leads?page=0&size=20&sort=createdAt,desc
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort criteria (e.g., `createdAt,desc`)

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "companyName": "Acme Corp",
      "contactName": "Jane Smith",
      "email": "jane.smith@acme.com",
      "phone": "+1-555-0123",
      "status": "NEW",
      "source": "WEBSITE",
      "createdAt": "2025-08-09T10:30:00",
      "updatedAt": "2025-08-09T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/v1/leads?page=0&size=10" \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Get Lead by ID
```http
GET /api/v1/leads/{id}
```

**Response (200):**
```json
{
  "id": 1,
  "companyName": "Acme Corp",
  "contactName": "Jane Smith",
  "email": "jane.smith@acme.com",
  "phone": "+1-555-0123",
  "status": "NEW",
  "source": "WEBSITE",
  "notes": "Interested in enterprise solution",
  "createdAt": "2025-08-09T10:30:00",
  "updatedAt": "2025-08-09T10:30:00"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/v1/leads/1 \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Create New Lead
```http
POST /api/v1/leads
```

**Request Body:**
```json
{
  "companyName": "Tech Startup Inc",
  "contactName": "Bob Johnson", 
  "email": "bob@techstartup.com",
  "phone": "+1-555-0456",
  "status": "NEW",
  "source": "REFERRAL",
  "notes": "Looking for CRM solution"
}
```

**Response (201):**
```json
{
  "id": 2,
  "companyName": "Tech Startup Inc",
  "contactName": "Bob Johnson",
  "email": "bob@techstartup.com", 
  "phone": "+1-555-0456",
  "status": "NEW",
  "source": "REFERRAL",
  "notes": "Looking for CRM solution",
  "createdAt": "2025-08-09T11:00:00",
  "updatedAt": "2025-08-09T11:00:00"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/leads \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Tech Startup Inc",
    "contactName": "Bob Johnson",
    "email": "bob@techstartup.com",
    "phone": "+1-555-0456", 
    "status": "NEW",
    "source": "REFERRAL",
    "notes": "Looking for CRM solution"
  }'
```

### Update Lead
```http
PUT /api/v1/leads/{id}
```

**Request Body:**
```json
{
  "companyName": "Tech Startup Inc",
  "contactName": "Bob Johnson",
  "email": "bob.johnson@techstartup.com",
  "phone": "+1-555-0456",
  "status": "QUALIFIED", 
  "source": "REFERRAL",
  "notes": "Qualified lead - ready for demo"
}
```

**Response (200):**
```json
{
  "id": 2,
  "companyName": "Tech Startup Inc",
  "contactName": "Bob Johnson",
  "email": "bob.johnson@techstartup.com",
  "phone": "+1-555-0456",
  "status": "QUALIFIED",
  "source": "REFERRAL", 
  "notes": "Qualified lead - ready for demo",
  "createdAt": "2025-08-09T11:00:00",
  "updatedAt": "2025-08-09T11:15:00"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/v1/leads/2 \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Tech Startup Inc",
    "contactName": "Bob Johnson",
    "email": "bob.johnson@techstartup.com",
    "phone": "+1-555-0456",
    "status": "QUALIFIED",
    "source": "REFERRAL",
    "notes": "Qualified lead - ready for demo"
  }'
```

### Delete Lead
```http
DELETE /api/v1/leads/{id}
```

**Response (204):** No content

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/v1/leads/2 \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Get Leads by Status
```http
GET /api/v1/leads/status/{status}
```

**Valid Status Values:**
- `NEW` - New leads
- `CONTACTED` - Leads that have been contacted
- `QUALIFIED` - Qualified leads
- `LOST` - Lost leads

**Response (200):**
```json
[
  {
    "id": 1,
    "companyName": "Acme Corp",
    "contactName": "Jane Smith",
    "email": "jane.smith@acme.com",
    "phone": "+1-555-0123",
    "status": "QUALIFIED",
    "source": "WEBSITE",
    "createdAt": "2025-08-09T10:30:00",
    "updatedAt": "2025-08-09T10:45:00"
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/v1/leads/status/QUALIFIED \
  -H "Authorization: Bearer <your_jwt_token>"
```

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "Invalid input data. Please check your request."
}
```

### 401 Unauthorized
```json
{
  "error": "Invalid credentials or token expired"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied - insufficient permissions"
}
```

### 404 Not Found
```json
{
  "error": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "An unexpected error occurred. Please try again."
}
```

---

## Data Models

### User
```json
{
  "id": "Long",
  "firstName": "String",
  "lastName": "String", 
  "email": "String (unique)",
  "role": "ADMIN | MANAGER | SALES_REP",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### Lead
```json
{
  "id": "Long",
  "companyName": "String (required)",
  "contactName": "String (required)",
  "email": "String (required, email format)",
  "phone": "String",
  "status": "NEW | CONTACTED | QUALIFIED | LOST",
  "source": "WEBSITE | EMAIL | PHONE | REFERRAL | SOCIAL_MEDIA | OTHER",
  "notes": "String",
  "assignedTo": "User",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

---

## Rate Limiting

The API implements rate limiting to ensure fair usage:
- **Authentication endpoints**: 5 requests per minute per IP
- **CRUD endpoints**: 100 requests per minute per user
- **Read-only endpoints**: 200 requests per minute per user

---

## Postman Collection

You can import the API collection into Postman using the OpenAPI spec:

1. Open Postman
2. Click "Import" 
3. Select "Link" tab
4. Enter: `http://localhost:8080/v3/api-docs`
5. Click "Continue" and "Import"

---

## Development Setup

### Start the Application
```bash
cd my-sfa-app
./gradlew :web:bootRun --args="--spring.profiles.active=dev"
```

### Access Swagger UI
Open your browser and navigate to: http://localhost:8080/swagger-ui.html

### Test Authentication Flow
1. Register a new user via `/api/v1/auth/register`
2. Login to get JWT token via `/api/v1/auth/login`  
3. Use the token in Authorization header for protected endpoints
4. Test CRUD operations on leads

---

## Support

For API support and questions:
- **Documentation**: http://localhost:8080/swagger-ui.html  
- **Health Check**: http://localhost:8080/actuator/health
- **Application Metrics**: http://localhost:8080/actuator/metrics

---

*Last Updated: August 9, 2025*
*API Version: 1.0.0*