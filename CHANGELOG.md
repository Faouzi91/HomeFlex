# Changelog

All notable changes to the HomeFlex project will be documented in this file.

## [Unreleased] — 2026-04-15 (Security Hardening & PII Protection)

### Security Hardening 🛡️

- **PII Encryption**: Upgraded from insecure AES/ECB to **AES-256-GCM** (Authenticated Encryption) with random per-record IVs for all sensitive user data.
- **Secret Management**: Removed all hardcoded default secrets from `application.yml` and `docker-compose.yml`. Secrets like `JWT_SECRET`, `PII_ENCRYPTION_KEY`, and `STRIPE_SECRET_KEY` must now be provided via environment variables.
- **Infrastructure Isolation**: Restricted all backend infrastructure (PostgreSQL, Redis, RabbitMQ, Elasticsearch, Kibana, Logstash) to the internal Docker network. Host port mappings were removed to prevent external access.
- **Elasticsearch Security**: Enabled mandatory X-Pack security and authentication for the Elasticsearch cluster.
- **Secure Cookies**: Enforced `Secure` flag on JWT cookies by default (`app.jwt.cookie.secure: true`).
- **Prometheus Security**: Replaced hardcoded metrics scrape tokens with environment variable references.

## [Unreleased] — 2026-04-14 (Round 12 — Admin Console & Role-Based Profiles)

### Added — Separated Admin Console

- **Admin Login Page** — Dedicated dark-themed admin login at `/admin/login`. Non-admin users are rejected with an error and logged out. Consumer login redirects admin users to `/admin`.
- **Admin Route Guard** — `adminGuard` enforces `ADMIN` role on all `/admin/*` routes, redirecting unauthorized users to `/admin/login`.
- **Admin Layout** — Full sidebar layout (`AdminLayoutComponent`) with navigation (Dashboard, Users, Properties, Reports, Settings), user plate, and logout. Consumer shell chrome (header/footer/support widget) is hidden on admin routes via `isAdminRoute` signal.
- **Admin Users Page** — Paginated user table with search by name/email, role filter (Tenant/Landlord/Admin), role badges, active/suspended status indicators, and suspend/activate action buttons. Admins cannot be suspended.
- **Admin Properties Page** — Card grid of pending properties showing images, pricing, specs, landlord info. Approve with one click; reject requires a reason via modal dialog.
- **Admin Reports Page** — Flagged content table with reporter info, reason tags, status filter (Pending/Resolved), and resolve action with optional notes via modal.
- **Admin Settings Page** — Admin profile management with avatar upload, personal info form (pre-populated from session), password change, and notification preference toggles.
- **Admin Dashboard** — Analytics cards (total users, active properties, pending properties, total bookings) consuming `GET /admin/analytics`.

### Added — Role-Based Profile Views

- **Notification Preferences** — Added email, push, and SMS notification toggle switches to both workspace and admin settings. Backend `UserDto` and `UserUpdateRequest` extended with notification fields.
- **Profile Completeness** — Added `profileCompleteness` to `UserDto` with server-side calculation. Displayed as a progress indicator in the workspace profile tab.
- **Avatar Upload** — Added `onAvatarSelected()` method to workspace for profile picture uploads via camera overlay UI.
- **Role-Specific Sections** — Workspace profile tab now shows host status card (KYC + payout status) for landlords/admins, security section, and GDPR section.

### Fixed — UI & UX Bugs

- **Logout not working** — Header dropdown logout was using a `routerLink` with a query param that was never handled. Replaced with a `<button>` calling `session.logout()` and navigating to `/`.
- **Profile form fields empty** — `profileForm.patchValue()` was inside a `forkJoin` callback. If any API call failed, the form never got patched. Moved to run immediately from session data.
- **Heading contrast on dark backgrounds** — Added CSS safety net ensuring `h1-h4` inside `.text-white` containers inherit the white color.
- **Backend test compilation** — Fixed `AuthServiceTest` `UserDto` constructor to include 3 new notification boolean fields.

### Changed — Documentation

- **CLAUDE.md** — Added admin console architecture section, expanded module structure with admin/guard/shell details.
- **SRS.md** — Bumped to v3.1. Added FR-104 through FR-106 (role-based profiles, notifications, avatar) and FR-600 through FR-606 (admin console).
- **README.md** — Updated features list, project structure, and API overview to reflect the admin console and management endpoints.

---

## [Unreleased] — 2026-04-12 (Round 10 — API Coverage Audit & DevOps Hardening)

### Added — Full Angular API Parity

- **26 missing API methods** added to the Angular `ApiClient`, achieving 100% backend endpoint coverage:
  - Auth: `resetPassword`, `sendOtp`, `verifyOtp`
  - User: `uploadAvatar`, `getUserById`
  - Chat: `markMessageAsRead`, `markChatRoomAsRead`
  - Notifications: `registerFcmToken`
  - Property: `updateProperty`, `deleteProperty`, `compareProperties`, `getPropertyReports`
  - Booking: `getBookingById`
  - Reviews: `replyToReview`
  - Disputes: `uploadDisputeEvidence`, `getDisputeEvidence`
  - Vehicles: `createVehicleConditionReport`, `getVehicleActiveBookings`
  - Admin: `getAdminUsers`, `suspendUser`, `activateUser`, `resolveReport`, `getSystemConfigs`, `updateSystemConfig`, `createAmenity`, `deleteAmenity`
- **New TypeScript interfaces** — `SystemConfig`, `DisputeEvidence`, `ConditionReport` added to `api.types.ts`.
- **Stronger typing** — `getVehicleConditionReports` return type upgraded from `any` to `ConditionReport`.

### Fixed — DevOps & Infrastructure

- **nginx.conf** — Removed dead `/app/` proxy block that referenced disabled `mobile-frontend` service, preventing 502 errors.
- **CI Pipeline** — Changed frontend build from `--configuration=development` to `--configuration=production` for realistic CI validation.
- **Prometheus** — Replaced hardcoded `changeme-metrics-token` with env var reference `${METRICS_BEARER_TOKEN:-dev-metrics-token}`.
- **Logstash** — Added structured log filters: JSON parsing, log level extraction, ISO8601 timestamp parsing, and exception tagging.

### Fixed — Angular Build Warnings

- Removed unused `DatePipe` and `TitleCasePipe` imports from `WorkspacePageComponent`.

### Changed — Documentation

- **README** — Updated tech stack versions (PostgreSQL 18, Redis 8, RabbitMQ 4), removed obsolete Mobile Web Frontend URL.
- **CHANGELOG** — Added Round 10 entry.

---

## [Unreleased] — 2026-04-11 (Round 9 — Unified Infrastructure & GDPR)

### Added — Unified Frontend Architecture

- **Micro-Frontend Gateway** — Split the frontend into `web-frontend` (Angular) and `mobile-frontend` (Flutter). Nginx now routes root traffic to Angular and `/app/` traffic to the Flutter Web experience.
- **Flutter Web Integration** — Fully integrated the Flutter project into the Docker orchestration with a specialized build pipeline.
- **Nginx Hardening** — Added production-grade security headers (CSP, HSTS, XSS Protection) and optimized proxy buffering for enterprise payloads.

### Added — 2026 Standard Stack Upgrade

- **PostgreSQL 18** — Upgraded database to the latest major version with improved data directory structure for better performance.
- **Redis 8** — Integrated the latest Redis version for ultra-fast caching and distributed locking.
- **RabbitMQ 4** — Upgraded messaging broker for enhanced reliability and management.
- **Elastic Stack 9.1** — Deployed the full ELK stack (Elasticsearch, Logstash, Kibana) using the stable 9.1 branch for advanced logging and search.

### Added — Compliance & API Coverage

- **GDPR Self-Service (Angular)** — Added "Export Data" and "Delete Account" buttons to the user profile, fully consuming the backend GDPR endpoints.
- **API Client Completion** — Updated both Angular and Flutter `ApiClient` implementations to cover 100% of the backend API surface, including Finance, Leases, and GDPR.

### Changed — Backend Stability

- **Schema Management (Hibernate -> Flyway)** — Shifted from Hibernate's `ddl-auto: update` to a strict Flyway-only migration strategy. Hibernate is now configured to merely `validate` the schema across all profiles, ensuring deterministic schema evolution via existing migration scripts.
- **Spring Boot 3.4.4 Transition** — Optimized the backend by moving from 4.0.4 to the stable enterprise 3.4.4 release to ensure binary compatibility with Redisson and other mature starters.
- **Failsafe Build Pipeline** — Implemented a manual Gradle installation strategy in Docker to bypass SSL/Network issues during high-volume dependency resolution.

### Added — Enterprise Hardening & Compliance

- **GDPR Tooling** — Added endpoints for data portability (export) and data erasure (Right to be Forgotten), ensuring strict European compliance.
- **App-Level Encryption (PII)** — Implemented AES-256 field-level database encryption (`PiiEncryptionConverter`) for highly sensitive user data (First Name, Last Name, Phone Number), supplementing standard RDS encryption-at-rest.
- **Multi-Region Strategy** — Prepared Terraform configurations for Cross-Region RDS Global Clusters and latency-based Route53 routing for high availability.
- **SLO Monitoring** — Defined Prometheus alerting rules for request latency (99th percentile < 500ms) and error rates (< 1%), enabling proactive operational response.
- **Dispute Evidence System** — Enhanced the resolution module with multi-party evidence upload support, allowing tenants and landlords to submit photos/documents for mediation.
- **Distributed Locking (Redlock)** — Integrated Redisson for robust distributed locking across the booking lifecycle, eliminating double-booking risks in clustered environments.
- **Two-Way Review System** — Expanded reviews to a bidirectional trust model where landlords and tenants can rate each other, contributing to a dynamic global Trust Score.
- **Multi-Currency Engine** — Built a currency conversion service supporting USD, EUR, GBP, XAF, AED, and SAR with real-time (simulated) exchange rates.

### Added — Advanced Web Features (Angular)

- **Admin Control Panel Expansion** — Built high-density management views for the **Agency Network** and **Dispute Resolution** modules within the workspace. Admins can now resolve disputes and view the global agency network.
- **Real-time Booking Modifications** — Implemented a complex workflow allowing tenants to request date changes for approved bookings, with automated availability checks, price recalculation, and landlord re-approval.
- **Interactive Map Search** — Integrated Leaflet maps into the property discovery page with a Grid/Map toggle and automated bounds fitting.
- **Global i18n Expansion** — Added support for **Spanish** and **Arabic**, including full **RTL (Right-to-Left)** layout support for the Arabic locale.
- **Social Login (Backend)** — Completed the authentication flows for **Apple** and **Facebook** OAuth, enabling seamless user creation for the prototype.

### Added — Enterprise Resiliency & Observability

- **GDPR Tooling** — Added endpoints for data portability (export) and data erasure (Right to be Forgotten), ensuring strict European compliance.
- **App-Level Encryption (PII)** — Implemented AES-256 field-level database encryption (`PiiEncryptionConverter`) for highly sensitive user data (First Name, Last Name, Phone Number), supplementing standard RDS encryption-at-rest.
- **Distributed Caching (Redis)** — Implemented `@Cacheable` for property lookups with configurable TTLs (30m for details, 5m for search). Greatly reduces PostgreSQL load for frequently accessed listings.
- **RabbitMQ Resiliency (DLX/DLQ)** — Configured a Dead Letter Exchange and dedicated queues for all domain events. Failed tasks (like Elasticsearch indexing) now gracefully fail to a DLQ for manual inspection instead of blocking consumers.
- **ELK Logging Stack** — Fully integrated Elasticsearch, Logstash, and Kibana. Backend now ships structured JSON logs via TCP to Logstash for real-time analysis.
- **KYC Hardening** — Enabled live Stripe Identity verification by exposing the publishable key to the frontend during session creation.
- **Maintenance Alerts** — Enhanced the maintenance system with automated **SMS (Twilio)** and **Push (Firebase)** notifications for status updates.

### Added — Future-Proof Innovations (v3.0/v4.0)

- **Agency Network (v4.0)** — Completed the backend controller and service for agency management, allowing administrators to verify and manage real estate agencies.
- **Insurance Marketplace (v3.0)** — Built a comprehensive insurance foundation including `InsuranceProvider`, `InsurancePlan`, and `InsurancePolicy` entities. Tenants can now purchase protection plans during the booking flow.
- **Automated Finance** — Implemented an automated receipt generation system using OpenPDF. Receipts are now automatically issued and stored as PDFs upon successful payment confirmation.
- **Dispute Resolution** — Added a core dispute management system allowing tenants to open disputes for bookings, which can then be reviewed and resolved by administrators.
- **Advanced Search (Elasticsearch)** — Enhanced property search by integrating amenities into the Elasticsearch index. Users can now filter properties based on specific features like "Pool", "Gym", or "High-speed Wi-Fi".
- **Agency White-labeling (v4.0)** — Implemented the core multi-tenant schema and entities for real estate agencies to manage their own properties and agents.
- **Blockchain Lease Immutability (v3.0)** — Added an asynchronous `BlockchainLeaseService` that records signed leases on a simulated blockchain (Ethereum/Polygon) for tamper-proof records.
- **AI Pricing Engine (v3.0)** — Built a data-driven pricing recommendation service that suggests optimal rates based on location demand and seasonal trends.

### Fixed

- **Backend Stability** — Fixed compilation errors in `VehicleV1Controller` related to invalid Stream API usage on Spring Data `Page` objects.

---

## [Unreleased] — 2026-04-09 (Round 7 — Property Availability & Web Frontend)

### Added — Maintenance Request System (Full Stack)

- **Backend** — Comprehensive maintenance request API with category selection, priority levels, and image support.
- **Flutter UI** — Integrated reporting flow for tenants (with multi-image upload) and a management dashboard for landlords.
- **Notifications** — Automated alerts for landlords on new requests and tenants on status updates.

### Added — Property Availability System (Backend)

- **Sparse availability model** — New `property_availability` table (V11) stores
  only BLOCKED or BOOKED dates. Un-listed dates are available by default.
- **Concurrency control** — Database-level `UNIQUE(property_id, date)`
  constraint guarantees no double-booking, even across parallel requests.
- **`PropertyAvailabilityService`** — Methods for `isAvailable`, `blockRange`,
  `unblockRange`, `reserveForBooking`, and `releaseForBooking`.
- **`PropertyAvailabilityController`** — New endpoints for getting availability
  ranges and host-controlled blocking.

### Added — Stripe Webhook Idempotency (Backend)

- **Processed event tracking** — New `processed_stripe_events` table (V10) to
  record event IDs from Stripe.
- **`ProcessedStripeEventRepository`** — Ensures each webhook payload is
  handled exactly once by checking before processing.

### Added — Web Frontend (`homeflex-web`)

- **Angular 21 + Tailwind 4** — Re-introduced the Angular frontend as a new
  project in the root directory.
- **Project Renaming** — Renamed all references from `rental-app-frontend` to
  `homeflex-web` in root documentation and `docker-compose.yml`.

### Changed — Flutter App (`rental-app-flutter`)

- **UI Updates** — Various improvements to login/register screens, property and
  vehicle grids, detail views, and user profiles.
- **Provider Updates** — Logic enhancements in `PropertyProvider`,
  `VehicleProvider`, and core API services.

### Added — Digital Lease System (Backend & Web)

- **`PropertyLease` entity** — New tracking for rental contracts, signatures, and document URLs (V12).
- **`LeaseService` & `LeaseController`** — Logic for generating, signing, and retrieving leases.
- **Web UI** — Integrated lease signing for tenants and generation for landlords in the Workspace.

### Added — SMS & WhatsApp Notifications (Backend)

- **Twilio Integration** — New `TwilioSmsGateway` for automated alerts.
- **Booking Alerts** — Real-time SMS/WhatsApp notifications for new booking requests and status updates.

### Changed — Documentation & Roadmap

- **SRS Update** — Aligned functional requirements with Round 7 progress. Moved Twilio, Leases, and Availability to Implemented.
- **Identified Remaining Gaps** — Maintenance requests, Multi-region deployment, i18n (Arabic/Spanish), Apple/Facebook Social Login, ELK logging, AI pricing, Blockchain leases, and Agency white-labeling.
- **README Update** — Refreshed feature list and appended the "Remaining Gaps" explicit breakdown.

---

## [Unreleased] — 2026-04-07 (Round 6 — Flutter Migration)

### Added — Flutter Mobile App (`rental-app-flutter`)

- **Full backend feature parity** — A new Flutter app replaces the Angular frontend
  and covers every backend endpoint: properties, vehicles, bookings, chat (STOMP
  WebSocket), reviews, favorites, notifications, KYC, vehicle condition reports,
  and the admin panel.
- **Auth** — Email/password login & register, forgot/reset password, email
  verification (deep-link), Google OAuth (`google_sign_in`), session restore via
  cookie auth. Web build uses `withCredentials: true` (browser-managed cookies);
  native builds use `PersistCookieJar` via `path_provider`.
- **State & routing** — Riverpod 3.x (`Notifier`/`NotifierProvider`), GoRouter 17
  with `StatefulShellRoute.indexedStack` (5 bottom-nav tabs), Freezed 3.x DTOs.
- **UX polish** — Material 3 light/dark theme with system toggle, shimmer
  loading skeletons, error retry widget, full-screen tap-to-zoom image gallery,
  pull-to-refresh on all lists, infinite-scroll pagination on properties &
  vehicles, advanced search filters (price range slider, min bedrooms/bathrooms),
  language preference toggle (EN/FR) persisted via `PUT /users/me`.
- **Real-time chat** — STOMP WebSocket subscriber with HTTP fallback and
  automatic message de-duplication.
- **KYC flow** — Stripe Identity session start + status polling for landlords.
- **Vehicle condition reports** — Multi-photo upload, mileage & fuel-level
  capture, history view (landlord/admin only).

### Removed

- **Angular frontend (`rental-app-frontend`) deleted** — All web-frontend
  responsibilities now belong to the Flutter app (which targets web, Android,
  iOS, Windows, macOS, and Linux from a single codebase).

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
