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
| Auth       | JWT (httpOnly cookies), Google OAuth (Apple/FB planned) |
| Payments   | Stripe Connect (escrow, destination charges)  |
| KYC        | Stripe Identity Verification                  |
| CI         | GitHub Actions                                |
| IaC        | Terraform (AWS ECS, RDS, Route53)             |

## Module Structure

- `homeflex-web/`: Angular 21 frontend. Features logic in `src/app/features/`.
  - `src/app/core/api/services/`: Domain-specific API services (`booking.api.ts`, `dispute.api.ts`, `finance.api.ts`, `payout.api.ts`, `insurance.api.ts`, `maintenance.api.ts`, etc.) extending `BaseApi`. **Use these directly in components — not `ApiClient`.**
  - `src/app/core/api/api.client.ts`: Legacy facade — kept for auth, chat, admin, and a few misc endpoints. Do NOT use for new workspace tab code.
  - `src/app/core/models/api.types.ts`: All TypeScript interfaces for request/response types.
  - `src/app/core/state/session.store.ts`: NgRx Signal Store for auth session state.
  - `src/app/core/guards/admin.guard.ts`: Route guard enforcing ADMIN role, redirects to `/admin/login`.
  - `src/app/features/workspace/`: Main authenticated dashboard (workspace page handles tenant/landlord roles).
  - `src/app/features/admin/`: Separate admin console with its own layout, login, and pages (dashboard, users, properties, reports, settings).
  - `src/app/shell/`: App shell with header/footer (hidden on admin routes via `isAdminRoute` signal).
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

## Admin Console Architecture

- **Separate login**: `/admin/login` — dark-themed, restricted portal. Non-admin users are rejected and logged out.
- **Guarded routes**: All `/admin/*` routes protected by `adminGuard` (checks `isAuthenticated && role === 'ADMIN'`).
- **Dedicated layout**: `AdminLayoutComponent` with sidebar navigation, top bar, and user plate. Consumer shell (header/footer) is hidden.
- **Admin pages**: Dashboard (analytics), Users (list/suspend/activate), Properties (pending approval/reject), Reports (flagged content/resolve), Settings (profile/password/notifications).
- **Backend**: `AdminController` with `@PreAuthorize("hasRole('ADMIN')")` — all admin endpoints under `/api/v1/admin/`.

## API Architecture

- All endpoints prefixed with `/api/v1/`
- 25 REST controllers, ~100+ endpoints
- Domain-specific services (`BookingApi`, `DisputeApi`, `FinanceApi`, etc.) are the primary HTTP layer for workspace components
- `ApiClient` is a legacy facade; retained for auth, admin, chat, and a few shared endpoints
- Nginx reverse proxy: `/api/` -> backend:8080, `/ws/` -> WebSocket, `/uploads/` -> `minio:9000/rental-app-media/`, `/` -> Angular SPA
- `StorageService` generates `/uploads/<key>` relative URLs — never full `http://minio:...` URLs (unreachable from browser)
- CSRF token sent via `X-XSRF-TOKEN` header on mutating requests
- All requests use `withCredentials: true` for cookie-based auth

## Available Skills

Custom skills live in `.claude/skills/` and are auto-loaded by Claude Code. Always invoke the matching skill instead of generating code freehand.

| Skill                | Trigger phrases                                                        | File                                    |
| -------------------- | ---------------------------------------------------------------------- | --------------------------------------- |
| `spring-entity`      | "create entity / domain model / @Entity"                               | `.claude/skills/spring-entity/SKILL.md` |
| `spring-dto`         | "create DTO / request / response / mapper"                             | `.claude/skills/spring-dto/SKILL.md`    |
| `spring-service`     | "create service / use case / business logic"                           | `.claude/skills/spring-service/SKILL.md`|
| `spring-controller`  | "create controller / REST endpoint / API route"                        | `.claude/skills/spring-controller/SKILL.md` |
| `spring-migration`   | "create migration / add column / add table"                            | `.claude/skills/spring-migration/SKILL.md` |
| `angular-component`  | "create component / page / form / list / modal"                        | `.claude/skills/angular-component/SKILL.md` |
| `angular-service`    | "create Angular service / call backend / HTTP client"                  | `.claude/skills/angular-service/SKILL.md` |
| `angular-feature`    | "scaffold Angular feature / full CRUD module"                          | `.claude/skills/angular-feature/SKILL.md` |
| `docker-compose`     | "create docker-compose / dockerize / add service"                      | `.claude/skills/docker-compose/SKILL.md` |
| `security`           | "secure X / harden / audit / OWASP / encrypt / rate-limit"            | `.claude/skills/security/SKILL.md`      |
| `folder-structure`   | "scaffold project / folder layout / directory structure / architecture" | `.claude/skills/folder-structure/SKILL.md` |

### Security Skill — Key Rules

The `security` skill encodes HomeFlex's security architecture. Always consult it when:
- Writing any auth, CSRF, session, or token-handling code
- Adding a new endpoint that needs `@PreAuthorize`
- Implementing file upload, encryption, or secret management
- Reviewing or generating rate-limiting logic

Critical rules enforced by this skill:
1. Secrets: `${VAR}` with **no fallback** for sensitive values — fail fast if unset
2. `DataInitializer` (and any seed class) must be `@Profile("!prod")`
3. Token comparisons use `MessageDigest.isEqual()` — never `String.equals()`
4. Rate-limit IP resolution uses **last** `X-Forwarded-For` entry (Nginx-appended)
5. Password-reset and OTP flows must return the same response for hit/miss
6. Swagger UI disabled by default; re-enabled only in `dev` profile
7. `rental-backend/.env` is git-ignored — use `.env.example` as template

### Folder Structure Skill — Key Rules

The `folder-structure` skill generates idiomatic directory trees for:
- **Java / Spring Boot**: Layered (current), Hexagonal, Vertical Slice
- **TypeScript / Angular**: Feature-Sliced Design (current)
- **Python / FastAPI**: Clean Architecture
- **Go**: Standard Go layout with Clean Architecture
- **Rust**: Hexagonal with Axum/Actix
- **Flutter / Dart**: Feature-First Clean Architecture

Always state the chosen architecture pattern and the 3–5 key conventions that govern it.

## Frontend Design Conventions (Mandatory)

All new public-facing pages and workspace tabs must follow the editorial luxury design system:

1. **Dark hero headers on listing/discovery pages** — Use `bg-slate-900 py-14` section with `.eyebrow--light` badge, `text-white font-extrabold` title, `text-slate-400` subtitle, and `bg-white/5 border border-white/10 rounded-2xl` stat tiles.
2. **Premium filter sidebars** — Sticky (`top-24`), `rounded-xl border border-slate-200 bg-slate-50` inputs, `.select-wrap` + `.select-styled` dropdowns, `font-black` CTA button with `shadow-brand-100/50`.
3. **Workspace tab headers** — `bg-white border-b border-slate-100 px-6 lg:px-8 py-6` with colored icon box (`h-10 w-10 rounded-xl bg-<color>-500`), bold title, and muted subtitle.
4. **No raw enum display** — All enum values in templates must use `.replaceAll('_', ' ')`. Status badge colors must use a `statusClass(status)` method returning a Tailwind class string.
5. **Color theme per domain** — Insurance: emerald. Disputes: amber. Finance: slate-900 (dark) + gold CTA. Hosting: brand. Maintenance: violet. Notifications: sky.
6. **CSS utility classes available** — `.eyebrow`, `.eyebrow--gold`, `.eyebrow--light`, `.button`, `.button--primary`, `.button--gold`, `.button--ghost`, `.button--dark`, `.card-img-overlay`, `.select-styled`, `.select-wrap`. Use these instead of re-authoring base styles.

## Angular Component Conventions (Mandatory)

All workspace tab components and new Angular components must follow these patterns:

1. **No `ApiClient` in workspace tabs** — inject the domain-specific service (e.g., `DisputeApi`, `FinanceApi`) directly.
2. **`takeUntilDestroyed(destroyRef)`** — every RxJS subscription inside a component must be piped through this operator. Inject `DestroyRef` in the constructor. No manual `ngOnDestroy` unsubscription.
3. **Constructor data loading** — call APIs in `constructor()` via `takeUntilDestroyed`, not `ngOnInit`. This is consistent with Angular 21's signal-based lifecycle.
4. **`protected` signals for templates** — signals and computed values accessed in templates must be `protected readonly`, not `private`.
5. **`catchError` on every HTTP call** — always handle errors; set an `error` signal and return `of([])` or `of(null)` to keep the stream alive.

## Important Conventions

- Commit messages follow conventional commits: `feat:`, `fix:`, `chore:`, `docs:`, `security:`
- Prettier is enforced via lint-staged pre-commit hook
- `.gitignore` excludes `homeflex-web/`, `.github/`, `monitoring/`, `logstash/` — use `git add -f` for these paths
- `rental-backend/.env` is git-ignored — never commit secrets; copy `.env.example` to get started
- Frontend build must pass with zero errors before committing
- Backend tests exclude integration tests by default (tagged `@Tag("integration")`)
