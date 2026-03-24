# ADR-001: DDD Module Boundaries

## Status
Accepted

## Context
HomeFlex backend is currently organized by technical layers. The SRS requires bounded contexts and DDD-style modules.

## Decision
Use package-by-domain with five top-level concerns:
- `domain`
- `application`
- `api`
- `infrastructure`
- `shared`

Domain slices start with `property`, `booking`, and `user`.

## Consequences
- Enables incremental migration with compatibility controllers.
- Reduces cross-layer coupling and clarifies ownership.
- Requires temporary bridges between legacy services and new application services.
