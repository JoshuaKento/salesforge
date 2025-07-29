# SalesForge – One‑Week Sprint Plan (Mon July 29 – Sun Aug 4)

## Objective
Build and deploy a **mini Sales‑Force‑Automation (SFA) web‑app** in one intense week (~5 focused h/day). The project mirrors Job Journey’s production stack (Java 17 • Spring Boot 3 • PostgreSQL) and demonstrates your competence across design, coding, testing, CI/CD, documentation and DevOps.

## Key Portfolio Signals
- **Enterprise Java** codebase with clean layered architecture & UML diagrams.
- **> 90 % test coverage** (JUnit 5) plus containerised integration tests via Testcontainers.
- **Cross‑OS GitHub Actions** pipeline (Windows & Linux runners) building, testing and pushing Docker images.
- **Dual‑language docs (JP + EN)** proving professional communication skills.
- **Mentoring/leadership evidence** via PR reviews or blog style guidance.
- **Live demo on Render** with one‑click Docker Compose start‑up.

## Sprint Calendar
| Day | Focus & Output Deliverable | Detailed Tasks / AI Prompts |
| --- | ------------------------- | --------------------------- |
| **Mon (Design)** | *architecture.md* (ERD + sequence diagrams)<br>Repo scaffold | **Claude** – “Draft the SFA domain model (Lead, Account, Opportunity, User) and REST spec in Mermaid.”<br>**GPT‑4o** – “Generate Spring Boot 3 project (Gradle, modules: core, web, security, infra) incl. Postgres, Flyway, Testcontainers.” |
| **Tue (Core CRUD)** | Entities, Repositories, Service layer<br>80 % unit‑test stubs | **Cursor** – “Create `@Entity` classes with Lombok; Spring Data JPA repos; JUnit 5 + Mockito stubs.”<br>Hand‑wire service interfaces.
|
| **Wed (Auth + CI/CD)** | JWT login endpoint<br>GitHub Actions matrix | **GPT‑4o** – “Implement Spring Security JWT filter; issue tokens on `/auth/login`.”<br>**Claude** – “Compose GH Actions YAML: build & test on ubuntu‑latest and windows‑latest; push Docker image.” |
| **Thu (Frontend lite)** | React/Vite client or Thymeleaf pages<br>Screenshots for README | **Cursor** – “Scaffold Vite + React 18 + tanstack‑query; `/leads` table with CRUD dialogs.”<br>Grab hero screenshots for README. |
| **Fri (Polish & Docs)** | Multi‑lang README (JP/EN)<br>OpenAPI JSON | **Claude** – “Rewrite README (overview, quick‑start, tech‑stack table, ERD image, coverage badge).”<br>**GPT‑4o** – “Export OpenAPI 3 spec from controllers.” |
| **Sat (Deploy)** | Live URL on Render<br>CI auto‑deploy | Use Render’s Docker blueprint, set env vars, connect GH repo, verify health check.
|
| **Sun (Marketing)** | LinkedIn post draft<br>Portfolio PDF snapshot | **GPT‑4o** – “Draft LinkedIn post: one‑week sprint story, highlights, live link, CTA.”<br>Use `wkhtmltopdf` to snapshot README into PDF. |

## Quality Gates
1. All PRs pass `./gradlew test` with > 90 % coverage.
2. `docker compose up` boots API + Postgres in ≤ 60 s.
3. End‑to‑end `/leads` CRUD flow green in Postman collection.

## Stretch Goals (time‑permitting)
- Pagination & filtering via `JpaSpecificationExecutor`.
- Role‑based access control (ADMIN vs SALES).
- GitHub Pages docs microsite.

## Time Budget (≈ 28 h)
| Area | Hours |
| --- | --- |
| Design & Planning | 5 h |
| Coding (CRUD + Auth) | 12 h |
| Testing | 4 h |
| Docs & Marketing | 4 h |
| DevOps & Deploy | 3 h |

## AI Usage Cheatsheet
| AI Tool | Best Use |
| --- | --- |
| **Claude 3.5 Sonnet** | Long‑form specs, diagram descriptions |
| **GPT‑4o** | Boilerplate code, transforms, test generation |
| **Cursor** | In‑IDE refactors & repetitive scaffolding |

---
**Recommended Project Name:** **SalesForge**
> *“Forge a production‑ready SFA in one week.”*

Alternate name ideas: PipelinePilot · LeadFoundry · SprintSFA · DealSmith

