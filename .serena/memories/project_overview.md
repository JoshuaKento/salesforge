# SalesForge Project Overview

## Purpose
SalesForge is a Sales Force Automation (SFA) mini-application built with Spring Boot and Java 17. It provides CRM functionality for managing leads, accounts, contacts, opportunities, and activities in a sales pipeline.

## Core Business Domain
The application follows a typical CRM domain model with these core entities:
- **User** - System users (sales reps, managers, admins)
- **Lead** - Potential customers not yet qualified  
- **Account** - Qualified organizations/companies
- **Contact** - Individual people within accounts
- **Opportunity** - Potential deals/sales for pipeline tracking
- **Activity** - Interactions with leads/contacts (calls, emails, meetings)

## Entity Relationships
- Users manage leads and own accounts/opportunities
- Leads convert to accounts
- Accounts contain contacts and have opportunities  
- Activities track interactions across leads, contacts, and opportunities

## API Design
REST API endpoints following `/api/v1/` pattern:
- Authentication endpoints (`/auth/`)
- CRUD operations for all major entities
- Reporting and analytics endpoints
- JWT-based authentication with role-based access control

## Security Features
- JWT-based authentication
- Role-based access control (ADMIN, MANAGER, SALES_REP)
- OAuth2 integration support
- Spring Security configuration