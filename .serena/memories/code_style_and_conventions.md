# SalesForge Code Style and Conventions

## Package Structure
- **Root Package**: `com.example`
- **Module Structure**: Multi-module Gradle project
  - `core`: Domain entities and business logic
  - `security`: Authentication and authorization
  - `infra`: Infrastructure and data access
  - `web`: REST controllers and web layer

## Java Conventions
- **Java Version**: 17 source/target compatibility, Java 24 for development
- **Code Style**: Standard Java conventions
- **Annotations**: Heavy use of Spring and Lombok annotations

## Lombok Usage
- `@RequiredArgsConstructor`: Constructor injection
- `@Slf4j`: Logging
- Used across all modules for reducing boilerplate

## Spring Annotations
- `@RestController`: REST API controllers
- `@RequestMapping`: API endpoint mapping (e.g., `/api/v1/auth`)
- `@Valid`: Request validation
- `@SpringBootApplication`: Main application class with component scanning
- `@Operation`: OpenAPI/Swagger documentation

## Error Handling Patterns
- Try-catch blocks with proper logging
- Meaningful error messages in response maps
- HTTP status codes (201 for creation, 401 for auth failures, etc.)
- Structured JSON error responses

## API Response Patterns
- Consistent use of `ResponseEntity<?>`
- JSON responses with `Map<String, Object>` for flexibility
- Success responses include relevant data
- Error responses include error messages

## Logging Conventions
- `@Slf4j` annotation for logger injection
- Info level for successful operations
- Error level for exceptions with stack traces
- Structured logging with user context (email addresses)

## Validation
- `@Valid` annotation on request DTOs
- Jakarta validation annotations
- Business logic validation in service layer

## Security Patterns
- JWT token-based authentication
- Environment variables for sensitive configuration
- Secure session cookies configuration
- Role-based access control