# Task Completion Workflow for SalesForge

## Standard Task Completion Process
When any development task is completed, follow these steps in order:

### 1. Build Verification
```bash
# Navigate to my-sfa-app directory
cd my-sfa-app

# Clean and build the project
gradlew.bat clean build
```
This ensures:
- All code compiles successfully
- No compilation errors exist
- Dependencies are resolved correctly

### 2. Test Execution
```bash
# Run all tests
gradlew.bat test
```
This verifies:
- Unit tests pass
- Integration tests pass  
- No regressions introduced

### 3. Application Startup Test
```bash
# Start the application
gradlew.bat bootRun
```
This confirms:
- Application starts without errors
- Spring context loads successfully
- Database connections work (if database is running)
- No runtime configuration issues

Use `Ctrl+C` to stop the application after verifying it starts.

### 4. Database Setup (if needed)
If working with database-related features:
```bash
# Start PostgreSQL (from project root)
docker-compose up -d

# Verify database connectivity by checking application logs
# Look for successful Flyway migrations and JPA startup
```

## Error Handling During Task Completion
- **Build Failures**: Fix compilation errors before proceeding
- **Test Failures**: Address failing tests, don't skip them
- **Startup Issues**: Check application.yml configuration and database connectivity
- **Database Issues**: Verify PostgreSQL is running and credentials are correct

## Environment Variables for Testing
For local development testing:
```bash
DB_URL=jdbc:postgresql://localhost:5432/sfa_db
DB_USERNAME=sfa_user  
DB_PASSWORD=sfa_password
SERVER_PORT=8081
JWT_SECRET=your-secret-key-here
```

## Code Quality Checks
Since no specific linting tools are configured:
- Rely on IDE warnings and errors
- Follow existing code patterns and conventions
- Use Lombok annotations consistently
- Ensure proper exception handling and logging

## Pre-commit Verification
Before committing code changes:
1. ✅ `gradlew.bat build` succeeds
2. ✅ `gradlew.bat test` passes
3. ✅ `gradlew.bat bootRun` starts successfully
4. ✅ Code follows project conventions
5. ✅ Proper error handling implemented
6. ✅ Logging added for important operations