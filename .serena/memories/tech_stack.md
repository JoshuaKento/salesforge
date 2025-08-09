# SalesForge Technology Stack

## Backend Framework
- **Spring Boot**: 3.3.5 (latest stable)
- **Java**: 17 (source/target compatibility, development with Java 24)
- **Build Tool**: Gradle with Kotlin DSL
- **Architecture**: Multi-module project (core, security, infra, web)

## Key Spring Technologies
- **Spring Web**: REST API endpoints
- **Spring Security**: Authentication & authorization
- **Spring Data JPA**: Database access layer
- **Spring Boot Actuator**: Monitoring and health checks
- **Spring Validation**: Request validation

## Database
- **PostgreSQL**: Primary database (runtime dependency)
- **Flyway**: Database migrations
- **Hibernate**: ORM with PostgreSQL dialect

## Additional Frameworks & Libraries
- **Netflix DGS**: GraphQL support (planned)
- **SpringDoc OpenAPI**: API documentation (Swagger UI)
- **Lombok**: Code generation for boilerplate reduction
- **SLF4J**: Logging framework
- **JWT**: Token-based authentication

## Testing Stack
- **JUnit 5**: Primary testing framework
- **Spring Boot Test**: Integration testing
- **Spring Security Test**: Security testing
- **Testcontainers**: Integration testing with PostgreSQL
- **Mockito**: Mocking (included in spring-boot-starter-test)

## Development Tools
- **Docker**: Containerization (Dockerfile provided)
- **Docker Compose**: Database setup (docker-compose.yml)
- **Vaadin**: Optional UI framework (commented out in web module)