# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SalesForge is a Sales Force Automation (SFA) mini-application built with Spring Boot and Java 17. It provides CRM functionality for managing leads, accounts, contacts, opportunities, and activities in a sales pipeline.

## Technology Stack

- **Backend**: Spring Boot 3.3.5 with Java 17
- **Frontend**: REST API with JSON responses (Vaadin optional)
- **Additional Frameworks**: 
  - Netflix DGS (GraphQL)
  - Spring Security with OAuth2
  - Spring Data JPA
  - HTMX integration
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
- The application runs on Spring Boot with Vaadin UI
- Vaadin launcher is configured to auto-open browser (`vaadin.launch-browser=true`)
- Application name is set to `sales-forge`

## Project Structure

### Package Structure
- Main package: `com.example`
- Note: Uses standard reverse domain naming convention with example placeholder

### Key Directories
- `src/main/java/` - Java source code
- `src/main/resources/` - Configuration and resources
  - `application.properties` - Main configuration
  - `graphql/` - GraphQL schema definitions (empty currently)
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
- OAuth2 integration (client, authorization server, resource server)
- Spring Security configuration
- LDAP authentication support

## Development Notes

- Uses Spring Boot auto-configuration
- Vaadin integration for modern web UI
- GraphQL support via Netflix DGS
- Multiple session management options (MongoDB, Redis, Hazelcast, JDBC)
- Comprehensive validation and HATEOAS support

## Testing

- Uses JUnit 5 platform
- Spring Boot Test integration
- GraphQL testing support
- Security testing capabilities

## Documentation

Refer to `docs/sfa-app-spec.md` for detailed:
- Complete domain model with ERD
- Full REST API specification
- Sequence diagrams for key workflows
- Request/response examples
- Security and performance considerations