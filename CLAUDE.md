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

## Module Structure

- `homeflex-web`: Angular 21 frontend. Features logic in `src/app/features/`.
- `rental-backend`: Spring Boot 4 / Java 21 API.
  - `core/`: Auth, Security, Infrastructure, Common Services.
  - `features/`: Business modules (Property, Vehicle, Booking, Lease).
- `rental-app-flutter`: Flutter mobile app.

## Development Commands

- **Backend Build**: `./gradlew build` (in `rental-backend`)
- **Backend Run**: `./gradlew bootRun`
- **Frontend Test**: `npm test` (in `homeflex-web`)
- **Frontend Build**: `npm run build`

## Key Database Tables

- `users`: Core identity and roles.
- `properties` / `vehicles`: Rental assets.
- `bookings`: Central reservation engine.
- `property_availability`: Sparse date model for asset availability.
- `property_leases`: Digital contract tracking.
- `outbox_events`: Queue for reliable event delivery.
- `processed_stripe_events`: Idempotency log for Stripe webhooks.
