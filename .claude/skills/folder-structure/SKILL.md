---
name: folder-structure
description: >
  Generate idiomatic folder structures for any language and architectural style. Trigger this skill
  whenever the user asks to "scaffold a project", "set up the folder structure for X", "organize X
  following Y architecture", "create the file layout for X", "how should I structure X", or "generate
  the directory tree for X". Adapts output to the detected language (Java/Spring Boot, TypeScript/Angular,
  Python/FastAPI, Go, Rust, Flutter/Dart) and architectural pattern (Layered, Hexagonal/Ports & Adapters,
  Clean Architecture, Feature-Sliced, Vertical Slice, Modular Monolith, Event-Driven). Always apply this
  — never freehand project scaffolding.
---

# Folder Structure Skill

## How to Use This Skill

1. **Identify the language/framework** from context (imports, file extensions, dependencies).
2. **Identify the architecture** from context or ask if ambiguous.
3. **Apply the matching template** below — substitute `<domain>`, `<feature>`, `<entity>` with real names.
4. **Output an annotated directory tree** and explain the role of each folder.

---

## Architecture Decision Guide

| Style                  | Best For                                                           | Key Signal in Request                            |
| ---------------------- | ------------------------------------------------------------------ | ------------------------------------------------ |
| **Layered (N-Tier)**   | Simple CRUDs, small teams, quick APIs                              | "standard", "simple", "basic"                    |
| **Hexagonal**          | Domain logic must be framework-independent, high testability       | "ports and adapters", "testable", "DDD-light"    |
| **Clean Architecture** | Complex domain, multiple delivery mechanisms (REST + CLI + events) | "Uncle Bob", "use cases", "entities/interactors" |
| **Feature-Sliced**     | Large frontends with many independent features                     | "Angular", "React", "feature modules"            |
| **Vertical Slice**     | Teams own full vertical slices (UI → DB) per feature               | "CQRS", "minimal coupling", "slice per feature"  |
| **Modular Monolith**   | Monolith with clear module boundaries ready to split               | "bounded contexts", "future microservices"       |
| **Event-Driven**       | Async processing, outbox pattern, pub/sub                          | "RabbitMQ", "Kafka", "events", "outbox"          |

---

## Java / Spring Boot 4

### A. Layered Architecture (current HomeFlex backend)

```
rental-backend/src/main/java/com/homeflex/
├── core/                          # Cross-cutting concerns
│   ├── config/                    # Spring configs (Security, WebSocket, JPA, CORS)
│   │   ├── SecurityConfig.java
│   │   ├── DataInitializer.java   # @Profile("!prod") seed data
│   │   └── AppProperties.java
│   ├── security/                  # Filters, JWT, CSRF
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   ├── RateLimitFilter.java
│   │   └── MetricsTokenFilter.java
│   ├── exception/                 # GlobalExceptionHandler + domain exceptions
│   ├── service/                   # Shared services (StorageService, EmailService, etc.)
│   └── domain/                    # Shared entities, enums, repositories
│       ├── entity/
│       ├── enums/
│       └── repository/
│
└── features/                      # One package per bounded context
    └── <domain>/                  # e.g. property, vehicle, booking, finance
        ├── api/
        │   └── v1/
        │       └── <Domain>V1Controller.java   # @RestController, no logic
        ├── service/
        │   └── <Domain>Service.java            # @Service, @Transactional
        ├── domain/
        │   ├── entity/
        │   │   └── <Domain>.java               # @Entity
        │   └── repository/
        │       └── <Domain>Repository.java     # extends JpaRepository
        ├── dto/
        │   ├── <Domain>Dto.java                # Response
        │   ├── Create<Domain>Request.java
        │   └── Update<Domain>Request.java
        └── mapper/
            └── <Domain>Mapper.java             # MapStruct interface
```

**Rules:**

- Controllers → Services → Repositories (never skip layers)
- No `@Autowired` — use `@RequiredArgsConstructor` (Lombok)
- One `@Entity` per table; one `Service` per aggregate root
- All DTOs use `record` or immutable classes
- `@Transactional(readOnly = true)` at class level; override writes with `@Transactional`

---

### B. Hexagonal Architecture (Ports & Adapters)

```
src/main/java/com/homeflex/<domain>/
├── domain/                        # Pure Java — NO framework annotations
│   ├── model/
│   │   └── <Entity>.java          # Plain Java class / value objects
│   ├── port/
│   │   ├── in/                    # Use-case interfaces (driving ports)
│   │   │   ├── Create<Entity>UseCase.java
│   │   │   └── Get<Entity>UseCase.java
│   │   └── out/                   # Infrastructure interfaces (driven ports)
│   │       ├── <Entity>Repository.java    # Interface — no Spring Data here
│   │       └── <Entity>EventPublisher.java
│   └── service/                   # Domain services implementing use-case ports
│       └── <Entity>DomainService.java     # implements Create<Entity>UseCase
│
├── application/                   # Orchestrates domain; framework allowed here
│   └── <Entity>ApplicationService.java   # Thin orchestrator; calls domain services
│
└── adapter/
    ├── in/                        # Driving adapters
    │   ├── web/
    │   │   └── <Entity>Controller.java    # @RestController
    │   └── messaging/
    │       └── <Entity>Consumer.java      # @RabbitListener
    └── out/                       # Driven adapters
        ├── persistence/
        │   ├── <Entity>JpaEntity.java     # @Entity (JPA-specific)
        │   ├── <Entity>JpaRepository.java # extends JpaRepository
        │   └── <Entity>PersistenceAdapter.java  # implements domain port
        └── messaging/
            └── <Entity>RabbitPublisher.java     # implements event publisher port
```

**Rules:**

- `domain/` package: zero Spring imports — pure business logic
- Ports are interfaces; adapters are implementations
- Domain model ≠ JPA entity (mapped separately in persistence adapter)
- Test domain services with plain JUnit — no Spring context needed

---

### C. Vertical Slice Architecture

```
src/main/java/com/homeflex/
└── feature/
    └── <feature>/                 # e.g. CreateBooking, ApproveProperty
        ├── <Feature>Command.java          # Input record (immutable)
        ├── <Feature>Result.java           # Output record
        ├── <Feature>Handler.java          # @Component — single public handle() method
        ├── <Feature>Controller.java       # @RestController — delegates to handler
        ├── <Feature>Validator.java        # Business rule validation
        └── <Feature>IntegrationTest.java  # Full-stack test per slice
```

**Rules:**

- No shared service layer — each slice owns its own query/mutation logic
- Cross-slice communication via domain events, not direct calls
- Each slice can choose its own persistence strategy (JPA, JDBC, ES)
- Ideal with MediatR-style dispatcher (Spring's `ApplicationEventPublisher`)

---

## TypeScript / Angular 21

### Feature-Sliced Design (current HomeFlex frontend)

```
homeflex-web/src/app/
│
├── core/                          # Singleton services, loaded once at app init
│   ├── api/
│   │   ├── api.client.ts          # Base HttpClient wrapper (withCredentials, XSRF)
│   │   └── services/              # Domain-specific API services
│   │       ├── base.api.ts        # abstract BaseApi with error handling
│   │       ├── auth.api.ts
│   │       ├── property.api.ts
│   │       └── admin.api.ts
│   ├── models/
│   │   └── api.types.ts           # All TypeScript interfaces — single source of truth
│   ├── guards/
│   │   ├── auth.guard.ts          # Redirect unauthenticated to /login
│   │   └── admin.guard.ts         # Redirect non-ADMIN to /admin/login
│   ├── interceptors/
│   │   └── credentials.interceptor.ts  # withCredentials + X-XSRF-TOKEN
│   └── state/
│       └── session.store.ts       # NgRx Signal Store — auth session
│
├── shared/                        # Reusable dumb components, pipes, directives
│   ├── components/
│   │   ├── button/
│   │   ├── modal/
│   │   └── spinner/
│   ├── pipes/
│   │   └── currency-format.pipe.ts
│   └── directives/
│       └── click-outside.directive.ts
│
├── features/                      # One folder per product feature
│   ├── auth/                      # Login, register, forgot-password
│   │   ├── login/
│   │   │   ├── login.component.ts
│   │   │   ├── login.component.html
│   │   │   └── login.component.spec.ts
│   │   └── register/
│   ├── workspace/                 # Main tenant/landlord dashboard
│   │   ├── workspace.component.ts
│   │   ├── pages/
│   │   │   ├── bookings/
│   │   │   ├── properties/
│   │   │   └── profile/
│   │   └── workspace.routes.ts
│   └── admin/                     # Separate admin console
│       ├── login/
│       ├── dashboard/
│       ├── users/
│       ├── properties/
│       ├── reports/
│       ├── settings/
│       ├── admin-layout.component.ts
│       └── admin.routes.ts
│
├── shell/                         # App shell (header, footer, layout wrapper)
│   ├── header/
│   ├── footer/
│   └── shell.component.ts         # Hides header/footer on isAdminRoute signal
│
├── app.routes.ts                  # Root router config
├── app.config.ts                  # provideRouter, provideHttpClient, interceptors
└── app.component.ts               # Root component — just <router-outlet>
```

**Component rules:**

- `standalone: true` — no NgModules ever
- `changeDetection: ChangeDetectionStrategy.OnPush` — always
- State via `signal()` / `computed()` / NgRx Signal Store — no `BehaviorSubject`
- `inject()` function — no constructor injection
- Smart (page) components talk to services; dumb components get `@Input()` / emit `@Output()`
- Every component file: `.ts` + `.html` + `.spec.ts` — no inline templates beyond ~10 lines

---

## Python / FastAPI

### Clean Architecture

```
app/
├── domain/                        # Pure Python — no FastAPI/SQLAlchemy imports
│   ├── entities/
│   │   └── <entity>.py            # Dataclass or Pydantic BaseModel (domain layer)
│   ├── repositories/
│   │   └── i_<entity>_repository.py   # Abstract base class (port)
│   └── services/
│       └── <entity>_service.py        # Business logic, uses repo interface
│
├── application/                   # Use cases / command handlers
│   └── <feature>/
│       ├── commands.py            # Pydantic request models
│       ├── responses.py           # Pydantic response models
│       └── handlers.py            # Orchestrates domain services
│
├── infrastructure/                # Framework & IO implementations
│   ├── db/
│   │   ├── models.py              # SQLAlchemy ORM models
│   │   ├── repositories/
│   │   │   └── <entity>_repo.py   # Implements domain repository port
│   │   └── database.py            # Session factory, engine
│   ├── cache/
│   │   └── redis_client.py
│   └── messaging/
│       └── rabbitmq_publisher.py
│
├── api/                           # FastAPI routers — thin layer only
│   ├── v1/
│   │   ├── router.py              # APIRouter combining all feature routers
│   │   └── <feature>/
│   │       ├── routes.py          # @router.get / @router.post
│   │       └── dependencies.py    # Depends() factories (auth, db session)
│   └── middleware/
│       ├── auth.py                # JWT middleware
│       └── rate_limit.py
│
├── core/
│   ├── config.py                  # pydantic-settings BaseSettings
│   ├── security.py                # JWT encode/decode, bcrypt
│   └── exceptions.py              # Custom exception classes
│
├── tests/
│   ├── unit/                      # Test domain services (no DB/FastAPI)
│   ├── integration/               # Test DB repositories
│   └── e2e/                       # Test full HTTP stack (TestClient)
│
├── alembic/                       # DB migrations
│   └── versions/
├── main.py                        # App factory: create_app()
└── requirements.txt
```

---

## Go

### Standard Go Layout (Clean Architecture variant)

```
cmd/
└── api/
    └── main.go                    # Entrypoint — wire everything up

internal/
├── domain/                        # Business logic — no framework imports
│   ├── <entity>/
│   │   ├── entity.go              # Struct + value objects
│   │   ├── repository.go          # Interface (port)
│   │   └── service.go             # Business rules
│   └── errors.go                  # Domain error types
│
├── application/                   # Use cases
│   └── <feature>/
│       ├── command.go
│       └── handler.go
│
├── infrastructure/                # Implementations
│   ├── postgres/
│   │   └── <entity>_repo.go       # Implements domain repository
│   ├── redis/
│   │   └── cache.go
│   └── rabbitmq/
│       └── publisher.go
│
└── api/
    ├── http/
    │   ├── handler/
    │   │   └── <entity>_handler.go  # net/http or Gin handler
    │   ├── middleware/
    │   │   ├── auth.go
    │   │   └── rate_limit.go
    │   └── router.go
    └── grpc/                        # Optional gRPC layer
        └── <entity>_server.go

pkg/                               # Exported shared utilities (safe to import from other projects)
├── jwt/
└── crypto/

migrations/                        # golang-migrate SQL files
config/
└── config.go                      # env var struct with validation
```

---

## Rust (Axum / Actix)

### Hexagonal Architecture

```
src/
├── main.rs                        # Entrypoint: build router, inject dependencies
│
├── domain/                        # Pure Rust — zero framework deps
│   ├── mod.rs
│   ├── <entity>.rs                # Struct + impl + value objects
│   ├── repository.rs              # trait <Entity>Repository
│   └── service.rs                 # Business logic using trait objects
│
├── application/                   # Use cases / command handlers
│   ├── mod.rs
│   └── <feature>/
│       ├── command.rs
│       └── handler.rs
│
├── infrastructure/                # Framework implementations
│   ├── db/
│   │   ├── mod.rs
│   │   ├── <entity>_sqlx_repo.rs  # impl <Entity>Repository for SqlxRepo
│   │   └── models.rs              # SQLx row types (separate from domain)
│   ├── cache/
│   │   └── redis.rs
│   └── messaging/
│       └── rabbitmq.rs
│
├── api/                           # HTTP layer (Axum/Actix)
│   ├── mod.rs
│   ├── router.rs                  # build_router() fn
│   ├── handlers/
│   │   └── <entity>_handler.rs    # async fn — extract → call use case → respond
│   ├── middleware/
│   │   ├── auth.rs                # JWT extractor
│   │   └── rate_limit.rs
│   └── errors.rs                  # IntoResponse for domain errors
│
└── config.rs                      # envy / config crate structs

migrations/                        # sqlx migrate files
tests/
├── unit/
└── integration/
```

---

## Flutter / Dart

### Feature-First Clean Architecture

```
lib/
├── main.dart                      # Entry point — ProviderScope / MaterialApp

├── core/
│   ├── network/
│   │   ├── api_client.dart        # Dio client with interceptors (JWT, CSRF)
│   │   └── api_endpoints.dart     # All URL constants
│   ├── storage/
│   │   └── secure_storage.dart    # flutter_secure_storage wrapper
│   ├── error/
│   │   ├── failures.dart          # Sealed class hierarchy
│   │   └── exceptions.dart
│   └── utils/
│       └── validators.dart
│
├── features/
│   └── <feature>/                 # e.g. auth, property, booking
│       ├── data/
│       │   ├── datasources/
│       │   │   └── <feature>_remote_ds.dart    # Dio calls
│       │   ├── models/
│       │   │   └── <feature>_model.dart        # JSON serializable (freezed)
│       │   └── repositories/
│       │       └── <feature>_repo_impl.dart    # Implements domain repo
│       │
│       ├── domain/
│       │   ├── entities/
│       │   │   └── <feature>_entity.dart       # Immutable domain object (freezed)
│       │   ├── repositories/
│       │   │   └── i_<feature>_repository.dart # Abstract interface
│       │   └── usecases/
│       │       └── get_<feature>.dart          # Single-responsibility use case
│       │
│       └── presentation/
│           ├── providers/
│           │   └── <feature>_provider.dart     # Riverpod AsyncNotifierProvider
│           ├── pages/
│           │   └── <feature>_page.dart         # ConsumerWidget
│           └── widgets/
│               └── <feature>_card.dart         # Reusable dumb widget
│
└── shared/
    ├── widgets/
    │   ├── loading_indicator.dart
    │   └── error_widget.dart
    └── theme/
        └── app_theme.dart
```

**Flutter rules:**

- State via Riverpod `AsyncNotifierProvider` — no `setState` in page-level widgets
- Domain layer: zero Flutter imports — plain Dart
- All models use `freezed` for immutability and `json_serializable` for JSON
- Repository pattern: `domain/repositories/` interface ← `data/repositories/` implementation
- `Either<Failure, T>` return type from use cases (functional error handling)

---

## Output Format

When generating a structure, always:

1. Show the annotated tree (use `#` comments on the same line for each folder/file)
2. State the chosen architecture and why
3. List the 3–5 most important conventions for that stack
4. Offer to generate the boilerplate for any file in the tree on request

### Example Output

```
Chosen architecture: Layered (N-Tier)
Reason: HomeFlex uses this pattern — controllers → services → repositories with MapStruct.

rental-backend/src/main/java/com/homeflex/features/payment/
├── api/v1/
│   └── PaymentV1Controller.java    # @RestController — delegates only, no logic
├── service/
│   └── PaymentService.java         # @Service @Transactional — all business rules here
├── domain/
│   ├── entity/
│   │   └── Payment.java            # @Entity — maps to payments table
│   └── repository/
│       └── PaymentRepository.java  # extends JpaRepository<Payment, UUID>
├── dto/
│   ├── PaymentDto.java             # Response DTO
│   ├── CreatePaymentRequest.java   # Validated input DTO (@Valid)
│   └── PaymentStatusUpdate.java    # Partial update DTO
└── mapper/
    └── PaymentMapper.java          # @Mapper(componentModel = "spring")

Conventions:
1. Controllers never touch repositories — always go through the service.
2. @Transactional(readOnly = true) at class level; override writes with @Transactional.
3. MapStruct mapper for all Entity ↔ DTO conversions — no manual mapping.
4. UUIDs as primary keys (matches existing homeflex schema).
5. Flyway migration required for every schema change (see spring-migration skill).
```
