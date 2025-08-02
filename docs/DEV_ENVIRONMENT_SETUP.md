# Development Environment Setup Guide

This guide will help you set up a complete development environment for the SalesForge multi-module Spring Boot application on a new computer.

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 17 or higher**
   - Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation: `java --version` and `javac --version`

2. **Git**
   - Download from [git-scm.com](https://git-scm.com/)
   - Verify installation: `git --version`

3. **PostgreSQL Database**
   - Download from [postgresql.org](https://www.postgresql.org/download/)
   - Alternative: Use Docker (see Docker option below)

4. **IDE/Text Editor** (Choose one)
   - [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Recommended)
   - [Visual Studio Code](https://code.visualstudio.com/) with Java extensions
   - [Eclipse IDE](https://www.eclipse.org/ide/)

### Optional but Recommended

5. **Docker Desktop** (Alternative to local PostgreSQL)
   - Download from [docker.com](https://www.docker.com/products/docker-desktop/)
   - Useful for running PostgreSQL in containers

6. **Postman or Similar API Client**
   - Download from [postman.com](https://www.postman.com/)
   - For testing REST APIs

## Step-by-Step Setup

### 1. Clone the Repository

```bash
# Clone the repository
git clone https://github.com/JoshuaKento/salesforge.git

# Navigate to the project directory
cd salesforge

# Switch to the my-sfa-app directory
cd my-sfa-app
```

### 2. Verify Java Installation

```bash
# Check Java version (should be 17+)
java --version

# Check if JAVA_HOME is set correctly
echo $JAVA_HOME  # Linux/Mac
echo %JAVA_HOME% # Windows

# If JAVA_HOME is not set, add it to your environment variables
# Example for Windows: set JAVA_HOME=C:\Program Files\Java\jdk-17
# Example for Linux/Mac: export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### 3. Set Up PostgreSQL Database

#### Option A: Local PostgreSQL Installation

1. **Install PostgreSQL** (if not already installed)
2. **Create Database and User:**

```sql
-- Connect to PostgreSQL as superuser (usually 'postgres')
psql -U postgres

-- Create database
CREATE DATABASE sfa_db;

-- Create user
CREATE USER sfa_user WITH PASSWORD 'sfa_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE sfa_db TO sfa_user;

-- Exit psql
\q
```

#### Option B: Docker PostgreSQL (Recommended for Development)

```bash
# Pull PostgreSQL image
docker pull postgres:15-alpine

# Run PostgreSQL container
docker run --name sfa-postgres \
  -e POSTGRES_DB=sfa_db \
  -e POSTGRES_USER=sfa_user \
  -e POSTGRES_PASSWORD=sfa_password \
  -p 5432:5432 \
  -d postgres:15-alpine

# Verify container is running
docker ps
```

### 4. Configure Application Properties

The application is pre-configured with default database settings. If you used different credentials, update:

**File:** `web/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sfa_db
    username: sfa_user  # Change if different
    password: sfa_password  # Change if different
```

### 5. Build and Run the Application

```bash
# Make gradlew executable (Linux/Mac only)
chmod +x gradlew

# Build the entire project
./gradlew build

# Run the application
./gradlew :web:bootRun
```

**For Windows:**
```cmd
# Build the project
gradlew.bat build

# Run the application
gradlew.bat :web:bootRun
```

### 6. Verify Installation

1. **Check Application Startup:**
   - Application should start on `http://localhost:8080`
   - Look for "Started Application" message in logs

2. **Test Database Connection:**
   - Flyway migrations should run automatically
   - Check logs for "Migrating schema" messages

3. **Test API Endpoints:**
   ```bash
   # Health check
   curl http://localhost:8080/actuator/health
   
   # Should return: {"status":"UP"}
   ```

4. **Access API Documentation:**
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 7. Test Authentication

```bash
# Login with sample user (password: password123)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "sales@example.com",
    "password": "password123"
  }'

# Response should include JWT token
```

## IDE-Specific Setup

### IntelliJ IDEA

1. **Import Project:**
   - Open IntelliJ IDEA
   - Choose "Open" and select the `my-sfa-app` folder
   - IntelliJ should auto-detect it as a Gradle project

2. **Configure SDK:**
   - Go to File → Project Structure → Project
   - Set Project SDK to Java 17+

3. **Enable Annotation Processing:**
   - Go to File → Settings → Build → Compiler → Annotation Processors
   - Check "Enable annotation processing" (for Lombok)

4. **Install Lombok Plugin:**
   - Go to File → Settings → Plugins
   - Search and install "Lombok"
   - Restart IDE

### Visual Studio Code

1. **Install Java Extensions:**
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. **Open Project:**
   - Open the `my-sfa-app` folder in VS Code

## Development Workflow

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:test
./gradlew :web:test

# Run with coverage
./gradlew test jacocoTestReport
```

### Working with Database

```bash
# Connect to database
psql -h localhost -U sfa_user -d sfa_db

# View tables
\dt

# Check sample data
SELECT * FROM users;
SELECT * FROM leads;
```

### Hot Reload Development

```bash
# Run with continuous build
./gradlew :web:bootRun --continuous

# Or use Spring Boot DevTools (already included)
# Changes to classes will trigger automatic restart
```

## Troubleshooting

### Common Issues

1. **"Port 8080 already in use"**
   ```bash
   # Find process using port 8080
   netstat -ano | findstr :8080  # Windows
   lsof -i :8080                 # Linux/Mac
   
   # Kill the process or change port in application.yml
   ```

2. **Database Connection Issues**
   ```bash
   # Verify PostgreSQL is running
   docker ps  # If using Docker
   systemctl status postgresql  # Linux
   
   # Test connection manually
   psql -h localhost -U sfa_user -d sfa_db
   ```

3. **Gradle Build Failures**
   ```bash
   # Clean and rebuild
   ./gradlew clean build
   
   # Check Java version
   ./gradlew --version
   ```

4. **Lombok Not Working**
   - Ensure IDE has Lombok plugin installed
   - Enable annotation processing in IDE settings
   - Restart IDE after configuration

5. **Test Failures**
   ```bash
   # Run tests with more verbose output
   ./gradlew test --info
   
   # Skip tests temporarily
   ./gradlew build -x test
   ```

### Environment Variables

If you need to customize configuration:

```bash
# Database configuration
export DB_URL=jdbc:postgresql://localhost:5432/sfa_db
export DB_USERNAME=sfa_user
export DB_PASSWORD=sfa_password

# JWT configuration
export JWT_SECRET=your-secret-key-here
export JWT_EXPIRATION=86400000
```

### Useful Gradle Commands

```bash
# List all tasks
./gradlew tasks

# Build specific module
./gradlew :core:build

# Run application with profile
./gradlew :web:bootRun --args='--spring.profiles.active=dev'

# Generate dependency report
./gradlew dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

## Next Steps

Once your environment is set up:

1. **Explore the API:**
   - Use Swagger UI at `http://localhost:8080/swagger-ui.html`
   - Test authentication and CRUD operations

2. **Review the Code:**
   - Start with domain entities in the `core` module
   - Understand the security configuration in `security` module
   - Check repository patterns in `infra` module
   - Review REST controllers in `web` module

3. **Development Best Practices:**
   - Write tests for new features
   - Follow the existing code structure and patterns
   - Use the provided validation and error handling
   - Maintain the modular architecture

## Getting Help

- **Documentation:** Check the `docs/` folder for additional documentation
- **Issues:** Report problems in the project's issue tracker
- **Code Review:** Follow the project's contribution guidelines

---

**Note:** This guide assumes you're setting up a development environment. For production deployment, additional security and performance configurations are required.