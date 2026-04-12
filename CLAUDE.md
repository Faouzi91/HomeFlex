# HomeFlex AI Assistant Context

## Project Mission

HomeFlex is an enterprise-grade rental marketplace for properties and vehicles, prioritizing security (httpOnly cookies, CSRF), reliability (outbox relay, circuit breakers), and scalability (ES search, Redis caching).

## Core Architectural Mandates

- **Decoupling**: Controllers MUST NOT access repositories directly. Always use a Service layer.
- **Reliability**: Use the transactional outbox pattern for cross-service events (e.g., indexing to Elasticsearch).
- **Security**:
  - JWTs are stored in httpOnly cookies.
  - CSRF protection is mandatory for state-changing operations.
  - Landlord identity (KYC) is required for publishing.
- **Mapping**: Use MapStruct for Entity <-> DTO conversions. Never manual mapping.
- **State**: Angular uses NgRx Signal Store for entity management and side effects.

## Tech Stack

| Layer      | Technology                                    |
| ---------- | --------------------------------------------- |
| Frontend   | Angular 21, TailwindCSS 4, TypeScript 5.9     |
| Backend    | Spring Boot 4, Java 21, Gradle                |
| Database   | PostgreSQL 18, Flyway migrations              |
| Cache      | Redis 8                                       |
| Messaging  | RabbitMQ 4                                    |
| Search     | Elasticsearch 9.1                             |
| Logging    | Logstash 9.1 + Kibana 9.1 (ELK stack)         |
| Monitoring | Micrometer, Prometheus 3.5, Grafana 11.6      |
| Auth       | JWT (httpOnly cookies), Google/Apple/FB OAuth |
| Payments   | Stripe Connect (escrow, destination charges)  |
| KYC        | Stripe Identity Verification                  |
| CI         | GitHub Actions                                |
| IaC        | Terraform (AWS ECS, RDS, Route53)             |

## Module Structure

- `homeflex-web/`: Angular 21 frontend. Features logic in `src/app/features/`.
  - `src/app/core/api/api.client.ts`: Centralized HTTP client — ALL API calls go through this single service.
  - `src/app/core/models/api.types.ts`: All TypeScript interfaces for request/response types.
  - `src/app/core/state/session.store.ts`: NgRx Signal Store for auth session state.
  - `src/app/features/workspace/`: Main authenticated dashboard (workspace page handles all user roles).
- `rental-backend/`: Spring Boot 4 / Java 21 API.
  - `core/`: Auth, Security, Infrastructure, Common Services.
  - `features/`: Business modules (Property, Vehicle, Booking, Lease, Finance, Insurance, Dispute).
- `rental-app-flutter/`: Flutter mobile app (not part of Docker Compose; focus is Angular + Spring Boot).

## Development Commands

- **Full Stack (Docker)**: `docker compose up --build` (starts all 8 services)
- **Monitoring Stack**: `docker compose -f docker-compose.monitoring.yml up -d` (Prometheus + Grafana)
- **Backend Build**: `./gradlew build` (in `rental-backend/`)
- **Backend Run**: `./gradlew bootRun` (in `rental-backend/`)
- **Backend Tests**: `./gradlew test --no-daemon` (unit tests only)
- **Frontend Install**: `npm ci` (in `homeflex-web/`)
- **Frontend Dev**: `npm start` (in `homeflex-web/`, dev server on port 4200)
- **Frontend Build**: `npm run build` (in `homeflex-web/`)
- **Frontend Lint**: `npm run lint` (Prettier check)
- **Frontend Test**: `npm test` (in `homeflex-web/`)

## Docker Services (docker-compose.yml)

| Service       | Image                        | Port  | Purpose                   |
| ------------- | ---------------------------- | ----- | ------------------------- |
| backend       | ./rental-backend (build)     | 8080  | Spring Boot API           |
| frontend      | ./homeflex-web (build)       | 80    | Angular SPA + Nginx proxy |
| db            | postgres:18-alpine           | 5432  | PostgreSQL database       |
| redis         | redis:8-alpine               | 6379  | Cache & distributed locks |
| rabbitmq      | rabbitmq:4-management-alpine | 5672  | Message broker            |
| elasticsearch | elasticsearch:9.1.2          | 9200  | Full-text search          |
| logstash      | logstash:9.1.2               | 50000 | Log aggregation           |
| kibana        | kibana:9.1.2                 | 5601  | Log visualization         |

## Key Database Tables

- `users`: Core identity and roles.
- `properties` / `vehicles`: Rental assets.
- `bookings`: Central reservation engine.
- `property_availability`: Sparse date model for asset availability.
- `property_leases`: Digital contract tracking with blockchain TX hashes.
- `agencies`: Multi-tenant agency white-labeling.
- `maintenance_requests`: Tenant issue tracking.
- `insurance_policies`: Tenant/Landlord protection plans.
- `receipts`: Automated PDF financial records.
- `disputes`: Conflict resolution tracking.
- `outbox_events`: Queue for reliable event delivery.
- `processed_stripe_events`: Idempotency log for Stripe webhooks.

## API Architecture

- All endpoints prefixed with `/api/v1/`
- 25 REST controllers, ~100+ endpoints
- Angular `ApiClient` has full 1:1 coverage of all backend endpoints
- Nginx reverse proxy: `/api/` -> backend:8080, `/ws/` -> WebSocket, `/` -> Angular SPA
- CSRF token sent via `X-XSRF-TOKEN` header on mutating requests
- All requests use `withCredentials: true` for cookie-based auth

## Important Conventions

- Commit messages follow conventional commits: `feat:`, `fix:`, `chore:`, `docs:`
- Prettier is enforced via lint-staged pre-commit hook
- `.gitignore` excludes `homeflex-web/`, `.github/`, `monitoring/`, `logstash/` — use `git add -f` for these paths
- Frontend build must pass with zero errors before committing
- Backend tests exclude integration tests by default (tagged `@Tag("integration")`)
