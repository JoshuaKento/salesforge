# SalesForge Development Commands

## Windows System Commands
Since the project runs on Windows, use these system commands:
- `dir`: List directory contents (instead of `ls`)
- `cd`: Change directory
- `type`: Display file contents (instead of `cat`)
- `find`: Search within files
- `findstr`: Regex search within files
- `xcopy`: Copy files/directories
- `del`: Delete files
- `rmdir`: Remove directories

## Primary Development Commands (from project root)
All commands should be run from the `my-sfa-app` directory:

### Build Commands
```bash
# Build the entire project
gradlew.bat build

# Clean build
gradlew.bat clean build

# Build without tests (for faster builds)
gradlew.bat build -x test
```

### Run Commands
```bash
# Run the application (default port 8080)
gradlew.bat bootRun

# Run with specific port
gradlew.bat :web:bootRun --args="--server.port=8081"

# Run with database environment variables
DB_URL=jdbc:postgresql://localhost:5432/sfa_db DB_USERNAME=sfa_user DB_PASSWORD=sfa_password gradlew.bat :web:bootRun
```

### Test Commands  
```bash
# Run all tests
gradlew.bat test

# Run tests for specific module
gradlew.bat :web:test
gradlew.bat :core:test
gradlew.bat :security:test
gradlew.bat :infra:test

# Run tests with detailed output
gradlew.bat test --info
```

### Database Commands
```bash
# Start PostgreSQL with Docker Compose (from root directory)
docker-compose up -d

# Stop database
docker-compose down
```

## Task Completion Commands
When a development task is completed, run these commands in sequence:

1. **Build the project**: `gradlew.bat build`
2. **Run tests**: `gradlew.bat test`
3. **Verify application starts**: `gradlew.bat bootRun` (then Ctrl+C to stop)

Note: No specific linting or formatting tools are configured in this project. The build process includes compilation which catches syntax errors.

## Useful Gradle Tasks
```bash
# List all available tasks
gradlew.bat tasks

# List verification tasks (test, check, etc.)
gradlew.bat tasks --group=verification

# List build tasks
gradlew.bat tasks --group=build

# Show dependencies
gradlew.bat dependencies

# Show project structure
gradlew.bat projects
```

## Development Workflow
1. Make code changes
2. Run `gradlew.bat build` to verify compilation
3. Run `gradlew.bat test` to ensure tests pass  
4. Run `gradlew.bat bootRun` to test the application locally
5. Check logs for any errors or warnings