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
./gradlew test --tests "com.realestate.rental.SomeTest"  # Single test class
./gradlew test --tests "com.realestate.rental.SomeTest.someMethod"  # Single test method
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
- **Layered architecture:** Controller → Service → Repository → Database
- **Package:** `com.realestate.rental`
  - `api/v1/` — All REST controllers (thin: validate, delegate, return)
  - `domain/entity/` — JPA entities
  - `domain/enums/` — Domain enumerations (UserRole, BookingStatus, PropertyType, etc.)
  - `domain/repository/` — Spring Data JPA repositories
  - `domain/event/` — Domain events (OutboxEvent)
  - `service/` — Business logic services
  - `mapper/` — Object mapping (DTO ↔ Entity)
  - `dto/request/` — Inbound request DTOs (Java records, Jakarta validation)
  - `dto/response/` — Outbound response DTOs
  - `dto/common/` — API response wrappers (ApiPageResponse, ApiListResponse, ApiValueResponse)
  - `security/` — JWT filter + token provider
  - `config/` — Spring configuration (Security, WebSocket, Firebase, AppProperties)
  - `exception/` — Global `@RestControllerAdvice` + custom exceptions
  - `infrastructure/` — External integrations (notifications)
- **Database:** PostgreSQL, Flyway migrations in `src/main/resources/db/migration/`
- **DI:** Constructor injection via Lombok `@RequiredArgsConstructor` (never `@Autowired` on fields)
- **All API endpoints** prefixed `/api/v1/`
- **Swagger UI** at `/swagger-ui.html`

#### Backend Tree (`com.realestate.rental`)

```
├── RealEstateRentalApplication.java
├── api/v1/
│   ├── AdminController.java
│   ├── AuthV1Controller.java
│   ├── BookingV1Controller.java
│   ├── ChatController.java
│   ├── FavoriteController.java
│   ├── NotificationController.java
│   ├── PropertyV1Controller.java
│   ├── ReviewController.java
│   ├── StatsController.java
│   ├── UserController.java
│   └── WebSocketChatController.java
├── config/
│   ├── AppProperties.java
│   ├── DataInitializer.java
│   ├── FirebaseConfig.java
│   ├── SampleDataInitializer.java
│   ├── SecurityConfig.java
│   └── WebSocketConfig.java
├── domain/
│   ├── entity/                      # Amenity, Booking, ChatRoom, Favorite, FcmToken,
│   │                                # Message, Notification, OAuthProvider, Property,
│   │                                # PropertyImage, PropertyVideo, RefreshToken,
│   │                                # ReportedListing, Review, TypingNotification, User
│   ├── enums/                       # AmenityCategory, BookingStatus, BookingType,
│   │                                # ListingType, NotificationType, PropertyStatus,
│   │                                # PropertyType, UserRole
│   ├── event/                       # OutboxEvent, OutboxEventRepository
│   └── repository/                  # AmenityRepository, BookingRepository, ChatRoomRepository,
│                                    # FavoriteRepository, FcmTokenRepository, MessageRepository,
│                                    # NotificationRepository, OAuthProviderRepository,
│                                    # PropertyImageRepository, PropertyRepository,
│                                    # PropertyVideoRepository, RefreshTokenRepository,
│                                    # ReportedListingRepository, ReviewRepository, UserRepository
├── dto/
│   ├── request/                     # BookingCreateRequest, LoginRequest, RegisterRequest,
│   │                                # PropertyCreateRequest, etc. (20+ request DTOs)
│   ├── response/                    # AuthResponse, BookingDto, PropertyDto, UserDto,
│   │                                # ChatRoomDto, MessageDto, NotificationDto, etc.
│   └── common/                      # ApiListResponse, ApiPageResponse, ApiValueResponse
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ConflictException.java
│   ├── DomainException.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── infrastructure/notification/
│   ├── NotificationGateway.java     # Interface
│   └── FirebaseNotificationGateway.java
├── mapper/                          # AdminMapper, BookingMapper, ChatMapper, FavoriteMapper,
│                                    # NotificationMapper, PropertyMapper, ReportMapper,
│                                    # ReviewMapper, UserMapper
├── security/
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
└── service/                         # AdminService, AuthService, BookingService, ChatService,
                                     # EmailService, EventOutboxService, FavoriteService,
                                     # NotificationService, PaymentService, PropertyService,
                                     # ReviewService, StorageService, UserService
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
  - `elasticsearch` — Elasticsearch 8 (full-text + geo search)
- All services have health checks; backend waits for db, redis, rabbitmq, elasticsearch to be healthy
- Multi-stage Dockerfiles with layer caching for fast rebuilds
- Non-root users in both frontend (nginx) and backend containers
- Nginx config includes: gzip, security headers, static asset caching (1yr), WebSocket proxy, Swagger proxy

## Key Patterns & Conventions

- **DTOs are Java records** with Jakarta validation annotations on request DTOs. Entities never leak to the API layer.
- **Transactions:** `@Transactional` on service methods (write), `@Transactional(readOnly = true)` for reads. Never on controllers or repositories.
- **Date/time:** `java.time` exclusively. Store as `TIMESTAMPTZ` in PostgreSQL. Never `java.util.Date`.
- **Flyway migrations** are immutable and versioned (`V1__`, `V2__`, etc.). Hibernate `ddl-auto: validate` in production.
- **Logging:** SLF4J via Lombok `@Slf4j`. Never log sensitive data.
- **API versioning:** `/api/v1/` prefix. See `docs/adr/ADR-002` for rationale.
- **Frontend forms:** Reactive Forms for complex forms (never template-driven).
- **Angular components:** Standalone (no NgModules for new features).
- **i18n:** ngx-translate with HttpLoader. Translation files in assets.

## Auth & Security

- JWT: 15min access token (Bearer header), 7-day refresh token
- Roles: TENANT, LANDLORD, ADMIN
- Public endpoints: `/api/v1/auth/**`, property GET (search), Swagger, actuator (dev only)
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
