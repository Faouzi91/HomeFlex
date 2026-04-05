# Changelog

All notable changes to the HomeFlex project will be documented in this file.

## [Unreleased] — 2026-04-05 (Round 5)

### Fixes — Architecture Violation (Backend)

- **Moved payment webhook logic out of controller** — `StripeWebhookController` no longer
  imports `BookingRepository` directly. New `BookingService.handlePaymentSucceeded()` and
  `BookingService.handlePaymentFailed()` methods encapsulate the booking status updates.
  `ArchitectureGuardrailsTest` now passes (controllers ↛ repositories).
- **Deleted unused `NotificationGateway` interface** — The interface was not referenced by
  any consumer; all notification calls go through `NotificationService` directly.

### Fixes — Circular Dependency (Backend)

- **Broke `NotificationService` ↔ `FirebaseNotificationGateway` cycle** —
  `FirebaseNotificationGateway` no longer depends on `NotificationService`. Removed the
  delegation methods (`sendNewMessage`, `sendBookingRequest`, etc.) that called back into
  `NotificationService`, keeping only the `sendPush()` method. The `@Lazy` workaround on
  `NotificationService` was removed since it didn't work with `@RequiredArgsConstructor`.
- **Spring context now loads cleanly** — `HomeFlexApplicationTests.contextLoads()` no longer
  fails with `BeanCurrentlyInCreationException`.

### Fixes — Test Infrastructure (Backend)

- **Tagged `contextLoads` as integration test** — Added `@Tag("integration")` and
  `@ActiveProfiles("test")` so it is excluded from `./gradlew test` (which runs unit tests
  only). Run integration tests separately via `./gradlew integrationTest`.
- **Created `application-test.yml`** — Provides defaults for `JWT_SECRET`, Stripe keys,
  and disables Flyway/Firebase/AWS/outbox so the test profile can boot without env vars.
- **Configured Gradle test task** — `excludeTags 'integration'` in the default `test` task;
  added a separate `integrationTest` task with `includeTags 'integration'`.

### Features — Elasticsearch Full-Text Search (Frontend)

- **Added search bar to property list** — New search input with clear button on the
  property list page. Queries the `q` parameter of `GET /api/v1/properties/search`,
  which hits the Elasticsearch-backed `PropertySearchService` for fuzzy full-text search.
- **Updated `PropertyStore`** — Added `q` to `PropertyFilters` interface, `buildHttpParams`,
  and `hasActiveFilters` computed signal.
- **Updated `PropertyListComponent`** — Added `searchQuery` field, `onSearch()` method,
  reads `q` from query params, clears on `clearFilters()`.

### Fixes — Vehicle Booking Form Validation (Frontend)

- **Added date validation to vehicle booking form** — Start date cannot be in the past,
  end date must be on or after start date. HTML `min` attributes enforce constraints at
  browser level; TypeScript `dateError` / `datesValid` getters provide inline error messages.
- **Booking button now requires availability check** — "Confirm Booking" is disabled until
  availability is explicitly confirmed (`isAvailable === true`), not just "not false".

### Features — i18n Translation Keys

- **Added `vehicle` section to `en.json` and `fr.json`** — 40+ translation keys covering
  vehicle list, detail, booking form, filters, availability badges, error messages,
  transmission options (Automatic/Manual), and fuel type options (Petrol/Diesel/Electric/Hybrid).

---

## [Unreleased] — 2026-04-05 (Round 4)

### Fixes — Frontend Build

- **Fixed 10 broken environment imports** — Services (`AuthService`, `BookingService`,
  `ChatService`, `FavoriteService`, `NotificationService`, `UserService`,
  `VehicleService`, `WebSocketService`), the auth interceptor, and `PropertyStore`
  all used relative paths (`../../../environments/environment`) that resolved to a
  non-existent `src/environments/` directory. Normalised all imports to
  `src/app/environments/environment` so the Angular build completes without errors.
- **Frontend now builds cleanly** — `ng build` produces a successful production bundle
  with zero errors (only pre-existing Sass deprecation and CommonJS warnings remain).

### Tests — Unit Tests (Round 4)

- **Created `VehicleAvailabilityServiceTest`** — 12 unit tests covering:
  - Availability check (no overlap returns true, overlap returns false)
  - Reserve vehicle (success with correct total price calculation,
    vehicle not found, vehicle deleted, vehicle not available status,
    owner cannot book own vehicle, date overlap conflict,
    end date before start date, start date in past, null dates)
  - Get active bookings (returns list)
  - Get tenant bookings (returns empty list)
- **Created `ReviewServiceTest`** — 13 unit tests covering:
  - Create review (success, already reviewed conflict, no booking throws domain,
    property not found, user not found)
  - Get property reviews (returns mapped list, empty returns empty list)
  - Get average rating (returns average, no reviews returns null)
  - Delete review (success, review not found, wrong user unauthorized)
- **Total passing unit tests: 55** (17 AuthService + 13 BookingService +
  12 VehicleAvailabilityService + 13 ReviewService)

---

## [Unreleased] — 2026-04-05 (Round 3)

### Features — Email Verification UI (Frontend)

- **Created `VerifyEmailComponent`** — Standalone page at `/auth/verify-email?token=`
  that calls `GET /api/v1/auth/verify` and shows loading/success/error states.
- Registered route in `AuthModule`.

### Features — Vehicle Rental Module (Frontend)

- **Created vehicle data model** — `Vehicle`, `VehicleBooking`, `VehicleBookingCreateRequest`,
  `VehicleSearchParams` interfaces in `models/vehicle.model.ts`.
- **Created `VehicleService`** — Angular service with methods for search, getById,
  checkAvailability, createBooking, getMyBookings, incrementViewCount.
- **Created `VehicleCardComponent`** — Reusable card displaying vehicle image, brand/model,
  specs (transmission, fuel, mileage, seats), city, and daily price.
- **Created `VehicleListComponent`** — Searchable/filterable vehicle listing page with
  infinite scroll, filter sidebar (city, brand, transmission, fuel type, price range),
  quick-filter chips, and loading skeletons.
- **Created `VehicleDetailComponent`** — Vehicle detail page with image gallery navigation,
  specs grid, description, pickup location, and integrated booking form with
  availability checking before submission.
- **Added vehicle routes** — Lazy-loaded at `/vehicles` (list) and `/vehicles/:id` (detail)
  via `vehicles.routes.ts`, registered in `app.routes.ts`.

### Tests — Unit Tests

- **Created `AuthServiceTest`** — 17 unit tests covering:
  - Registration (success, duplicate email conflict)
  - Login (success, bad credentials, suspended account)
  - Email verification (success, invalid/expired/already-verified token)
  - Password reset send (success, unknown user)
  - Password reset execute (success, expired/used token)
  - Refresh token (success, expired)
  - Logout
- **Created `BookingServiceTest`** — 13 unit tests covering:
  - Create booking (success with payment, property not found, non-tenant role,
    date overlap conflict, invalid date range)
  - Approve booking (success confirms payment, wrong landlord unauthorized)
  - Reject booking (success cancels payment)
  - Cancel booking (success cancels payment, wrong tenant unauthorized)
  - Get booking by ID (as tenant, as landlord, unauthorized)

---

## [Unreleased] — 2026-04-05 (Rounds 1 & 2)

### Security — Auth Flow (CRITICAL)

- **Removed localStorage token storage** — Frontend `AuthService` no longer stores JWT
  tokens in `localStorage`. Tokens are delivered exclusively via httpOnly cookies set by
  the backend, eliminating XSS token-theft risk.
- **Fixed auth response DTO mismatch** — Frontend `AuthResponse` interface now matches
  the backend record (`{ user }` only; tokens are in cookies, not the response body).
- **Rewrote auth interceptor** — `authInterceptor` now sets `withCredentials: true` on
  API requests instead of injecting a `Bearer` header from localStorage. Token refresh
  retries the original request after the browser receives updated cookies.

### Features — Password Reset & Email Verification

- **Implemented password reset flow end-to-end:**
  - Created `PasswordResetToken` entity with expiry and used-at tracking.
  - Created `PasswordResetTokenRepository`.
  - `AuthService.sendPasswordResetEmail()` now persists a token (1-hour expiry) before
    sending the email, and invalidates prior tokens for the same user.
  - `AuthService.resetPassword()` validates the token, updates the password, marks the
    token used, and invalidates all refresh tokens (force re-login).
- **Implemented email verification flow end-to-end:**
  - Created `EmailVerificationToken` entity with expiry and verified-at tracking.
  - Created `EmailVerificationTokenRepository`.
  - `EmailService.generateVerificationToken()` now persists tokens (24-hour expiry)
    instead of returning a transient UUID.
  - `AuthService.verifyEmail()` validates the token and sets `user.isVerified = true`.
  - Added `GET /api/v1/auth/verify?token=` endpoint in `AuthV1Controller`.
- **Added DB migration V7** — `password_reset_tokens` and `email_verification_tokens`
  tables with indexes.

### Fixes — Payment Pipeline (CRITICAL)

- **PaymentService no longer returns `null` on failure** — All three methods
  (`createBookingPaymentIntent`, `releaseEscrow`, `getConnectedAccountBalance`) now
  throw `DomainException` on failure, which is caught by the global exception handler
  and returned as a proper 400 error to the client.
- **Added `PaymentService.confirmPaymentIntent()`** — Confirms (captures) a
  `PaymentIntent` when a landlord approves a booking.
- **Added `PaymentService.cancelPaymentIntent()`** — Cancels a `PaymentIntent` when
  a booking is rejected or cancelled by the tenant.
- **BookingService now handles payments on lifecycle transitions:**
  - `approveBooking()` confirms the PaymentIntent before changing status to APPROVED.
  - `rejectBooking()` cancels the PaymentIntent to release the hold.
  - `cancelBooking()` cancels the PaymentIntent when a tenant cancels.
  - `createBooking()` no longer silently swallows payment failures — the exception
    propagates and the booking is not created.

### Fixes — Stripe Webhook Controller

- **Webhook now updates booking status on payment events:**
  - `payment_intent.succeeded` → sets `paymentConfirmedAt` on the matching booking.
  - `payment_intent.payment_failed` → cancels the matching booking.
- **Deserialization failures now return HTTP 400** instead of silently continuing,
  so Stripe will retry the webhook delivery.
- Added `Booking.paymentConfirmedAt` field and **DB migration V8**.
- Added `BookingRepository.findByStripePaymentIntentId()` query method.

### Features — Vehicle Booking API

- **Added vehicle booking endpoints** to `VehicleV1Controller`:
  - `GET /{id}/availability?startDate=&endDate=` — check date availability (public).
  - `GET /{id}/bookings` — list active bookings for a vehicle (public calendar).
  - `POST /{id}/bookings` — create a vehicle booking (TENANT role required).
  - `GET /my-bookings` — list the authenticated tenant's vehicle bookings.
- Added `VehicleBookingCreateRequest` and `VehicleBookingResponse` DTOs.
- Added `VehicleMapper.toBookingResponse()` mapping.
- Added `VehicleAvailabilityService.getTenantBookings()`.
- Registered new public endpoints in `SecurityConfig`.

### Fixes — Frontend Error Handling

- **Error interceptor now extracts server-side messages** from the backend's
  `ErrorResponse` structure (`error.error.message`) instead of showing generic text.
- Added handling for HTTP 0 (network unreachable) and 409 (conflict) status codes.
- 401 errors are no longer double-handled — the error interceptor defers to the
  auth interceptor for token refresh/logout.

### Infra — CI/CD Pipeline

- **Upgraded CI to Java 21** — Backend job now uses `setup-java` with `java-version: '21'`
  to match the project's virtual threads requirement.
- **Added Docker service containers** — PostgreSQL 16, Redis 7, and RabbitMQ 3 run as
  GitHub Actions services so `mvn verify` can execute integration tests.
- Added Maven dependency caching and npm caching for faster builds.
- CI now triggers on both `main` and `master` branches.

### Infra — RabbitMQ Queues & Bindings

- **Added queue declarations** to `RabbitMqConfig`:
  - `homeflex.booking.events` bound to `Booking.#`
  - `homeflex.property.events` bound to `Property.#`
  - `homeflex.notification.events` bound to `*.#` (catch-all)
- The outbox relay now has actual queues to deliver events into.

### Features — Escrow Release Scheduler

- **Created `EscrowReleaseService`** — Scheduled job (hourly) that:
  - Finds property bookings in APPROVED status where `startDate <= today` and escrow
    has not been released, then creates a Stripe Transfer to the landlord's connected
    account and marks the booking COMPLETED.
  - Finds vehicle bookings in CONFIRMED status with the same criteria and releases
    escrow to the vehicle owner's connected account.

### Features — Admin Report Resolution

- **Admin report resolution now accepts notes** — `PATCH /admin/reports/{id}/resolve`
  accepts an optional `reason` body with resolution notes.
- Resolution records `resolvedBy` (admin user), `resolutionNotes`, and `resolvedAt`.
- **Reporter is notified** when their report is resolved via in-app notification and
  push notification.
- Added `resolutionNotes` column to `reported_listings` — **DB migration V9**.

### Fixes — Review Integrity

- **Reviews now require a completed booking** — `ReviewService.createReview()` verifies
  the reviewer has an APPROVED or COMPLETED booking for the property before allowing
  the review. Prevents fake reviews from users who never stayed.

### Fixes — WebSocket Service

- **Removed Bearer token dependency** — `WebSocketService` no longer calls `getToken()`
  (which was removed in the cookie auth migration). SockJS transports send httpOnly
  cookies automatically.
- **Added reconnection limit** — After 10 failed reconnect attempts, auto-reconnect
  stops to prevent tight retry loops on persistent failures.
- Added `onWebSocketClose` handler for connection status tracking.

### Fixes — Firebase Notification Gateway

- **Graceful degradation when Firebase is disabled** — `sendPush()` now checks
  `FirebaseApp.getApps().isEmpty()` before attempting to send. When Firebase credentials
  are not configured, push notifications are silently skipped instead of throwing.

### Fixes — StorageService

- **Rewrote `StorageService`** for robustness:
  - S3 client is now created once at startup (`@PostConstruct`) instead of per-request.
  - Throws `DomainException` instead of raw `RuntimeException` on upload failure.
  - Filenames are sanitized to prevent path traversal.
  - Respects `app.aws.enabled` flag — returns placeholder URLs when S3 is not configured.

### Fixes — Misc

- Fixed `GlobalExceptionHandler` class declaration formatting (extra whitespace).
- `EmailService.sendWithCircuitBreaker()` now re-throws the exception after logging
  so callers know the email failed (previously swallowed silently).
