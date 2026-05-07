# Claude Code Skills — Spring Boot + Angular Stack

A suite of `.claude/skills/` directive files that guide Claude Code to generate
production-grade code following your architecture conventions.

## Skills Included

| Skill               | File                         | Triggers on                                             |
| ------------------- | ---------------------------- | ------------------------------------------------------- |
| `spring-entity`     | `spring-entity/SKILL.md`     | Create JPA entity, domain model, @Entity                |
| `spring-dto`        | `spring-dto/SKILL.md`        | Create DTO, request/response, MapStruct mapper          |
| `spring-service`    | `spring-service/SKILL.md`    | Create service, business logic, Resilience4j            |
| `spring-controller` | `spring-controller/SKILL.md` | Create REST controller, endpoint, API route             |
| `spring-migration`  | `spring-migration/SKILL.md`  | Create Flyway migration, add column/table               |
| `angular-component` | `angular-component/SKILL.md` | Create Angular component, page, form, list              |
| `angular-service`   | `angular-service/SKILL.md`   | Create Angular service, HTTP client                     |
| `angular-feature`   | `angular-feature/SKILL.md`   | Scaffold full Angular CRUD feature                      |
| `docker-compose`    | `docker-compose/SKILL.md`    | Create docker-compose, Dockerfile, Nginx config         |
| `security`          | `security/SKILL.md`          | Security hardening, OWASP audit, encryption, rate-limit |
| `folder-structure`  | `folder-structure/SKILL.md`  | Scaffold project layout for any language + architecture |

## Installation

### Option A — Project-level (one project)

```bash
mkdir -p .claude/skills
cp -r spring-entity spring-dto spring-service spring-controller spring-migration \
       angular-component angular-service angular-feature docker-compose \
       security folder-structure \
       .claude/skills/
```

### Option B — Global (all your projects)

```bash
mkdir -p ~/.claude/skills
cp -r spring-entity spring-dto spring-service spring-controller spring-migration \
       angular-component angular-service angular-feature docker-compose \
       security folder-structure \
       ~/.claude/skills/
```

## Usage

Skills trigger automatically when Claude Code detects relevant intent.
You can also invoke them explicitly:

```
Create a JPA entity for Product with name, price, and category
→ spring-entity skill triggers

Generate a Flyway migration to add description column to products table
→ spring-migration skill triggers

Scaffold the full Angular feature for Product management
→ angular-feature skill triggers (and coordinates angular-component + angular-service)
```

## Stack Coverage

- **Backend**: Spring Boot 4.x, Spring Data JPA, Hibernate, Flyway, Resilience4j, MapStruct, Lombok, SpringDoc OpenAPI
- **Frontend**: Angular 21+, Standalone components, Signals, OnPush, Reactive Forms
- **Database**: PostgreSQL with BIGSERIAL PKs, snake_case naming
- **Storage**: MinIO (S3-compatible)
- **Infra**: Docker Compose, Nginx reverse proxy, Prometheus + Grafana
