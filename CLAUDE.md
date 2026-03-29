# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HomeFlex is a real estate rental platform with an Angular frontend, Spring Boot backend, and PostgreSQL database. It supports web and mobile (Capacitor) with real-time chat (WebSocket/STOMP), Stripe payments, and role-based access (TENANT, LANDLORD, ADMIN).

## Common Commands

### Backend (from `rental-backend/`)

```bash
./gradlew build              # Build + run all tests
./gradlew bootRun            # Start dev server (port 8080)
./gradlew test               # Run all tests
./gradlew test --tests "com.homeflex.SomeTest"  # Single test class
./gradlew test --tests "com.homeflex.SomeTest.someMethod"  # Single test method
```

### Frontend (from `rental-app-frontend/`)

```bash
npm start                    # Dev server (port 4200)
npm run build:prod           # Production build
npm test                     # Unit tests (Karma + Jasmine)
npm run lint                 # Lint
ng test -- --include='**/some.component.spec.ts'  # Single test file
```

### Full Stack (from root)

```bash
docker-compose up --build    # Start all services
docker-compose up -d         # Start detached
docker-compose down          # Stop all services
docker-compose logs -f backend   # Tail backend logs
```

### Root-level tooling

```bash
npx prettier --check .       # Check formatting (Husky pre-commit hook runs this)
```

## Architecture

### Backend: `rental-backend/`

- **Spring Boot 4.0.4**, Java 21, Gradle (Groovy DSL)
- **Package-by-Feature architecture** under `com.homeflex`
  - `com.homeflex.core` — Cross-cutting concerns (security, config, exceptions, infrastructure, user/chat/notification domain)
  - `com.homeflex.features.property` — Property rental feature (entities, services, controllers, DTOs, mappers)
  - `com.homeflex.features.vehicle` — Vehicle rental feature (separate `vehicles` PostgreSQL schema)
- **Main class:** `com.homeflex.HomeFlexApplication`
- **Database:** PostgreSQL, Flyway migrations in `src/main/resources/db/migration/`
- **DI:** Constructor injection via Lombok `@RequiredArgsConstructor` (never `@Autowired` on fields)
- **All API endpoints** prefixed `/api/v1/`
- **Swagger UI** at `/swagger-ui.html`

#### Backend Tree

```
com.homeflex/
├── HomeFlexApplication.java
├── core/
│   ├── CoreModuleConfig.java
│   ├── api/v1/                           # AdminController, AuthV1Controller, ChatController,
│   │                                     # NotificationController, UserController, WebSocketChatController
│   ├── config/                           # AppProperties, DataInitializer, FirebaseConfig,
│   │                                     # RabbitMqConfig, SampleDataInitializer, SecurityConfig, WebSocketConfig
│   ├── domain/
│   │   ├── entity/                       # User, RefreshToken, OAuthProvider, ChatRoom, Message,
│   │   │                                 # Notification, FcmToken, TypingNotification
│   │   ├── enums/                        # UserRole, NotificationType
│   │   ├── event/                        # OutboxEvent, OutboxEventRepository
│   │   └── repository/                   # UserRepository, RefreshTokenRepository, OAuthProviderRepository,
│   │                                     # ChatRoomRepository, MessageRepository, NotificationRepository, FcmTokenRepository
│   ├── dto/
│   │   ├── common/                       # ApiListResponse, ApiPageResponse, ApiValueResponse
│   │   ├── event/                        # OutboxEventMessage
│   │   ├── request/                      # LoginRequest, RegisterRequest, ChatRoomCreateRequest, etc.
│   │   └── response/                     # AuthResponse, UserDto, ChatRoomDto, MessageDto, NotificationDto
│   ├── exception/                        # GlobalExceptionHandler, ConflictException, DomainException,
│   │                                     # ResourceNotFoundException, UnauthorizedException
│   ├── infrastructure/notification/      # NotificationGateway, FirebaseNotificationGateway
│   ├── mapper/                           # UserMapper, ChatMapper, NotificationMapper
│   ├── security/                         # JwtAuthenticationFilter, JwtTokenProvider, RateLimitFilter
│   └── service/                          # AdminService, AuthService, ChatService, EmailService,
│                                         # NotificationService, StorageService, PaymentService,
│                                         # UserService, EventOutboxService, OutboxRelayService
├── features/
│   ├── property/
│   │   ├── PropertyModuleConfig.java
│   │   ├── api/v1/                       # PropertyV1Controller, BookingV1Controller, FavoriteController,
│   │   │                                 # ReviewController, StatsController
│   │   ├── config/                       # PropertySearchConfig (RabbitMQ queue/binding for ES indexing)
│   │   ├── domain/
│   │   │   ├── document/                 # PropertyDocument (@Document for Elasticsearch)
│   │   │   ├── entity/                   # Property, PropertyImage, PropertyVideo, Amenity,
│   │   │   │                             # Booking, Favorite, Review, ReportedListing
│   │   │   ├── enums/                    # PropertyType, PropertyStatus, ListingType,
│   │   │   │                             # BookingStatus, BookingType, AmenityCategory
│   │   │   └── repository/              # PropertyRepository, BookingRepository, FavoriteRepository,
│   │   │                                 # ReviewRepository, AmenityRepository, PropertySearchRepository, + more
│   │   ├── dto/
│   │   │   ├── request/                  # PropertyCreateRequest, BookingCreateRequest, ReviewCreateRequest, etc.
│   │   │   └── response/                # PropertyDto, BookingDto, FavoriteDto, ReviewDto, ReportDto, etc.
│   │   ├── mapper/                       # PropertyMapper, BookingMapper, FavoriteMapper,
│   │   │                                 # ReviewMapper, ReportMapper, AdminMapper
│   │   └── service/                      # PropertyService, PropertySearchService, PropertyIndexConsumer,
│   │                                     # BookingService, FavoriteService, ReviewService
│   └── vehicle/
│       ├── VehicleModuleConfig.java
│       ├── api/v1/                       # VehicleV1Controller
│       ├── domain/
│       │   ├── entity/                   # Vehicle, VehicleImage, VehicleBooking, ConditionReport
│       │   ├── enums/                    # FuelType, Transmission, VehicleStatus, VehicleBookingStatus
│       │   └── repository/              # VehicleRepository, VehicleImageRepository,
│       │                                 # VehicleBookingRepository, ConditionReportRepository
│       ├── dto/
│       │   ├── request/                  # VehicleCreateRequest, VehicleUpdateRequest
│       │   └── response/                # VehicleResponse, VehicleImageDto, VehicleSearchParams,
│       │                                 # ConditionReportResponse
│       ├── mapper/                       # VehicleMapper
│       └── service/                      # VehicleService, VehicleAvailabilityService
```

### Frontend: `rental-app-frontend/`

- **Angular 21** (standalone components), Ionic 8.8, TailwindCSS, TypeScript 5.9
- **Lazy-loaded feature modules:** auth, properties, bookings, chat, profile, admin, favorites
- `core/guards/` — AuthGuard, RoleGuard, PublicAccessGuard, AdminGuard
- `core/interceptors/` — authInterceptor (JWT), errorInterceptor (401 refresh)
- `core/services/` — Domain services (auth, property, booking, chat, payment, websocket)
- `core/state/` — Signal-based state management (auth, property)
- `environments/` — Dev/prod configs with `apiUrl` and `wsUrl`
- **Mobile:** Capacitor 8 for Android/iOS

#### Frontend Tree (`src/app/`)

```
├── app.component.ts/html/scss
├── app.config.ts
├── app.routes.ts
├── core/
│   ├── guards/                      # admin.guard, auth.guard, public-access.guard, role.guard
│   ├── interceptors/                # auth.interceptor, error.interceptor
│   ├── services/
│   │   ├── admin/                   # admin.service
│   │   ├── auth/                    # auth.service, token-storage.service
│   │   ├── booking/                 # booking.service
│   │   ├── capacitor/               # capacitor.service
│   │   ├── chat/                    # chat.service
│   │   ├── favorite/                # favorite.service
│   │   ├── loading/                 # loading.service
│   │   ├── notification/            # notification.service
│   │   ├── property/                # property.service
│   │   ├── review/                  # review.service
│   │   ├── storage/                 # storage.service
│   │   ├── theme/                   # theme.service
│   │   ├── toast/                   # toast.service
│   │   ├── user/                    # user.service
│   │   └── websocket/               # websocket.service
│   ├── state/                       # auth.state, property.state
│   └── utils/                       # currency.utils, validators
├── features/
│   ├── admin/
│   │   ├── admin.routes.ts
│   │   ├── dashboard/               # admin-dashboard.component
│   │   ├── layout/                  # admin-layout.component
│   │   ├── properties/              # admin-properties.component
│   │   ├── reports/                 # admin-reports.component
│   │   └── users/                   # admin-users.component
│   ├── auth/
│   │   ├── auth.module.ts
│   │   ├── login/                   # login.component
│   │   ├── register/                # register.component
│   │   ├── forgot-password/         # forgot-password.component
│   │   └── reset-password/          # reset-password.component
│   ├── bookings/
│   │   ├── bookings.module.ts
│   │   ├── list/                    # bookings-list.component
│   │   └── detail/                  # booking-detail.component
│   ├── chat/
│   │   ├── chat.module.ts
│   │   ├── list/                    # chat-list.component
│   │   └── room/                    # chat-room.component
│   ├── favorites/
│   │   ├── favorites.routes.ts
│   │   └── favorites.component.ts
│   ├── landing/                     # landing.component
│   ├── profile/
│   │   ├── profile.module.ts
│   │   ├── profile/                 # profile.component
│   │   ├── edit-profile/            # edit-profile.component
│   │   └── change-password/         # change-password.component
│   └── properties/
│       ├── properties.routes.ts
│       ├── property-list/           # property-list.component
│       ├── property-detail/         # property-detail.component
│       ├── property-card/           # property-card.component
│       ├── property-filters/        # property-filters.component
│       ├── my-properties/           # my-properties.component
│       ├── add-property/            # add-property.component
│       └── booking-card/            # booking-card.component
├── shared/
│   ├── shared.module.ts
│   ├── components/                  # header, footer, loader, skeleton
│   ├── directives/
│   └── pipes/
├── models/                          # property.model, user.model
├── types/                           # api.types
└── environments/                    # environment.ts, environment.prod.ts
```

### Docker (`docker-compose.yml`)

- **6 services** on `rental-network` (bridge):
  - `frontend` — Nginx (serves Angular SPA, reverse proxies `/api/*` and `/ws/*` to backend)
  - `backend` — Spring Boot JAR (Gradle + Java 21, non-root user)
  - `db` — PostgreSQL 16 (database: `homeflex`)
  - `redis` — Redis 7 (caching, rate limiting, distributed locking)
  - `rabbitmq` — RabbitMQ 3 (async messaging, event fanout; management UI on :15672)
  - `elasticsearch` — Elasticsearch 9 (full-text + geo search)
- All services have health checks; backend waits for db, redis, rabbitmq, elasticsearch to be healthy
- Multi-stage Dockerfiles with layer caching for fast rebuilds
- Non-root users in both frontend (nginx) and backend containers
- Nginx config includes: gzip, security headers, static asset caching (1yr), WebSocket proxy, Swagger proxy

## Key Patterns & Conventions

- **DTOs are Java records** with Jakarta validation annotations on request DTOs. Entities never leak to the API layer.
- **Transactions:** `@Transactional` on service methods (write), `@Transactional(readOnly = true)` for reads. Never on controllers or repositories.
- **Date/time:** `java.time` exclusively. Store as `TIMESTAMPTZ` in PostgreSQL. Never `java.util.Date`.
- **Flyway migrations** are immutable and versioned (`V1__`, `V2__`, etc.). Hibernate `ddl-auto: validate` in production.
- **Outbox pattern:** `EventOutboxService.enqueue()` writes events to `outbox_events`; `OutboxRelayService` polls with `FOR UPDATE SKIP LOCKED`, publishes to RabbitMQ, marks processed on ACK. Consumers (e.g. `PropertyIndexConsumer`) react to domain events.
- **Logging:** SLF4J via Lombok `@Slf4j`. Never log sensitive data.
- **API versioning:** `/api/v1/` prefix. See `docs/adr/ADR-002` for rationale.
- **Frontend forms:** Reactive Forms for complex forms (never template-driven).
- **Angular components:** Standalone (no NgModules for new features).
- **i18n:** ngx-translate with HttpLoader. Translation files in assets.

## Auth & Security

- JWT: 15min access token in `ACCESS_TOKEN` httpOnly/Secure/SameSite=Strict cookie, 7-day refresh token in `REFRESH_TOKEN` cookie (scoped to `/api/v1/auth`)
- **No Authorization header** — JwtAuthenticationFilter reads exclusively from cookies (XSS-safe)
- **CSRF protection** — `CookieCsrfTokenRepository.withHttpOnlyFalse()` + `SpaCsrfTokenRequestHandler` for Angular 21 compatibility; auth/webhook/ws endpoints exempted
- **Rate limiting** — Redis-backed `RateLimitFilter` (Lua atomic INCR+EXPIRE): 100 req/min authenticated, 20 req/min public. Returns 429 with `Retry-After` header. Fails open on Redis unavailability.
- Roles: TENANT, LANDLORD, ADMIN
- Public endpoints: `/api/v1/auth/**`, property GET (search), vehicle GET (search/detail), Swagger, actuator (dev only)
- Protected: bookings, chat, payments require authentication; admin endpoints require ADMIN role
- CORS: localhost:4200 (dev), configurable for production

## Testing

- **Backend:** JUnit 5, Mockito, ArchUnit (architecture tests), Spring Security Test
- **Frontend:** Jasmine + Karma (can migrate to Jest)
- **CI:** GitHub Actions (`.github/workflows/ci.yml`) runs on push/PR to main

## Important References

- **AGENT.md** — Comprehensive engineering standards and AI orchestration guide (read this for detailed conventions)
- **docs/adr/** — Architecture Decision Records
- **docs/architecture-guardrails.md** — Engineering constraints
- **SRS.md** — Software requirements specification
- **rental-backend/.env** — Environment variables template (JWT_SECRET, DB creds, AWS, Firebase)
