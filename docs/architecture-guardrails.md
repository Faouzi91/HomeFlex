# HomeFlex Architecture Guardrails

## Backend

### Package-by-Feature Structure

All backend code lives under `com.homeflex` using a **package-by-feature** architecture:

- **`com.homeflex.core`** — Cross-cutting concerns (config, security, exception, shared entities, services, controllers)
- **`com.homeflex.features.<feature>`** — Feature modules (property, vehicle, etc.)

### Feature Module Template

Every new feature module under `com.homeflex.features.<feature>` **must** follow this folder structure:

```
com.homeflex.features.<feature>/
├── <Feature>ModuleConfig.java           # @Configuration + @AutoConfigurationPackage + @EnableJpaRepositories
├── api/v1/                              # REST controllers (thin: validate, delegate, return)
├── domain/
│   ├── entity/                          # JPA entities
│   ├── enums/                           # Domain enumerations
│   └── repository/                      # Spring Data JPA repositories
├── dto/
│   ├── request/                         # Inbound DTOs (Java records, Jakarta validation)
│   └── response/                        # Outbound DTOs
├── mapper/                              # MapStruct mappers (Entity ↔ DTO)
└── service/                             # Business logic (single concrete class per service, no interface+impl)
```

### Rules

- New REST endpoints must be under `/api/v1/`.
- DTOs are Java records with Jakarta validation on request DTOs. Entities never leak to the API layer.
- Services are concrete classes annotated with `@Service` — no separate interface unless there are multiple implementations.
- Constructor injection via Lombok `@RequiredArgsConstructor` (never `@Autowired` on fields).
- `@Transactional` on service methods (write), `@Transactional(readOnly = true)` for reads. Never on controllers or repositories.
- Each feature module has a `*ModuleConfig.java` with `@AutoConfigurationPackage` (entity scanning) and `@EnableJpaRepositories` (repo scanning).
- Cross-module imports: features depend on `core`; `core` may reference feature entities where necessary (e.g., AdminService).
- Legacy `com.realestate.rental` package is fully retired — no new code under that namespace.

## Frontend

- New features should prefer standalone components and route-based composition.
- `WebSocketService` is the single owner of STOMP lifecycle.
- New global state should be added via `core/state/*`, not ad-hoc service subjects.
- New NgModules require explicit migration exception in PR description.
