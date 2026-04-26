# HomeFlex

A full-stack real estate rental platform where tenants can search and book properties, landlords can list and manage them, and admins oversee the entire marketplace. Supports real-time chat, Stripe payments, push notifications, and mobile deployment.

## Tech Stack

| Layer         | Technology                                   |
| ------------- | -------------------------------------------- |
| Frontend      | Angular 21, TailwindCSS 4, TypeScript 5.9    |
| Backend       | Spring Boot 4, Java 21, Gradle               |
| Database      | PostgreSQL 18, Flyway migrations             |
| Cache         | Redis 8                                      |
| Messaging     | RabbitMQ 4                                   |
| Search        | Elasticsearch 9                              |
| Mobile        | Capacitor 8 (Android / iOS)                  |
| State         | NgRx Signal Store (`@ngrx/signals` 21)       |
| Auth          | JWT (access + refresh tokens), Google OAuth  |
| Payments      | Stripe Connect (escrow, destination charges) |
| KYC           | Stripe Identity Verification                 |
| Notifications | Firebase Cloud Messaging                     |
| Resilience    | Resilience4j (circuit breaker, retry)        |
| Monitoring    | Micrometer, Prometheus, Grafana              |
| CI            | GitHub Actions                               |

## Features

- **Property search** with Elasticsearch-powered fuzzy matching, faceted filtering, and geo-distance sorting
- **Hierarchical property model** — `Property` acts as a building/group, `RoomType` declares unit types (Room / Studio / Apartment / Suite) each with their own `totalRooms`, `pricePerNight`, `bedType`, `maxOccupancy`, images and amenities; bookings target either a whole property or a specific room type with `numberOfRooms`
- **Real-time inventory tracking** — `room_inventory(room_type_id, date, rooms_booked)` sparse table powers `available = totalRooms − roomsBooked` per night; `RoomInventoryService.reserve/release` runs inside the booking transaction and rejects requests that would exceed available capacity, eliminating overbooking
- **Admin-owned reference data** — admins control amenities, property/vehicle/listing types, pricing rules, commission rates, cancellation policies and platform-wide settings under `/api/v1/admin/*`; non-admin clients can only consume them
- **Property availability** with sparse date model, calendar UI, and double-booking prevention (V11)
- **Digital leases** with e-signature support and automated document management (V12)
- **Vehicle rentals** with image uploads, condition reports, and availability checking
- **Enterprise RBAC** — `Role`/`Permission` JPA entities, 46 compile-time permission constants (`Permissions.*`), V27/V28 Flyway migrations, backward-compatible `user_roles` backfill from legacy `users.role` column
- **Centralized permission + ownership** — `ResourcePermissionService` is the single source of truth for all ownership rules; `HomeFlexPermissionEvaluator` is a thin auth-contract handler that delegates to it; `BookingRepository.findByIdWithParties` uses JOIN FETCH to prevent N+1 in the evaluator path
- **Permission-based authorization** — controllers use `hasAuthority()` / `hasPermission()` SpEL expressions; `BookingService` is pure business logic with no security concerns; `GET /bookings/{id}` now enforces ownership (was a security gap)
- **Separated Admin Console** with dedicated login, dashboard, user management, property approvals, and report resolution
- **Booking system** with approve / reject / cancel workflow
- **Real-time chat** between tenants and landlords (WebSocket + STOMP)
- **SMS & WhatsApp notifications** via Twilio for booking alerts (SRS-3.4.2)
- **Push notifications** via Firebase (FCM)
- **Stripe Connect payments** — full escrow lifecycle: MANUAL-capture `PaymentIntent` created at booking, captured on landlord approval, prorated refund on early checkout, full refund on cancellation post-capture; tenant pays via Stripe Elements on property detail page
- **Landlord Stripe Connect onboarding** — Hosting → Payments tab with connected / not-connected states, "Connect with Stripe" redirect to Stripe Express onboarding, and a live payout summary (available balance, pending, in escrow, lifetime earnings)
- **Dispute Modal** — Structured dispute form (6 reason categories, 20–1000 char description) replaces browser `prompt()` calls; wired into the booking detail panel
- **Landlord KYC** via Stripe Identity verification
- **Admin console** with separate login, user management (suspend/activate), property approvals (approve/reject), report resolution, and operational analytics
- **Prometheus + Grafana monitoring** with custom business metrics
- **Premium Editorial UI** — Dark `bg-slate-900` hero headers on listing pages, glassmorphism stat tiles, premium filter sidebars with sticky positioning and `.select-styled` dropdowns; workspace tabs (Insurance, Disputes, Finance) fully restyled with emerald/amber/gold color themes, SVG icon headers, and animated skeleton loaders
- **Image Proxy via Nginx** — `/uploads/*` proxied through Nginx to MinIO so browser-side image URLs always resolve; `StorageService` generates relative `/uploads/<key>` paths; V38 migration rewrites historical absolute URLs
- **Modern Web Dashboard** — Angular 21 + Tailwind 4 operational panel with **Map Search**
- **Global Reach** — Full i18n support (English, French, Spanish, Arabic) with **RTL support**
- **Enterprise Observability** — Centralized **ELK Stack** (Elasticsearch, Logstash, Kibana) + Prometheus/Grafana
- **Innovative Tech** — **Blockchain-backed lease contracts** and **AI-driven pricing recommendations**
- **B2B Foundation** — Multi-tenant **Agency white-labeling** architecture
- **Workspace Dashboard** — Finance/Receipts tab (PDF download links, status badges), Disputes tab (OPEN/UNDER_REVIEW/RESOLVED/CLOSED workflow), and Insurance tab (plan cards with tenant/landlord grouping and purchase flow)
- **Trust & Safety** — Two-way reviews, **Trust Scores**, and **Dispute Resolution** with evidence upload
- **Compliance** — **AES-256-GCM PII encryption** and full **GDPR tooling** (Export/Erase)
- **Security Hardening** — Mandatory environment-based secrets, zero-trust infrastructure isolation, X-Pack Elasticsearch, constant-time token comparison, CSP headers, and production-only DataInitializer guard.

## Quick Start (Docker)

The fastest way to run everything locally. Spins up 6 services: frontend, backend, PostgreSQL, Redis, RabbitMQ, and Elasticsearch.

### Prerequisites

- Docker & Docker Compose

### Required environment variables

Before starting, copy `.env.example` and fill in secrets. The following variables **must** be set — the application will fail to start without them:

```bash
cp rental-backend/.env.example rental-backend/.env
# then edit rental-backend/.env
```

| Variable            | Purpose                          |
| ------------------- | -------------------------------- |
| `JWT_SECRET`        | HS256 signing key (≥ 32 chars)   |
| `ADMIN_PASSWORD`    | Initial admin account password   |
| `PII_ENCRYPTION_KEY`| AES-256-GCM key for PII fields   |
| `MAIL_USERNAME`     | SMTP sender address              |
| `MAIL_PASSWORD`     | SMTP credential / app password   |
| `ELASTIC_PASSWORD`  | Elasticsearch `elastic` user password |

### Run

```bash
git clone <repo-url> && cd HomeFlex
# Set required env vars (see above), then:
docker-compose up --build
```

Once all containers are healthy (~60s):

| Service             | URL                                              |
| ------------------- | ------------------------------------------------ |
| Web Frontend        | http://localhost                                 |
| Backend API         | http://localhost:8080/api/v1                     |
| Swagger UI          | Dev profile only — not available in `prod`       |
| Kibana Dashboard    | Internal network only (no host port in prod)     |
| RabbitMQ Management | Internal network only (no host port in prod)     |

### Monitoring (optional)

```bash
docker-compose -f docker-compose.monitoring.yml up -d
```

| Service    | URL                                   |
| ---------- | ------------------------------------- |
| Prometheus | http://localhost:9090                 |
| Grafana    | http://localhost:3000 (admin / admin) |

### Stop

```bash
docker-compose down            # stop services, keep data
docker-compose down -v         # stop services and delete volumes
```

## Development Setup

### Backend

Requires **Java 21** and a running PostgreSQL instance (or use the Docker `db` service).

```bash
cd rental-backend
./gradlew bootRun              # start on port 8080
./gradlew build                # build + run all tests
./gradlew test                 # tests only
```

Key environment variables (set in `.env` or export):

| Variable                | Purpose                                |
| ----------------------- | -------------------------------------- |
| `JWT_SECRET`            | Signing key for access/refresh tokens  |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL                    |
| `STRIPE_SECRET_KEY`     | Stripe secret key (`sk_test_...`)      |
| `STRIPE_PUBLISHABLE_KEY`| Stripe publishable key (`pk_test_...`) |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret          |
| `GOOGLE_CLIENT_ID`      | Google OAuth client ID                 |
| `FIREBASE_*`            | Firebase config for push notifications |

### Frontend

Requires **Node 20+**.

```bash
cd homeflex-web
npm install
npm start                      # dev server on port 4200
npm run build                  # production build
npm test                       # unit tests (Vitest)
```

The dev server proxies `/api/*` to `http://localhost:8080` by default.

### Full Stack (Docker hybrid)

Run infrastructure in Docker and the app locally:

```bash
# start only db, redis, rabbitmq, elasticsearch
docker-compose up -d db redis rabbitmq elasticsearch

# run backend and frontend locally
cd rental-backend && ./gradlew bootRun &
cd homeflex-web && npm start &
```

## Project Structure

```
HomeFlex/
├── homeflex-web/                 # Angular 21 SPA
│   ├── src/app/
│   │   ├── core/                 # Guards, interceptors, services, state
│   │   ├── features/             # Lazy-loaded feature modules
│   │   │   ├── admin/            # Separated admin console
│   │   │   │   ├── layout/       # Admin sidebar layout
│   │   │   │   └── pages/        # Login, dashboard, users, properties, reports, settings
│   │   │   ├── auth/             # Login, register, forgot/reset password
│   │   │   ├── marketing/        # Home, support pages
│   │   │   ├── properties/       # Search, detail
│   │   │   ├── vehicles/         # Search, detail
│   │   │   └── workspace/        # Authenticated dashboard (tenant/landlord)
│   │   ├── shell/                # App shell (header, footer — hidden on admin routes)
│   │   └── shared/               # Reusable UI components
│   ├── Dockerfile                # Multi-stage build (Node -> Nginx)
│   └── nginx.conf                # Reverse proxy, gzip, SPA fallback
│
├── rental-backend/               # Spring Boot API
│   ├── src/main/java/.../
│   │   ├── api/v1/               # REST controllers (all /api/v1/*)
│   │   ├── domain/
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── enums/            # Domain enumerations
│   │   │   ├── event/            # Outbox events
│   │   │   └── repository/       # Spring Data JPA repos
│   │   ├── dto/                  # Request, response, and common DTOs
│   │   ├── service/              # Business logic
│   │   ├── mapper/               # Entity <-> DTO mapping
│   │   ├── security/             # JWT filter + token provider
│   │   ├── config/               # Spring configs (Security, WebSocket, etc.)
│   │   ├── exception/            # Global handler + custom exceptions
│   │   └── infrastructure/       # External integrations (notifications)
│   ├── src/main/resources/
│   │   └── db/migration/         # Flyway SQL migrations
│   └── Dockerfile                # Multi-stage build (JDK -> JRE)
│
├── docker-compose.yml            # 6-service orchestration
├── docker-compose.monitoring.yml # Prometheus + Grafana
├── monitoring/                   # Prometheus config, Grafana dashboards
├── .github/workflows/ci.yml     # CI pipeline
└── CLAUDE.md                     # AI assistant context
```

## API Overview

All endpoints are prefixed with `/api/v1`. Public endpoints don't require authentication.

| Method | Endpoint                              | Auth     | Description                         |
| ------ | ------------------------------------- | -------- | ----------------------------------- |
| POST   | `/auth/register`                      | Public   | Register a new user                 |
| POST   | `/auth/login`                         | Public   | Login, sets JWT cookies             |
| POST   | `/auth/google`                        | Public   | Google OAuth login                  |
| POST   | `/auth/refresh`                       | Public   | Refresh access token                |
| GET    | `/properties/search`                  | Public   | Search with filters + pagination    |
| GET    | `/properties/{id}`                    | Public   | Property detail                     |
| POST   | `/properties`                         | Landlord | Create property (multipart)         |
| PUT    | `/properties/{id}`                    | Landlord | Update property                     |
| DELETE | `/properties/{id}`                    | Landlord | Delete property                     |
| POST   | `/properties/{id}/bookings/draft`     | Tenant   | Create property booking draft       |
| POST   | `/properties/{id}/bookings/{bid}/pay` | Tenant   | Initiate property booking payment   |
| PATCH  | `/bookings/{id}/approve`              | Landlord | Approve booking                     |
| PATCH  | `/bookings/{id}/reject`               | Landlord | Reject booking                      |
| PATCH  | `/bookings/{id}/cancel`               | Tenant   | Cancel booking                      |
| PATCH  | `/bookings/{id}/early-checkout`       | Tenant   | Early checkout with prorated refund |
| GET    | `/chat/rooms`                         | Auth     | List chat rooms                     |
| POST   | `/chat/rooms`                         | Auth     | Create chat room                    |
| GET    | `/favorites`                          | Auth     | List favorites                      |
| POST   | `/favorites/{propertyId}`             | Auth     | Add to favorites                    |
| GET    | `/admin/users`                        | Admin    | List all users (paginated)          |
| PATCH  | `/admin/users/{id}/suspend`           | Admin    | Suspend a user account              |
| PATCH  | `/admin/users/{id}/activate`          | Admin    | Reactivate a suspended user         |
| PATCH  | `/admin/users/{id}/role`              | Admin    | Change a user's role (legacy + RBAC)|
| GET    | `/admin/properties/pending`           | Admin    | List pending properties             |
| PATCH  | `/admin/properties/{id}/approve`      | Admin    | Approve a property listing          |
| PATCH  | `/admin/properties/{id}/reject`       | Admin    | Reject a property (with reason)     |
| GET    | `/admin/reports`                      | Admin    | List flagged content reports        |
| PATCH  | `/admin/reports/{id}/resolve`         | Admin    | Resolve a report                    |
| GET    | `/disputes/mine`                      | Auth     | List disputes filed by current user |
| GET    | `/config`                             | Public   | Stripe publishable key + public cfg |
| GET    | `/admin/analytics`                    | Admin    | Platform analytics dashboard        |
| GET    | `/admin/configs`                      | Admin    | List system configurations          |
| PATCH  | `/admin/configs/{key}`                | Admin    | Update a system config value        |
| GET    | `/admin/pricing-rules`                | Admin    | List all pricing rules cross-property|
| DELETE | `/admin/pricing-rules/{ruleId}`       | Admin    | Delete a pricing rule               |
| GET    | `/admin/cancellation-policies`        | Admin    | List cancellation policies          |
| POST   | `/admin/cancellation-policies`        | Admin    | Create a cancellation policy        |
| PUT    | `/admin/cancellation-policies/{id}`   | Admin    | Update a cancellation policy        |
| DELETE | `/admin/cancellation-policies/{id}`   | Admin    | Delete a cancellation policy        |
| GET    | `/vehicles/search`                    | Public   | Search vehicles with filters        |
| GET    | `/vehicles/{id}`                      | Public   | Vehicle detail                      |
| POST   | `/vehicles`                           | Landlord | Create vehicle listing              |
| PUT    | `/vehicles/{id}`                      | Landlord | Update vehicle                      |
| DELETE | `/vehicles/{id}`                      | Landlord | Soft-delete vehicle                 |
| POST   | `/vehicles/{id}/bookings/draft`       | Tenant   | Create vehicle booking draft        |
| POST   | `/vehicles/{id}/bookings/{bid}/pay`   | Tenant   | Initiate vehicle booking payment    |
| POST   | `/vehicles/{id}/images`               | Landlord | Upload vehicle images               |
| POST   | `/vehicles/{id}/condition`            | Landlord | Create condition report             |
| GET    | `/vehicles/{id}/condition`            | Landlord | List condition reports              |
| POST   | `/kyc/session`                        | Landlord | Create Stripe Identity session      |
| GET    | `/kyc/status`                         | Landlord | Get KYC verification status         |
| POST   | `/webhooks/stripe`                    | Public   | Stripe webhook (signature-verified) |
| GET    | `/payouts/summary`                    | Landlord | Payout summary (balance + escrow)   |
| POST   | `/payouts/connect/onboard`            | Landlord | Create Stripe Connect account       |
| GET    | `/leases/my`                          | Auth     | List user's rental leases           |
| POST   | `/leases/booking/{id}/generate`       | Landlord | Generate new lease for booking      |
| POST   | `/leases/{id}/sign`                   | Tenant   | Electronically sign a lease         |
| GET    | `/properties/{id}/availability`       | Public   | Get booked/blocked dates            |
| POST   | `/properties/{id}/availability/block` | Landlord | Manually block dates                |

See Swagger UI at `/swagger-ui.html` for the complete API reference.

## Testing

```bash
# Backend
cd rental-backend
./gradlew test                                          # all tests
./gradlew test --tests "com.homeflex.SomeTest"          # single class

# Frontend
cd homeflex-web
npm test                                                # all tests
ng test -- --include='**/some.spec.ts'                  # single file
```

The backend includes **ArchUnit** tests that enforce architectural rules (e.g., controllers must not directly access repositories).

A shell-based smoke test covering all major API flows is available at `scripts/test-all-apis.sh` (94 assertions). Run it against a live stack:

```bash
bash scripts/test-all-apis.sh
```

## 🟢 Implementation Status: 100% Complete

All technical requirements specified in the SRS have been implemented, including:

- **Phase 0:** Core Marketplace functionality
- **Phase 1:** Search, i18n, and Observability
- **Phase 2:** Insurance, Finance, and Disputes
- **Phase 3:** Production Hardening & Resiliency
- **Phase 4:** Compliance, Security, and Global Strategy
- **Phase 5:** Enterprise RBAC, Permission-Based Authorization & Workspace Tabs
- **Phase 5.1:** Centralized Ownership via `ResourcePermissionService` — pure-logic service layer, security gap fix, N+1 prevention
- **Phase 5.2:** Stripe escrow workflow completed — MANUAL capture, capture-on-approve, prorated early checkout, full-refund on cancel; landlord Stripe Connect onboarding UI; DisputeModal component; type fixes and Prettier lint at 100%
- **Phase 5.3:** Production-grade state machine booking workflow — `BookingStatus` expanded to 10 states; `BookingStateMachine` enforces transitions; `/draft` and `/pay` endpoints with idempotency; `BookingAuditLog` added; Vehicle ownership rules supported.
- **Phase 5.4:** Premium UI/UX overhaul — editorial dark hero headers on all listing pages, premium filter sidebars, full workspace tab redesigns (insurance/disputes/finance), raw enum display sweep across all templates, MinIO image proxy via Nginx `/uploads/`.

The platform is now ready for production-scale deployment.

## License

Private project.
