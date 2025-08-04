# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SalesForge is a Sales Force Automation (SFA) mini-application built with Spring Boot and Java 21. It provides CRM functionality for managing leads, accounts, contacts, opportunities, and activities in a sales pipeline.

## Technology Stack

- **Backend**: Spring Boot 3.3.5 with Java 21
- **Additional Frameworks**: 
  - Spring Security with JWT
  - Spring Data JPA
  - Spring Web (REST APIs)
- **Database**: PostgreSQL (runtime dependency)
- **Build Tool**: Gradle with Kotlin DSL

## Common Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

### Development Workflow
- The application runs on Spring Boot with REST APIs
- Application name is set to `my-sfa-app`
- Default port: 8080

## Project Structure

### Package Structure
- Main package: `com.example`
- Modules: core, security, infra, web

### Key Directories
- `src/main/java/` - Java source code
- `src/main/resources/` - Configuration and resources
  - `application.yml` - Main configuration
  - `static/` - Static web assets
  - `templates/` - UI templates
- `src/test/java/` - Test files
- `docs/` - Project documentation including detailed app specification

## Domain Model Architecture

The application follows a typical CRM domain model with these core entities:

### Core Entities
- **User** - System users (sales reps, managers, admins)
- **Lead** - Potential customers not yet qualified
- **Account** - Qualified organizations/companies  
- **Contact** - Individual people within accounts
- **Opportunity** - Potential deals/sales for pipeline tracking
- **Activity** - Interactions with leads/contacts (calls, emails, meetings)

### Entity Relationships
- Users manage leads and own accounts/opportunities
- Leads convert to accounts
- Accounts contain contacts and have opportunities
- Activities track interactions across leads, contacts, and opportunities

## API Design

The application is designed with REST API endpoints following `/api/v1/` pattern:
- Authentication endpoints (`/auth/`)
- CRUD operations for all major entities
- Reporting and analytics endpoints
- JWT-based authentication with role-based access control

## Security Features

- JWT-based authentication
- Role-based access control (ADMIN, MANAGER, SALES_REP)
- Spring Security configuration
- Basic authentication endpoints

## Development Notes

- Uses Spring Boot auto-configuration
- Multi-module architecture (core, security, infra, web)
- PostgreSQL database with Flyway migrations
- Testcontainers for integration testing
- Lombok for reducing boilerplate code

## Testing

- Uses JUnit 5 platform
- Spring Boot Test integration
- Testcontainers for database testing
- Security testing capabilities

## Documentation

Refer to `docs/sfa-app-spec.md` for detailed:
- Complete domain model with ERD
- Full REST API specification
- Sequence diagrams for key workflows
- Request/response examples
- Security and performance considerations