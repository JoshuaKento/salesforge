# SalesForge Project Structure

## Root Directory Layout
```
salesforge/
├── .github/                    # GitHub Actions CI/CD
├── docs/                       # Project documentation
│   └── sfa-app-spec.md        # Detailed application specification
├── my-sfa-app/                # Main application directory
├── docker-compose.yml         # Database setup
├── CLAUDE.md                  # AI assistant instructions
├── README.md                  # Project overview
└── other docs...
```

## Main Application Structure (my-sfa-app/)
```
my-sfa-app/
├── core/                      # Domain entities and business logic
├── security/                  # Authentication and authorization
├── infra/                     # Infrastructure and data access
├── web/                       # REST controllers and web layer
├── gradle/                    # Gradle wrapper files
├── build.gradle.kts          # Root build configuration
├── settings.gradle.kts       # Multi-module settings
├── gradle.properties         # Java version and JVM settings
├── gradlew                   # Gradle wrapper (Unix)
├── gradlew.bat              # Gradle wrapper (Windows)
├── Dockerfile               # Container configuration
└── .env.template           # Environment variables template
```

## Module Breakdown

### Web Module (my-sfa-app/web/)
- **Main Class**: `com.example.web.Application.java`
- **Controllers**: REST API endpoints in `controller/` package
  - `AuthController.java`: Authentication endpoints (/api/v1/auth/*)
  - `LeadController.java`: Lead management endpoints
- **DTOs**: Request/response objects in `dto/` package
- **Resources**: 
  - `application.yml`: Application configuration
  - Database migrations (Flyway)
  - Static assets

### Package Structure Convention
- **Base Package**: `com.example`
- **Web Layer**: `com.example.web`
- **Controllers**: `com.example.web.controller`
- **DTOs**: `com.example.web.dto`
- **Core Domain**: `com.example.core`
- **Security**: `com.example.security`
- **Infrastructure**: `com.example.infra`

## Key Configuration Files
- **application.yml**: Spring Boot configuration
- **build.gradle.kts**: Dependencies and build settings
- **gradle.properties**: Java version and JVM configuration
- **docker-compose.yml**: PostgreSQL database setup

## Source Code Layout
Each module follows standard Maven/Gradle structure:
```
module/
├── src/
│   ├── main/
│   │   ├── java/           # Java source code
│   │   └── resources/      # Configuration files
│   └── test/
│       ├── java/           # Test source code
│       └── resources/      # Test configuration
└── build.gradle.kts       # Module-specific build config
```

## Dependencies Management
- Parent build file manages common dependencies (Lombok, Spring Boot BOM)
- Each module declares its specific dependencies
- Spring Boot BOM provides version management
- Testcontainers BOM for testing dependencies