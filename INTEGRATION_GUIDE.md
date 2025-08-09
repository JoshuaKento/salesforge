# SalesForge Integration Guide

This document provides comprehensive instructions for running the full-stack SalesForge application with frontend and backend integration.

## Quick Start

### 1. Start Backend (Terminal 1)

```bash
# Navigate to backend directory
cd "F:\a_Devenv\Sales Forge\salesforge\my-sfa-app"

# Start the Spring Boot application
gradlew.bat :web:bootRun --args="--spring.profiles.active=dev --server.port=8080"

# Or use the environment variable approach:
# DB_URL=jdbc:postgresql://localhost:5432/sfa_db DB_USERNAME=sfa_user DB_PASSWORD=sfa_password SERVER_PORT=8081 gradlew.bat :web:bootRun --args="--spring.profiles.active=dev --server.port=8081"
```

### 2. Start Frontend (Terminal 2)

```bash
# Navigate to frontend directory
cd "F:\a_Devenv\Sales Forge\salesforge\frontend\salesforge-ui"

# Install dependencies (if not done already)
npm install

# Start React development server
npm start
```

The application will be available at:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## Integration Features Completed

### ✅ Authentication Flow
- JWT-based authentication with role-based access control
- Login form with demo credentials: demo@salesforge.com / demo123456
- Token persistence in localStorage
- Automatic logout on token expiry

### ✅ Lead Management Interface
- Complete CRUD operations (Create, Read, Update, Delete)
- Advanced search and filtering capabilities
- Pagination with configurable page sizes
- Role-based permissions (SALES_REP cannot delete leads)
- Real-time filtering by status, source, and search term

### ✅ Responsive Design
- Mobile-first responsive design
- Professional UI with gradient backgrounds
- Status badges with color-coded indicators
- Modern table layout with hover effects
- Modal forms for create/edit operations

### ✅ API Integration
- Comprehensive TypeScript interfaces matching backend DTOs
- Axios-based HTTP client with interceptors
- Error handling and loading states
- Environment-specific API URLs (dev/production)

## API Endpoints Available

### Authentication
- `POST /api/v1/auth/login` - User authentication
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/logout` - User logout

### Lead Management
- `GET /api/v1/leads` - List leads with filtering and pagination
- `POST /api/v1/leads` - Create new lead
- `GET /api/v1/leads/{id}` - Get lead by ID
- `PUT /api/v1/leads/{id}` - Update lead
- `PATCH /api/v1/leads/{id}` - Partial update lead
- `DELETE /api/v1/leads/{id}` - Delete lead
- `GET /api/v1/leads/search` - Advanced search
- `GET /api/v1/leads/source/{source}` - Filter by source
- `GET /api/v1/leads/stats` - Lead statistics

## Database Setup

Ensure PostgreSQL is running with the following configuration:
- **Database**: sfa_db
- **Username**: sfa_user
- **Password**: sfa_password
- **Port**: 5432

## Testing the Integration

1. **Backend Health Check**: http://localhost:8080/actuator/health
2. **API Documentation**: http://localhost:8080/swagger-ui.html
3. **Frontend Login**: Use demo@salesforge.com / demo123456
4. **Create Lead**: Test the create lead functionality
5. **Filter & Search**: Test the real-time filtering capabilities

## Deployment

### Frontend Production Build
```bash
cd frontend/salesforge-ui
npm run build
npm run serve  # Test production build locally
```

### Backend Production Build
```bash
cd my-sfa-app
gradlew.bat build
java -jar web/build/libs/web-*.jar
```

## Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure backend CORS configuration allows frontend origin
2. **API Connection Issues**: Verify backend is running on port 8080
3. **Authentication Issues**: Check if JWT secret is configured
4. **Database Connection**: Ensure PostgreSQL is running and accessible

### Development Tips

1. Use browser dev tools to monitor network requests
2. Check browser console for JavaScript errors
3. Verify backend logs for API request processing
4. Test API endpoints directly using Swagger UI

## Architecture Summary

```
Frontend (React + TypeScript)
├── LoginForm - JWT authentication
├── LeadsTable - Lead management interface
├── LeadForm - Create/edit modal
└── ApiService - HTTP client

Backend (Spring Boot + Java)
├── AuthController - Authentication endpoints
├── LeadController - Lead management endpoints
├── SecurityConfig - JWT + role-based security
└── PostgreSQL Database
```

The application is now fully integrated with a professional, responsive frontend connected to a robust Spring Boot backend with comprehensive lead management capabilities.