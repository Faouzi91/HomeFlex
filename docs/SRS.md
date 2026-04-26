# Software Requirements Specification (SRS)

## HomeFlex — Real Estate Rental Marketplace Platform

**Version:** 5.2
**Date:** April 26, 2026
**Classification:** Confidential
**Status:** Active — Aligned with implemented codebase

---

## Document Control

| Version | Date       | Author        | Description                                                                                                                     |
| ------- | ---------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| 1.0     | 2024-XX-XX | Original Team | Initial real estate platform                                                                                                    |
| 2.0     | 2026-03-24 | Architect     | Full enterprise-grade overhaul + vehicle rentals                                                                                |
| 2.1     | 2026-03-28 | Architect     | Align SRS with actual implementation state; separate implemented vs planned                                                     |
| 2.2     | 2026-03-29 | Architect     | Update status: cookie-only auth, Redis rate limiting, ES search, outbox relay, vehicle module completion                        |
| 2.3     | 2026-03-30 | Architect     | Implement: KYC (Stripe Identity), Stripe Connect escrow/payouts, Resilience4j, Prometheus/Grafana monitoring, NgRx Signal Store |
| 2.4     | 2026-04-09 | Architect     | Implement: Property Availability (V11), Digital Leases (V12), Twilio SMS/WhatsApp, Stripe Webhook Idempotency (V10), Angular 21 |
| 3.2     | 2026-04-17 | Security Eng. | Security audit: remove OAuth dummy-bypass, fix user-enumeration, constant-time token compare, XFF rate-limit fix, CSP headers, DataInitializer profile-gated, Swagger disabled in prod |
| 3.3     | 2026-04-17 | Security Eng. | CI hardening: --watch=false, ADMIN_PASSWORD/PII_ENCRYPTION_KEY in CI env; dead code removal; new tests (password-reset enumeration, OAuth stubs, admin guard); skills: security + folder-structure |
| 3.4     | 2026-04-17 | Architect     | Stripe payment confirmation (client secret → confirmCardPayment), reactive header unread-count badge, landlord received-bookings view, home page resilience fix, public /api/v1/config endpoint. |
| 3.5     | 2026-04-18 | Architect     | Unread persistence fix (countUnreadInRoom excludes sender), robust avatar upload (null-safe contentType + nginx client_max_body_size 50M), overview stats filtered to active bookings only. |
| 4.0     | 2026-04-19 | Security Eng. | Full RBAC migration: Role/Permission entities, V27/V28 Flyway migrations, 46 permissions, 4 roles, permission-based authorization with Permissions constants, HomeFlexPermissionEvaluator, OwnershipVerifier; Booking feature refactored to hasAuthority/hasPermission; three new workspace tabs (Finance, Disputes, Insurance); GET /disputes/mine endpoint; comprehensive API smoke-test script. |
| 4.1     | 2026-04-19 | Security Eng. | Centralized ownership logic: ResourcePermissionService extracts all ownership rules from HomeFlexPermissionEvaluator; BookingService stripped to pure business logic; BookingV1Controller security gap fixed (GET /{id} hasPermission); BookingRepository.findByIdWithParties avoids N+1 in evaluator; 16 ResourcePermissionServiceTest ownership rule tests. |
| 4.2     | 2026-04-19 | Architect     | Stripe Connect escrow workflow completed: MANUAL capture PaymentIntent, capture-on-approve, prorated early-checkout refund, Stripe Connect Express landlord onboarding (Hosting > Payments tab); DisputeModal standalone component replaces prompt(); BookingDetailPanel wires earlyCheckout API; api.client.ts ConnectOnboardingResponse type fix; payment-modal dead code removed; Prettier lint 100%. |
| 4.3     | 2026-04-23 | Architect     | Production-grade state machine booking workflow: `BookingStatus` expanded to 10 states; `BookingStateMachine` enforces transitions; `BookingAuditLog` tracks all changes; booking creation split into `/draft` and `/pay` endpoints with idempotency keys; `ResourcePermissionService` supports Vehicle ownership rules. |
| 4.4     | 2026-04-24 | Architect     | Finalized booking workflow parity for vehicles: `VehicleBookingStatus` aligned with `BookingStatus` (10 states); split-payment flow (`/draft` and `/pay`) implemented for vehicles; frontend dashboard filters and visual status mappings updated for all 10 lifecycle states. |
| 4.5     | 2026-04-23 | Architect     | Frontend quality pass: all workspace tabs migrated off deprecated `ApiClient` to domain API services (`DisputeApi`, `FinanceApi`, `PayoutApi`, `InsuranceApi`); `takeUntilDestroyed` applied to all component subscriptions; insurance tab now fetches both TENANT and LANDLORD plans via `forkJoin`; Stripe Connect banner gated on `stripeNotConnected` computed signal; maintenance tab property selector replaced with `<select>` from `WorkspaceStore.myProperties()`; social login buttons (Apple/Facebook) disabled with "Soon" badge pending OAuth implementation. |
| 5.2     | 2026-04-26 | Architect     | Hierarchical Property Model & Admin Availability System: V34 property model enhancements (status/listing-type/policy fields), V35 `room_types` + `room_type_images` tables, V36 `room_inventory` count-based table, V37 booking room-type FK + `numberOfRooms`. Admin owns reference tables, system rules and platform-wide settings. Properties support hierarchical Building → Unit Type structure (RoomType with `totalRooms` per type), real-time availability tracking via `RoomInventoryService.reserve/release` with date-keyed counts. Hotel-style and standalone occupancy unified under `OccupancyController`. Vehicle availability already tracked per-vehicle (each vehicle is its own bookable unit). Frontend: hosting tab now exposes room-types CRUD wizard + occupancy summary; `RoomTypeController`, `OccupancyController` REST endpoints; admin validation checklist for property submissions with structured rejection reasons.
| 5.1     | 2026-04-26 | Architect     | Sprint 2: booking modification frontend 🔴→🟢 (tenant date-change modal, landlord approve/reject, PENDING_MODIFICATION info card); auto review prompt 🔴→🟢 (NotificationService.sendReviewPromptNotification wired into completeActiveBookings scheduler); admin amenity CRUD 🔴→🟢 (GET/POST/PUT/DELETE /admin/amenities, admin page with table + modal, nav item added).
| 5.0     | 2026-04-25 | Architect     | Quick wins: price breakdown 🔴→🟢 (4-row breakdown widget with cleaning fee + 15% platform fee + total); category sub-ratings 🔴→🟡→🟢 (frontend now renders sub-ratings from existing backend fields); profile completeness bar 🔴→🟢 (color-coded progress bar in profile tab); read receipts 🔴→🟢 (single/double SVG check on sent messages in messages tab).
| 4.9     | 2026-04-26 | Architect     | Sprint 1 close-out: geocoding 🔴→🟢 (GeocodingService via Nominatim, wired into PropertyService.createProperty); email verification gate 🟡→🟢 (BookingService.executeCreateDraft enforces isVerified); image thumbnails 🔴→🟢 (StorageService.uploadImageWithThumbnail generates 400px thumb alongside 1200px full; PropertyImage.thumbnailUrl now populated); admin analytics dashboard 🔴→🟢 (KPI grid, CSS bar charts for type/city/status, top-viewed/favorited lists); trust score 🟡→🟢 (already fully implemented in ReviewService — SRS misclassification corrected); Redis double-booking lock 🔴→🟢 (RedissonClient already used in BookingService — SRS misclassification corrected). Rule added: SRS updated after every implementation session.
| 4.8     | 2026-04-25 | Architect     | Second audit pass — corrected remaining misclassifications found by manual code inspection: account lockout 🔴→🟢 (LoginAttemptService, Redis-backed, configurable); two-way reviews 🟡→🟢 (POST /reviews handles both types, GET /reviews/tenant/{userId}, POST /reviews/{id}/reply); email verification 🟡→🟢 (endpoint exists, gate not enforced); FR-700 AC-6 landlord reply 🔴→🟢. Updated planned list and FR tables accordingly.
| 4.7     | 2026-04-25 | Architect     | Comprehensive codebase audit: corrected 15+ misclassified SRS items (🔴→🟢: auto-reject, cancellation policies, ES geo-search, full-text search, Twilio SMS, escrow/refunds/receipts, FR-401 finance dashboard, FR-800 leases, FR-900 maintenance, AC-6 dispute resolution; 🔴→🟡: two-way reviews, notification preferences, trust score, blockchain lease stub; 🟡→🔴: account lockout); added new "Implemented features not in SRS" section (pricing rules, room types, booking audit log, state machine, agency, OTP). SRS now reflects actual codebase state at 4.7.
| 4.6     | 2026-04-25 | Architect     | Full UI/UX premium redesign pass: dark `bg-slate-900` editorial hero headers on properties and vehicles listing pages; premium filter sidebars with `rounded-xl` inputs and `.select-styled` dropdowns; insurance tab restyled with emerald/gold sectioned plan cards; disputes tab restyled with amber color scheme and SVG meta rows; finance tab rebuilt with onboarding hero panel, 4-step progress indicator, earnings dashboard tiles, and improved receipts section; raw enum display fixed across all templates (`.replaceAll('_', ' ')` sweep covering `vehicle-detail`, `property-detail`, `favorites-tab`, `hosting-tab`, `admin-properties`); MinIO image proxy via Nginx `/uploads/` → `minio:9000/rental-app-media/`; `StorageService` generates relative `/uploads/<key>` URLs; V38 Flyway migration rewrites existing absolute `http://` image URLs to relative form. |

---

## Table of Contents

...

### Implemented since v2.3 (Round 7)

- 🟢 **Property Availability System** — Sparse date model (`property_availability` table) for blocking/booking dates with database-level concurrency control.
- 🟢 **Digital Lease Management** — `PropertyLease` entity and service for generating, signing, and managing rental contracts via PDF URLs.
- 🟢 **SMS & WhatsApp Alerts** — `TwilioSmsGateway` integration for real-time booking lifecycle notifications.
- 🟢 **Stripe Webhook Idempotency** — `processed_stripe_events` tracking to ensure exactly-once processing of payment events.
- 🟢 **Modern Web Dashboard** — Re-introduced and updated `homeflex-web` using Angular 21, Tailwind CSS 4, and NgRx Signal Store.
- 🟢 **Responsive Workspace** — Unified host/tenant operations panel with KYC status, payout summaries, and availability calendars.

### Implemented since v3.2 (Security Hardening & CI)

- 🟢 **Full Security Audit (10 vulnerabilities)** — OAuth dummy-bypass removed, user-enumeration silenced, constant-time token comparison, X-Forwarded-For spoofing fixed, Swagger UI disabled in prod, `DataInitializer` gated to `!prod`, admin password fallback removed, `.env` untracked from git, hardcoded JWT secret removed from `docker-compose.yml`, Content-Security-Policy added to Nginx.
- 🟢 **CI Pipeline Fixed** — Angular `ng test` was hanging (missing `--watch=false`); `ADMIN_PASSWORD` and `PII_ENCRYPTION_KEY` added to CI env and `application-test.yml` so the backend can start in the test runner.
- 🟢 **New Unit Tests** — `AuthServiceTest`: password-reset user-enumeration prevention, `appleLogin`/`facebookLogin` unconditional throws. Angular: `admin.guard.spec.ts` (3 cases).
- 🟢 **Claude Code Skills** — `security/SKILL.md` (OWASP Top 10, secure auth/PII/rate-limit patterns) and `folder-structure/SKILL.md` (6 languages × multiple architectural styles) added to `.claude/skills/`.

### Implemented since v5.3 (Per-Unit Identity Model — Individual Room/Unit Tracking)

> **Spec reference:** _HomeFlex Admin & Availability System Specification_ — every bookable unit has its own identity (Building → Unit Type → Individual Unit). Bookings auto-assign a specific unit number; landlords can label, floor-tag and take individual units out of service.

- 🟢 **`property_units` Table (V39)** — Every `RoomType` now has N concrete `PropertyUnit` rows (one per physical room). Columns: `id`, `room_type_id` (FK), `unit_number` (unique per room type), `floor`, `status` (`AVAILABLE` / `OUT_OF_SERVICE` / `UNDER_MAINTENANCE`), `notes`, `created_at`, `updated_at`. The migration backfills anonymous units `1..totalRooms` for existing room types so legacy data remains valid.
- 🟢 **`bookings.unit_id` (nullable FK)** — A booking may now bind to a specific `PropertyUnit`. Older bookings without a unit fall back to aggregate count behavior; new single-room bookings are auto-assigned.
- 🟢 **Auto-Assignment at Reservation** — `BookingService.approveBooking` and `approveModification` call `PropertyUnitService.findFirstAvailable(roomTypeId, start, end)` after the count-based reservation succeeds. Returns the lowest-numbered `AVAILABLE` unit with no overlapping non-terminal booking. Cancellations & rejections release the unit (sets `unit_id = null` on inventory release).
- 🟢 **`PropertyUnitService.syncUnitCount(roomTypeId, requestedTotal)`** — Called from `RoomTypeService` on create/update; appends new anonymous units when `totalRooms` increases. Never deletes existing units automatically (would orphan bookings); landlords must delete units explicitly.
- 🟢 **REST API — `/properties/{id}/room-types/{rtId}/units`** — `GET` (list), `GET /available?startDate=&endDate=` (availability filter), `POST` (create), `PUT /{unitId}` (rename / reflag / set floor / status / notes), `DELETE /{unitId}`. All mutating routes are gated by `PROPERTY_UPDATE` permission and authorize that the room type belongs to the property and the caller is the landlord.
- 🟢 **`BookingDto.unitId` / `unitNumber`** — `BookingMapper` exposes the assigned unit so the frontend booking detail panel can show "Unit 204" alongside "Standard Room × 1".
- 🟢 **Frontend `PropertyApi` Unit Methods** — `getUnits`, `getAvailableUnits`, `createUnit`, `updateUnit`, `deleteUnit` plus a new `Unit {Pill}` rendered in the booking detail panel.

### Implemented since v5.2 (Hierarchical Property Model & Admin/Availability System)

> **Spec reference:** _HomeFlex Admin & Availability System Specification_ — admin owns global configuration; properties support hierarchical Building → Unit Type structure; availability is tracked at the lowest bookable level (room type / vehicle); inventory updates are accurate and real-time; same logic applies to vehicles (each vehicle is its own bookable unit).

- 🟢 **Hierarchical Property Model (Building → Unit Type → Booking)** — A `Property` now acts as a building/group. Each Property can declare one or more `RoomType` rows (e.g. "Standard Room", "Studio", "Apartment Suite") with their own `pricePerNight`, `bedType`, `numBeds`, `maxOccupancy`, `totalRooms`, `sizeSqm`, per-type images and amenity links. A booking targets either the standalone Property (for whole-home rentals) or a specific `RoomType` with `numberOfRooms` (for hotel-style group inventory). _Migrations: V34, V35._
- 🟢 **Real-time Inventory Tracking** — `room_inventory(room_type_id, date, rooms_booked)` (V36) is a sparse, date-keyed count. `RoomInventoryService.reserve(...)` and `.release(...)` atomically increment / decrement booked counts inside the booking transaction. `OccupancyService` returns `available = totalRooms − roomsBooked` per night; bookings exceeding available count are rejected, preventing overbooking. Standalone properties continue to use `property_availability` for date-blocking; both paths are unified behind `OccupancyController`.
- 🟢 **Booking Room-Type FK** — `bookings.room_type_id` (V37) + `bookings.number_of_rooms` columns. `Booking.roomType` references a specific unit type; auto-assignment of an individual room number can be layered on top later (currently the count-based hotel inventory model is the default).
- 🟢 **Admin Reference-Table Ownership** — Admin owns all global configuration: amenities (`/admin/amenities` CRUD), property types & listing types (enums), pricing rules (`pricing_rules` table, V33), platform-wide settings (commission rate config, cancellation policies). Non-admin endpoints can only consume these; only `ADMIN` can mutate them (`@PreAuthorize("hasRole('ADMIN')")`).
- 🟢 **Admin Property Validation Checklist** — On submission, properties enter `PENDING` status; `AdminController` exposes approve/reject endpoints with structured rejection reasons stored on `properties.rejection_reason`. Approved properties transition to `LIVE` and become indexable in Elasticsearch.
- 🟢 **Vehicle Per-Unit Availability** — Each `Vehicle` is its own bookable unit (no "vehicle types" abstraction). `VehicleBooking` against a specific `vehicle_id` plus `vehicle_availability` date model means no overlapping bookings for the same vehicle. Fleet operators model multiple identical vehicles as separate `Vehicle` rows.
- 🟢 **Frontend Hosting Wizard** — `hosting-tab.component.ts` now lets landlords create a Property and then add Room Types inline (form fields: name, bed type, num beds, max occupancy, price/night, total rooms, size, amenities). Occupancy summary card shows live `roomsBooked` vs. `totalRooms` per date range.

### Implemented since v5.1 (Sprint 2 — Booking Modifications, Auto Review Prompt, Admin Amenity CRUD)

- 🟢 **Booking Modification UI** — Tenants can request a date change from an APPROVED or ACTIVE booking via an inline modal (new check-in / check-out dates + optional reason). The booking transitions to `PENDING_MODIFICATION`. Landlords see an info card with the proposed dates and get "Approve Date Change" / "Reject" action buttons. All three flows wire to the existing `POST /bookings/{id}/modify`, `PATCH /bookings/{id}/modify/approve`, and `PATCH /bookings/{id}/modify/reject` backend endpoints.
- 🟢 **Auto Review Prompt** — `NotificationService.sendReviewPromptNotification()` added. `BookingService.completeActiveBookings()` (scheduled at noon daily) now calls it for every booking that auto-completes, sending an in-app + push notification to the tenant: "How was your stay? Share your experience with future guests."
- 🟢 **Admin Amenity Management** — New `AdminAmenitiesPageComponent` at `/admin/amenities` provides a full CRUD table: list (sorted by category/name), create/edit modal (EN name, FR name, SVG icon path, category), delete with confirmation. Backend: `GET /admin/amenities` and `PUT /admin/amenities/{id}` endpoints added alongside the existing POST/DELETE. "Amenities" nav item added to admin sidebar.

### Implemented since v5.0 (Quick Wins — Price Breakdown, Sub-Ratings, Profile Bar, Read Receipts)

- 🟢 **Price Breakdown Widget** — Property detail page replaces the single estimated price tile with a 4-row breakdown table: base price (nights × nightly rate), cleaning fee (shown only when non-zero), 15% platform service fee, and grand total. Powered by `cleaningFeeEstimate`, `platformFeeEstimate`, and `totalEstimate` computed signals in `property-detail.page.ts`.
- 🟢 **Category Sub-Ratings in Reviews Tab** — The `Review` TypeScript interface gains 6 optional sub-rating fields (`cleanlinessRating`, `accuracyRating`, `communicationRating`, `locationRating`, `checkinRating`, `valueRating`). `ReviewsTabComponent` renders a responsive sub-ratings grid below the star badge whenever any sub-rating is present, using `hasSubRatings()` / `subRatings()` helpers.
- 🟢 **Profile Completeness Progress Bar** — The workspace profile tab now shows a color-coded horizontal progress bar directly under the user's name. Color transitions: emerald (≥80%), amber (≥50%), rose (<50%). Reads `User.profileCompleteness` from the session store.
- 🟢 **Read Receipts in Messages Tab** — Sent messages now display a delivery/read status icon: single checkmark (brand-200) = delivered, double checkmark (emerald-300) = read. Driven by `Message.isRead` from the existing backend field.

### Implemented since v4.9 (Sprint 1 — Geocoding, Email Gate, Thumbnails, Analytics, Reviews Tab)

- 🟢 **Geocoding via Nominatim** — `GeocodingService` (new, `core/service/`) calls OpenStreetMap Nominatim API (no API key, respects usage policy via `User-Agent` header). `PropertyService.createProperty()` auto-populates `latitude`/`longitude` when the client omits them, unblocking Elasticsearch geo-distance sorting for all new listings.
- 🟢 **Email Verification Gate for Bookings** — `BookingService.executeCreateDraft()` now throws `DomainException("Please verify your email address before making a booking.")` when `tenant.isVerified` is false. Landlord listing gate already existed via `KycService.requireVerified()`. Google OAuth users bypass the gate (auto-verified on login at `AuthService` line 168).
- 🟢 **Image Thumbnails** — `StorageService.uploadImageWithThumbnail()` generates two uploads per image: full-size (1200px cap, existing imgscalr) and 400px thumbnail stored under a `thumbs/` sub-prefix. `PropertyImage.thumbnailUrl` is now populated on every property image upload (create and `addImages`). Fallback: if imgscalr fails, original is uploaded and `thumbnailUrl` is null.
- 🟢 **Admin Analytics Dashboard** — `dashboard.page.ts` / `dashboard.page.html` rebuilt from a 4-card stub into a full intelligence dashboard: 4-tile KPI grid (users with tenant/landlord breakdown, properties with live/pending, bookings with approved/pending, messages); three CSS bar charts (properties-by-type, top-6-cities, bookings-by-status with semantic colour coding); ranked Top Viewed and Top Favorited property lists. All data sourced from existing `GET /admin/analytics` endpoint.
- 🟢 **Workspace Reviews Tab** — `ReviewsTabComponent` with two sub-tabs: "Property Reviews" (landlords see guest reviews on their properties, inline reply/edit) and "Received Reviews" (tenant reviews about the current user). Star rating display, reviewer avatar initials, skeleton loading, empty states. Wired into workspace routes and sidebar nav.
- 🟢 **Trust Score** — SRS misclassification corrected: `ReviewService` already recalculates `User.trustScore` on every review create and delete. Landlord score = average property rating; tenant score = average tenant rating. Fully implemented since ≥v4.0.
- 🟢 **Redis Distributed Lock (double-booking)** — SRS misclassification corrected: `BookingService.createDraftBooking()` already acquires a `RedissonClient` `RLock` keyed by `property:{id}:booking` before checking availability. Fully implemented since ≥v4.3.

### Implemented since v4.6 (Premium UI/UX Overhaul & Image Proxy)

- 🟢 **Dark Editorial Hero Headers** — Properties listing (`properties.page.html`) and Vehicles listing (`vehicles.page.html`) pages rebuilt with `bg-slate-900` full-width hero sections featuring eyebrow labels (`.eyebrow--light`), white `font-extrabold` titles, `text-slate-400` subtitles, and glassmorphism stat tiles (`bg-white/5 border border-white/10 rounded-2xl`).
- 🟢 **Premium Filter Sidebars** — Both listing pages now have sticky filter sidebars with `rounded-xl bg-slate-50` inputs, `.select-wrap` / `.select-styled` dropdowns, `font-black` CTA buttons with `shadow-brand-100/50` glow, and improved results headers with animated view-toggle pills.
- 🟢 **Insurance Tab Redesign** — `insurance-tab.component.html` rebuilt with `bg-slate-50/50` background, emerald icon header, sectioned tenant (emerald-themed) and landlord (gold-themed) plan cards with coverage tiles, provider badge pills, and spinner-state purchase buttons.
- 🟢 **Disputes Tab Redesign** — `disputes-tab.component.html` rebuilt with amber icon header (`bg-amber-500`), amber icon boxes per dispute card, SVG-led meta row (calendar icon for opened date, checkmark icon for resolved date), and improved skeleton loading animation.
- 🟢 **Finance Tab Redesign** — `finance-tab.component.html` rebuilt with a dark `bg-slate-900` onboarding hero panel featuring radial decorative blurs, a 3-tile benefits grid (Fast Payouts / Secure Escrow / KYC Verified), a 4-step visual progress bar, and a gold "Connect Bank Account" CTA. When connected: 4-tile earnings dashboard (Total Earned in dark tile, Available in emerald, Pending in amber, Escrow in brand); receipts section with icon header and hover-elevated cards.
- 🟢 **Vehicle Detail Gallery Grid** — `vehicle-detail.page.html` rebuilt with a CSS grid gallery (`lg:grid-cols-[2fr_1fr_1fr] lg:grid-rows-2`, 480px height): main image spans 2 rows; 4 thumbnails auto-fill the remaining 2×2 cells.
- 🟢 **Raw Enum Display Sweep** — All remaining raw enum values across the frontend now go through `.replaceAll('_', ' ')` before display. Files fixed: `vehicle-detail.page.html` (`transmission`, `fuelType`), `property-detail.page.html` (`listingType`, `propertyType`), `favorites-tab.component.html` (`propertyType`, `listingType`), `hosting-tab.component.html` (detail panel `status`), `admin-properties.page.html` (`propertyType`). Zero raw enums remain in any template.
- 🟢 **MinIO Image Proxy** — Nginx configuration extended with `/uploads/` → `http://minio:9000/rental-app-media/` reverse proxy block. `StorageService.generateUrl()` now returns `/uploads/<key>` relative paths instead of full `http://minio:...` URLs (unreachable from browser). V38 Flyway migration rewrites all existing absolute MinIO URLs in `property_images` and `vehicle_images` to relative `/uploads/<key>` form, ensuring historical uploads are immediately accessible.

### Implemented since v4.5 (Frontend Production-Readiness Pass)

- 🟢 **Domain API Services Everywhere** — All workspace tabs (`disputes-tab`, `finance-tab`, `insurance-tab`) migrated from the deprecated `ApiClient` facade to their dedicated domain services (`DisputeApi`, `FinanceApi`, `PayoutApi`, `InsuranceApi`). `ApiClient` is now only used for legacy/uncategorized calls.
- 🟢 **Memory Leak Elimination** — `takeUntilDestroyed(destroyRef)` applied to every component subscription in all six workspace tabs. No component now requires manual `ngOnDestroy` unsubscription.
- 🟢 **Insurance Tab — Both Plan Types** — `InsuranceTabComponent` now fetches TENANT and LANDLORD insurance plans in parallel via `forkJoin`. Previously only TENANT plans were loaded, leaving the Landlord Insurance section permanently empty.
- 🟢 **Stripe Connect Banner Condition Fix** — The "Connect Stripe Account" banner in the Finance tab is now gated on `stripeNotConnected` (computed from `PayoutSummary.stripeAccountConnected`). Previously the banner was shown to all landlords/admins, even those who had already connected their account.
- 🟢 **Maintenance Property Selector** — The raw UUID text input in the maintenance work-order form is replaced with a `<select>` dropdown populated from `WorkspaceStore.myProperties()`. Landlords with no properties see the fallback text input.
- 🟢 **Social Login Disabled UI** — Apple and Facebook login buttons are disabled with a "Soon" badge. Previously the buttons called `socialLogin('apple-token')` / `socialLogin('facebook-token')` with dummy hardcoded tokens, which would silently fail against the real backend.

### Implemented since v4.4 (Unified Booking State Machine & Vehicle Parity)

- 🟢 **Unified Booking Lifecycle** — Both property and vehicle bookings now follow the same 10-state lifecycle (`DRAFT`, `PAYMENT_PENDING`, `PAYMENT_FAILED`, `PENDING_APPROVAL`, `APPROVED`, `REJECTED`, `CANCELLED`, `ACTIVE`, `COMPLETED`, `PENDING_MODIFICATION`), ensuring consistent reporting and UI behavior across the platform.
- 🟢 **Vehicle Split-Payment Workflow** — Vehicle reservations migrated from a monolithic creation endpoint to a production-grade split flow: `POST /bookings/draft` followed by `POST /bookings/{id}/pay`.
- 🟢 **Dashboard Status Stabilization** — All dashboard filters (Active, Pending, Upcoming) updated to use the new state machine statuses. UI visual mappings (colors, labels) expanded to support the full 10-state lifecycle.
- 🟢 **Stripe Vehicle Integration** — `VehicleDetailPage` updated to mount Stripe Elements for secure payment confirmation, mirroring the property booking experience.
- 🟢 **Vehicle Ownership Security** — `ResourcePermissionService` extended to evaluate ownership logic for `Vehicle` and `VehicleBooking` domain objects.

### Implemented since v4.2 (Stripe Escrow Workflow, Dispute Modal & Hosting Payments Tab)

- 🟢 **Stripe MANUAL-capture escrow** — `PaymentService.createBookingPaymentIntent()` now sets `capture_method: MANUAL` so funds are only authorized at booking creation (no charge). On landlord approval `capturePaymentIntent()` captures the hold using `pi.capture()`, previously incorrectly calling `pi.confirm()`.
- 🟢 **Prorated early-checkout refund** — New `BookingService.earlyCheckout()` method calculates unused nights (`totalNights - nightsUsed`) / `totalNights × totalPrice`, calls `PaymentService.refundPayment()` with that prorated amount, and sets booking status to `CANCELLED`. `PATCH /bookings/{id}/early-checkout` is the new controller endpoint, protected by `BOOKING_CANCEL` permission.
- 🟢 **Full refund on tenant cancel** — `BookingService.cancelBooking()` now branches: if `paymentConfirmedAt != null` (payment was captured), calls `refundPayment(null)` for a full Stripe refund; otherwise calls `cancelPaymentIntent()` to release the uncaptured hold.
- 🟢 **Stripe Connect Express onboarding — Hosting tab** — `HostingTabComponent` gains a "Payments" section tab with a Stripe Connect status card (connected / not-connected states), "Connect with Stripe" / "Update account" buttons, and a 2×2 payout summary grid (Available, Pending, In Escrow, Lifetime Earnings). `PayoutApi.onboardConnectAccount()` is called on click; the user is redirected to Stripe's hosted onboarding URL. `loadPayoutSummary()` is called lazily when the Payments tab is first activated.
- 🟢 **DisputeModal standalone component** — `dispute-modal/dispute-modal.component.ts` replaces the removed browser `prompt()` calls. Provides a reason dropdown (6 values), a 20–1000 char description textarea, character counter, submit/cancel actions, and a success state. Wired into `BookingDetailPanelComponent` via `showDisputeModal` signal and `@if` block.
- 🟢 **BookingDetailPanel earlyCheckout wiring** — `cancelBooking()` in `BookingDetailPanelComponent` now calls `bookingApi.earlyCheckout(id)` when `isEarlyCheckout()` is true (status APPROVED + phase ACTIVE), falling back to `bookingApi.cancel(id)` for pre-stay cancellations.
- 🟢 **`UserDto` Stripe fields** — `stripeConnected: Boolean` and `stripeAccountId: String` added to the backend response DTO. `UserMapper` populates `stripeConnected` via MapStruct expression `java(user.getStripeAccountId() != null)`. Frontend `User` interface extended with optional `stripeConnected` and `stripeAccountId` fields.
- 🟢 **Type fixes** — `ConnectOnboardingResponse` (`{ stripeAccountId, onboardingUrl }`) replaces ad-hoc `{ url }` type in `ApiClient.onboardConnectAccount()`, `finance-tab`, and `profile-tab`. Unused `payment-modal` component deleted. Prettier lint passes on all 20+ modified files.

### Implemented since v4.1 (Centralized Permission + Ownership via ResourcePermissionService)

- 🟢 **`ResourcePermissionService`** — New service in `core/security/` that is the single source of truth for all ownership rules. Provides id-based lookup (via `BookingRepository.findByIdWithParties` JOIN FETCH) and object-based lookup (for already-loaded entities). Extensible to new domain types without modifying the evaluator.
- 🟢 **`HomeFlexPermissionEvaluator` refactored** — Now a thin authentication-contract handler: null-guard, authority check, admin bypass, userId extraction, then delegation to `ResourcePermissionService`. No domain-specific ownership logic remains in the evaluator.
- 🟢 **`BookingRepository.findByIdWithParties`** — Optimized JPQL query that eagerly fetches `tenant` and `property.landlord` in a single JOIN FETCH, preventing lazy-load N+1 queries when ownership is checked inside `@PreAuthorize` expressions.
- 🟢 **`BookingService` pure business logic** — All ownership guards removed. Method signatures simplified (no `landlordId`/`tenantId` params where they were only used for access control). Methods grouped into Create / Read / Landlord actions / Tenant actions / Webhook handlers / Scheduled.
- 🟢 **Security gap fixed** — `GET /api/v1/bookings/{id}` previously used `hasAuthority(BOOKING_READ)` (no ownership enforcement). Changed to `hasPermission(#id, 'Booking', 'BOOKING_READ')` so any user with the permission cannot read any arbitrary booking.
- 🟢 **`ResourcePermissionServiceTest`** — 16 unit tests covering all ownership rules: BOOKING_APPROVE (landlord-only), BOOKING_CANCEL (tenant-only), BOOKING_READ (either party), BOOKING_UPDATE (landlord-only), property ownership, id-based with entity fetch (found, not-found), unknown type, unknown object.

### Implemented since v4.0 (RBAC, Permission-Based Authorization & Frontend Workspace Tabs)

- 🟢 **Full RBAC System** — `Role` and `Permission` JPA entities with a `role_permissions` join table. 46 granular permissions across 11 domains (USER, PROPERTY, VEHICLE, BOOKING, LEASE, DISPUTE, REVIEW, PAYMENT, INSURANCE, KYC, ADMIN) assigned to 4 roles: `ROLE_TENANT`, `ROLE_LANDLORD`, `ROLE_ADMIN`, `ROLE_MONITORING`. Schema via V27/V28 Flyway migrations; V28 backfills `user_roles` from the legacy `users.role` column.
- 🟢 **Permission Constants Class** (`Permissions.java`) — Compile-time `public static final String` constants for every permission, eliminating magic strings from `@PreAuthorize` annotations.
- 🟢 **Custom `PermissionEvaluator`** (`HomeFlexPermissionEvaluator`) — Enables `hasPermission(#id, 'Booking', 'BOOKING_APPROVE')` SpEL expressions that check both permission authority and resource ownership in a single annotation. Admin role bypasses ownership checks.
- 🟢 **`OwnershipVerifier`** — Stateless Spring component injected into services. Enforces ownership with `AccessDeniedException` (→ 403) as a second line of defense below the controller-layer `@PreAuthorize`.
- 🟢 **Booking Feature Refactored** — `BookingV1Controller` migrated from `hasAnyRole(...)` to `hasAuthority(T(...).BOOKING_CREATE)` and `hasPermission(#id, 'Booking', 'BOOKING_APPROVE')`. `BookingService` no longer checks `UserRole` enum manually; all 7 ownership-guard blocks replaced with `OwnershipVerifier` calls.
- 🟢 **Backward-Compatible Role Migration** — `User.role` (enum) retained and `@Deprecated`. `User.roles` (`Set<Role>`) added. `JwtAuthenticationFilter.buildAuthorities()` emits role authorities + permission authorities, falling back to enum if no RBAC rows exist. `AuthService`, `DataInitializer`, and Google OAuth path all assign RBAC roles on new user creation.
- 🟢 **JWT Carries Roles List** — `JwtTokenProvider.generateToken()` now embeds a `roles` list claim alongside the backward-compat single `role` string claim. `UserDto` exposes `roles: List<String>` and `permissions: List<String>` for frontend role-aware rendering.
- 🟢 **`GET /disputes/mine` Endpoint** — New controller method returning the authenticated user's own disputes (no `ADMIN` role required). `SecurityConfig` updated with specific `authenticated()` matchers for `/disputes/mine` and `/disputes/*/evidence` before the admin catch-all.
- 🟢 **Finance / Receipts Workspace Tab** — Angular standalone component calling `api.getMyReceipts()`. Shows receipt number, amount+currency, PAID/PENDING status badge, issue date, and download link.
- 🟢 **Disputes Workspace Tab** — Angular standalone component calling `api.getMyDisputes()`. Shows reason, description, status badge (OPEN/UNDER_REVIEW/RESOLVED/CLOSED), and timestamps.
- 🟢 **Insurance Workspace Tab** — Angular standalone component using `InsuranceApi`. Fetches both TENANT and LANDLORD plans in parallel via `forkJoin`. Shows plan cards grouped by type with daily premium, max coverage, and "Select Plan" action.
- 🟢 **Comprehensive API Smoke-Test Script** (`scripts/test-all-apis.sh`) — Bash script covering 94 assertions across all 26 backend controllers and all frontend SPA routes. Starts Docker Compose, waits for health, seeds multi-role sessions, hits every endpoint, and reports colored PASS/FAIL summary.

### Implemented since v3.5 (Unread Persistence, Avatar Upload & Overview Stats)

- 🟢 **Chat unread count persists after refresh** — `MessageRepository.countUnreadInRoom` now excludes the viewing user's own outgoing messages (`m.sender.id <> :userId`). `ChatService` callsites pass the current user's ID. After `markRoomAsRead`, a page refresh now correctly reports zero unread for that room.
- 🟢 **Avatar upload robustness** — `UserService.updateAvatar` adds null/empty-file and null-safe `contentType` guards. `homeflex-web/nginx.conf` now sets `client_max_body_size 50M` so multipart uploads are not clipped by nginx's 1 MB default (which previously surfaced as an opaque 400 before Spring saw the request).
- 🟢 **Workspace overview stats reflect active bookings only** — The Property Stays and Vehicle Rentals tiles now use `activePropertyBookings` / `activeVehicleBookings` computed signals filtered to `APPROVED | ACTIVE | PENDING_APPROVAL | PAYMENT_PENDING`, so cancelled/rejected entries no longer inflate the counts.

### Implemented since v3.4 (Stripe Payment Flow, Reactive UI & UX Fixes)

- 🟢 **Stripe Payment Confirmation** — `BookingService` now captures `PaymentIntent.getClientSecret()` and returns it in `BookingDto.stripeClientSecret` (transient field, never persisted). The Angular property-detail page loads Stripe.js using a publishable key fetched from the new public `/api/v1/config` endpoint, then calls `stripe.confirmCardPayment` with the `pm_card_visa` test payment method after booking creation.
- 🟢 **Public `/api/v1/config` Endpoint** — `AppConfigController` exposes the Stripe publishable key (and future public config) to the frontend without auth, avoiding build-time key bundling.
- 🟢 **Reactive Header Unread Badge** — The header bell icon now uses `computed(() => workspaceStore.unreadNotificationCount() + workspaceStore.unreadMessageCount())` — live NgRx Signal reaction, no polling or manual refresh needed.
- 🟢 **Message Unread Count on Open** — `messages-tab` calls `chatApi.markRoomAsRead(roomId)` after loading a room and decrements `WorkspaceStore.unreadMessageCount` so the badge reflects the actual unseen count immediately.
- 🟢 **Landlord Received-Bookings View** — The workspace bookings tab shows a "Received" sub-tab for landlords listing all incoming bookings per property (loaded via `WorkspaceStore.myProperties()` + `bookingApi.getByProperty()`) with approve / reject actions and a pending-count badge.
- 🟢 **Home Page Resilience** — `forkJoin` on the home page now wraps each API source with its own `catchError(() => of(fallback))` so a failing stats or cities call no longer silently cancels property and vehicle loads.
- 🟢 **docker-compose Stripe Env Fix** — `STRIPE_API_KEY` renamed to `STRIPE_SECRET_KEY` (matching `application.yml`); `STRIPE_PUBLISHABLE_KEY` and `STRIPE_WEBHOOK_SECRET` added.

1. [Introduction](#1-introduction)
2. [System Overview & Vision](#2-system-overview--vision)
3. [Technology Stack & Justification](#3-technology-stack--justification)
4. [System Architecture](#4-system-architecture)
5. [Functional Requirements](#5-functional-requirements)
6. [Non-Functional Requirements](#6-non-functional-requirements)
7. [Data Model & Database Design](#7-data-model--database-design)
8. [API Design](#8-api-design)
9. [Security Architecture](#9-security-architecture)
10. [Infrastructure & Deployment](#10-infrastructure--deployment)
11. [Scalability & Performance Strategy](#11-scalability--performance-strategy)
12. [Monitoring & Observability](#12-monitoring--observability)
13. [Internationalization & Multi-Region](#13-internationalization--multi-region)
14. [Third-Party Integrations](#14-third-party-integrations)
15. [Mobile Strategy](#15-mobile-strategy)
16. [Testing Strategy](#16-testing-strategy)
17. [Release & Rollout Strategy](#17-release--rollout-strategy)
18. [Risk Analysis](#18-risk-analysis)
19. [Appendices](#19-appendices)

---

# 1. Introduction

## 1.1 Purpose

This Software Requirements Specification defines the complete technical and functional requirements for **HomeFlex**, a real estate rental marketplace platform. HomeFlex connects landlords with tenants through a secure platform supporting property listings, bookings, real-time chat, and payments.

> **Note on scope:** This document was originally written as a forward-looking enterprise vision (v2.0). Version 2.1 annotates each section with implementation status to clearly separate **what is built** from **what is planned**. Sections marked with 🟢 are implemented, 🟡 are partially implemented, and 🔴 are planned/not yet built.

This document serves as the authoritative source of truth for all development, QA, DevOps, and product decisions.

## 1.2 Scope

HomeFlex is a **real estate rental marketplace** currently supporting property rentals (apartments, houses, villas, studios, rooms). The architecture can be extended to additional rental verticals in the future.

### Implemented (v2.1)

- 🟢 Real estate rental listings with filters (city, price, type, bedrooms, bathrooms, amenities) and pagination
- 🟢 Role-Based Access Control (RBAC): TENANT, LANDLORD, ADMIN, MONITORING — with 46 granular permissions, custom `PermissionEvaluator`, and `OwnershipVerifier` for resource-level authorization
- 🟢 Booking management with approve / reject / cancel workflow
- 🟢 Real-time chat (WebSocket + STOMP, in-memory broker)
- 🟢 Favorites and reviews for properties
- 🟢 Admin dashboard with property moderation, user management, and reports
- 🟢 Push notifications via Firebase Cloud Messaging
- 🟢 Stripe payment integration (PaymentService)
- 🟢 i18n support (English, French) via ngx-translate
- 🟢 Dark / light theme toggle
- 🟢 Google OAuth social login
- 🟢 Email notifications via Gmail SMTP
- 🟢 Mobile-ready via Capacitor 8
- 🟢 Docker Compose deployment (6 services)
- 🟢 GitHub Actions CI pipeline

### Implemented since v2.1

- 🟢 Transactional outbox relay (OutboxRelayService polls with FOR UPDATE SKIP LOCKED, publishes to RabbitMQ, marks processed on ACK)
- 🟢 Redis rate limiting (Lua atomic INCR+EXPIRE, 100 req/min auth, 20 req/min public, 429 responses)
- 🟢 Elasticsearch property search (fuzzy matching, faceted filtering by type/city, geo-distance sorting)
- 🟢 RabbitMQ async event processing (PropertyIndexConsumer indexes properties to ES via outbox events)
- 🟢 httpOnly cookie token storage (ACCESS_TOKEN + REFRESH_TOKEN as Secure/SameSite=Strict cookies)
- 🟢 CSRF protection (CookieCsrfTokenRepository + SpaCsrfTokenRequestHandler for Angular 21)
- 🟢 Vehicle rentals vertical (full CRUD, image uploads, soft-delete, condition reports, availability/double-booking prevention)

### Implemented since v2.2

- 🟢 KYC verification via Stripe Identity (KycVerification entity, webhook-driven status updates, landlord publishing guard)
- 🟢 Stripe Connect with Destination Charges and Escrow (Express accounts, separate charges and transfers, 15% platform commission, hourly escrow release via EscrowService)
- 🟢 Payout management (GET /api/v1/payouts/summary, POST /api/v1/payouts/connect/onboard)
- 🟢 Resilience4j circuit breakers on EmailService and FirebaseNotificationGateway (trip after 5 consecutive failures)
- 🟢 Resilience4j retry with exponential backoff on Stripe API calls (3 attempts, 500ms base)
- 🟢 Prometheus metrics export (Micrometer registry, /actuator/prometheus secured by bearer token + ROLE_MONITORING)
- 🟢 Grafana monitoring dashboard (JVM heap, GC, threads, HikariCP, HTTP request rate/latency, booking/payment counters)
- 🟢 Custom Micrometer metrics (homeflex.bookings.created, homeflex.bookings.payments with outcome tag)
- 🟢 NgRx Signal Store for frontend state management (PropertyStore with withEntities + rxMethod, AuthStore)
- 🟢 Angular @for/@if control flow migration (zone-less rendering, no *ngFor/*ngIf)

### Partially Implemented (v2.2)

- 🟡 AWS S3 storage (StorageService exists with dev fallback, not fully wired in production)
- 🟡 Redis caching (Spring `@Cacheable` on `getPropertyById`; `getAllProperties` / search results uncached); 🟢 Redlock distributed locking — `RedissonClient` already used in `BookingService.createDraftBooking()` to prevent double-booking races (SRS misclassification corrected)

### Implemented since v2.4 (previously "Planned")

- 🟢 **SMS & WhatsApp notifications (Twilio)** — `TwilioSmsGateway` integrated for booking lifecycle alerts and OTP.
- 🟢 **Document management (leases)** — `PropertyLease` entity, full lease lifecycle (generation, signing, tracking), and PDF URLs.
- 🟢 **Maintenance request system** — Work-order creation, status tracking (`OPEN → IN_PROGRESS → RESOLVED`), and landlord assignment. Exposed in the Workspace Maintenance tab with a property selector dropdown.
- 🟢 **ELK logging stack** — Elasticsearch + Logstash + Kibana deployed as Docker services. Backend ships JSON logs to Logstash on port 50000.
- 🟢 **Insurance marketplace** — `InsurancePlan` entity with TENANT and LANDLORD plan types. Full CRUD API and Insurance workspace tab showing both plan categories (implemented since v4.0).

### Partially Implemented (v2.4+)

- 🟢 **Email verification gate** — Full flow implemented: `GET /api/v1/auth/verify?token=...`, `AuthService.verifyEmail()` sets `user.isVerified = true`, email sent on registration. Gate enforced: `BookingService.executeCreateDraft()` now throws `DomainException` if `tenant.isVerified` is false. Landlord listing gate already enforced via `KycService.requireVerified()`. Google OAuth users are auto-verified on login.
- 🟡 **Notification preferences** — `User` entity has `emailNotificationsEnabled`, `pushNotificationsEnabled`, `smsNotificationsEnabled` boolean flags. No granular per-event-type per-channel `NotificationPreference` entity yet.
- 🟡 **Blockchain lease contracts** — `BlockchainLeaseService` exists and is called from `LeaseService`, but is a no-op stub. Not a real blockchain integration.
- 🟢 **Trust Score** — `User.trustScore` (default 5.0, V19 migration). `ReviewService` recalculates and persists the trust score on every review create and delete: landlord score = average property rating across all their properties; tenant score = average tenant rating. SRS was misclassified as 🟡 — fully implemented.

### Implemented (confirmed in second audit pass — previously misclassified)

- 🟢 **Account lockout** — `LoginAttemptService` uses Redis to track failed attempts per email; blocks after configurable max (default 5) for configurable duration (default 30 min); wired into `AuthService.login()`.
- 🟢 **Two-way reviews** — `POST /api/v1/reviews` handles both `ReviewType.PROPERTY` and `ReviewType.TENANT` (auto-detected by request body). `GET /reviews/tenant/{userId}` returns landlord-written tenant reviews. `POST /reviews/{id}/reply` lets landlords post public responses. `DELETE /reviews/{id}` with ownership check.

### Planned (not yet built)

- 🔴 Multi-region deployment (AWS ECS Fargate, Route53 latency routing)
- 🔴 Arabic and Spanish i18n
- 🔴 Apple / Facebook social login — backend stub methods throw exceptions; UI buttons show "Soon" badge
- 🔴 AI-powered price recommendations
- 🔴 Recurring monthly rent collection (Stripe Billing subscriptions)
- 🟢 Image auto-resizing — `StorageService.uploadImageWithThumbnail()`: 1200px full-size + 400px thumbnail; `PropertyImage.thumbnailUrl` populated (implemented v4.9)
- 🟢 Geocoding API integration — `GeocodingService` calls Nominatim (OpenStreetMap, no API key) on property creation when client omits lat/lng; coordinates stored and indexed in Elasticsearch for geo-distance sorting
- 🟢 `isVerified` enforcement gate — now enforced in `BookingService.executeCreateDraft()`; landlord listing gate via `KycService.requireVerified()`

## 1.3 Decision Baseline (Approved)

This SRS is based on explicit product and architecture decisions. The "Status" column indicates what is implemented.

| Decision Area      | Approved Direction                        | Rationale                         | Status                                          |
| ------------------ | ----------------------------------------- | --------------------------------- | ----------------------------------------------- |
| Product scope      | Real estate rental marketplace            | Focus on core vertical first      | 🟢 Implemented                                  |
| Payments           | Stripe Connect (escrow + destination)     | Native marketplace support        | 🟢 Implemented (PaymentService + EscrowService) |
| Deployment model   | Docker Compose (local/single-server)      | Simplicity for current scale      | 🟢 Implemented                                  |
| Cloud provider     | AWS (planned)                             | Best fit for managed services     | 🔴 Planned — currently Docker Compose           |
| Trust & safety     | Admin moderation                          | Fraud reduction via manual review | 🟢 Implemented                                  |
| KYC verification   | Mandatory owner/landlord KYC              | Fraud reduction and compliance    | 🟢 Implemented (Stripe Identity)                |
| Platform verticals | Real estate (full) + vehicles (full CRUD) | Complete vehicle feature set      | 🟢 Both verticals implemented                   |

## 1.4 Definitions & Acronyms

| Term         | Definition                                                                     |
| ------------ | ------------------------------------------------------------------------------ |
| **Landlord** | A user who lists properties for rent (role: `LANDLORD`)                        |
| **Tenant**   | A user who searches for and books rental properties (role: `TENANT`)           |
| **Admin**    | Platform administrator who moderates content and manages users (role: `ADMIN`) |
| **Property** | A real estate asset posted for rent by a landlord                              |
| **Booking**  | A reservation of a property by a tenant                                        |
| **KYC**      | Know Your Customer — identity verification via Stripe Identity (implemented)   |
| **STOMP**    | Simple Text Oriented Messaging Protocol — used for WebSocket chat              |
| **SLA**      | Service Level Agreement                                                        |
| **CDN**      | Content Delivery Network                                                       |
| **RBAC**     | Role-Based Access Control                                                      |

## 1.5 References

- IEEE 830-1998 — Recommended Practice for Software Requirements Specifications
- OWASP Top 10 (2025) — Web Application Security Risks
- PCI DSS v4.0 — Payment Card Industry Data Security Standard
- GDPR — General Data Protection Regulation
- WCAG 2.2 — Web Content Accessibility Guidelines
- 12-Factor App Methodology

---

# 2. System Overview & Vision

## 2.1 Product Vision

HomeFlex aims to become the **unified rental marketplace** — a single platform where users can rent anything from a studio apartment to a pickup truck. Unlike niche platforms (Airbnb for stays, Turo for cars), HomeFlex aggregates multiple rental verticals into one experience with a shared identity, payment system, trust network, and communication layer.

## 2.2 Business Goals

| #    | Goal                 | Metric                | Target                   |
| ---- | -------------------- | --------------------- | ------------------------ |
| BG-1 | User acquisition     | Monthly active users  | 100K within 12 months    |
| BG-2 | Listing volume       | Total active listings | 50K within 12 months     |
| BG-3 | Transaction volume   | Monthly bookings      | 10K within 12 months     |
| BG-4 | Revenue              | Monthly GMV           | $2M within 12 months     |
| BG-5 | Global reach         | Supported regions     | 3 regions (NA, EU, MENA) |
| BG-6 | Platform reliability | Uptime SLA            | 99.9%                    |

## 2.3 User Personas

### Persona 1: Tenant

- **Who:** Individual looking for a property to rent
- **Goals:** Find affordable listings quickly; book securely; communicate with landlords
- **Pain points:** Scam listings, hidden fees, unresponsive landlords, complex booking processes

### Persona 2: Landlord

- **Who:** Individual or business that owns properties for rent
- **Goals:** Maximize occupancy; receive payments reliably; manage bookings efficiently
- **Pain points:** No-show tenants, payment disputes, property damage, manual booking management

### Persona 3: Platform Administrator

- **Who:** HomeFlex operations team member
- **Goals:** Moderate content, manage users, review reports, monitor platform health
- **Pain points:** Fraudulent listings, compliance risks, scaling support operations

## 2.4 Core User Flows

### Flow 1: Tenant Books a Property 🟢

```
Search → Filter → View Detail → Book → Pay via Stripe
→ Landlord Approves/Rejects → Tenant can Cancel → Review
```

### Flow 2: Landlord Onboarding 🟢

```
Register (email/password or Google OAuth) → Login
→ Create Property (multipart: details + images) → Publish → Go Live
```

### Flow 3: Payment Flow 🟢

```
Tenant Creates Booking → Stripe Payment → Landlord Approves → Booking Active
```

### Flow 4: Real-Time Chat 🟢

```
Tenant or Landlord opens Chat Room → WebSocket STOMP connection
→ Send/receive messages in real-time → Typing indicators
```

---

# 3. Technology Stack & Justification

## 3.1 Backend Stack

### 3.1.1 Language & Runtime: Java 21 (LTS)

**Choice:** Java 21 Long-Term Support

**Why:**

- **LTS until September 2028** — guarantees security patches and stability for 2+ years without forced upgrades. Non-LTS versions (22, 23, 24) lose support in 6 months.
- **Virtual threads (Project Loom)** — Java 21 introduces lightweight virtual threads that allow handling thousands of concurrent connections without thread pool exhaustion. Critical for WebSocket connections and high-throughput booking APIs.
- **Pattern matching & record patterns** — Modern language features that reduce boilerplate in DTOs and domain model transformations.
- **Mature ecosystem** — The largest ecosystem of enterprise libraries, monitoring tools, and developer talent pool. Finding Java developers is significantly easier than Kotlin, Scala, or Go for enterprise projects.
- **GraalVM compatibility** — Java 21 works seamlessly with GraalVM native image for optional ahead-of-time compilation, reducing cold start times for serverless deployments.

**Alternatives considered:**

- **Kotlin** — More concise but smaller talent pool, and Spring Boot's Java support is first-class.
- **Node.js (TypeScript)** — Single-threaded event loop struggles with CPU-intensive operations (image processing, report generation). Not ideal for enterprise-grade backend with complex business logic.
- **Go** — Excellent performance but lacks the ORM maturity (Hibernate), security frameworks (Spring Security), and enterprise integration libraries that Spring provides.

---

### 3.1.2 Framework: Spring Boot 4.0.4

**Choice:** Spring Boot 4 with Spring Framework 7

**Why:**

- **Industry standard for Java enterprise applications** — Used by Netflix, Amazon, Alibaba, and most Fortune 500 companies. Battle-tested at massive scale.
- **Spring Security** — The most comprehensive security framework in any language. Provides JWT authentication, OAuth2, CSRF protection, method-level authorization, and CORS configuration out of the box. Building this from scratch in other frameworks would take months.
- **Spring Data JPA** — Eliminates 80% of boilerplate data access code. Repository interfaces auto-generate queries, and Specifications enable type-safe dynamic queries for complex search features.
- **Spring WebSocket** — First-class STOMP protocol support with SockJS fallback, message broker abstraction (swap in-memory for RabbitMQ without code changes), and JWT authentication on WebSocket connections.
- **Spring Boot Actuator** — Production-ready health checks, metrics export (Prometheus), and environment inspection. Critical for Kubernetes readiness/liveness probes.
- **Auto-configuration** — Convention over configuration dramatically reduces setup time. Adding Redis caching, RabbitMQ messaging, or Elasticsearch is a dependency + annotation away.
- **Spring Boot 4 specifically** — Requires Java 17+ (we use 21), supports Gradle 9, and introduces improved observability APIs with Micrometer 2.0.

**Alternatives considered:**

- **Quarkus** — Faster startup (important for serverless, less so for long-running services). Smaller community and fewer production case studies at enterprise scale.
- **Micronaut** — Similar to Quarkus. Compile-time DI is elegant but Spring's runtime DI has more flexibility for plugin architectures.
- **Django (Python)** — Excellent for rapid prototyping but Python's GIL limits true concurrency. Not suitable for real-time WebSocket-heavy applications.

---

### 3.1.3 Build Tool: Gradle (Groovy DSL) 🟢

**Choice:** Gradle with Groovy DSL

**Why:**

- **Incremental builds** — Gradle only recompiles changed modules. In a multi-module project (which HomeFlex should become), this saves 60-80% of build time compared to Maven.
- **Build cache** — Remote build cache allows sharing compiled outputs across CI machines and developer laptops. A clean build on CI that would take 5 minutes becomes 30 seconds with cache hits.
- **Dependency locking** — `gradle.lockfile` ensures reproducible builds across environments. Critical for compliance and debugging production issues.
- **Parallel execution** — Gradle 9 runs independent tasks in parallel by default, utilizing all CPU cores.
- **Convention plugins** — Share build configuration across modules without copy-pasting. Define Java version, testing framework, code quality tools once.
- **Spring Boot 4 compatibility** — Spring Boot 4 officially supports Gradle 9.

**Alternatives considered:**

- **Maven** — XML-based configuration is verbose and slow. No build cache, no parallel execution by default. Still dominant in legacy enterprises but losing ground.

---

### 3.1.4 Primary Database: PostgreSQL 18

**Choice:** PostgreSQL 18 as the primary relational database

**Why:**

- **JSONB columns** — Store semi-structured data (vehicle features, property amenities, KYC metadata) without schema migration. Query JSONB with indexes for sub-millisecond lookups.
- **Full-text search** — Built-in `tsvector`/`tsquery` provides basic full-text search capabilities for simple queries without Elasticsearch overhead.
- **PostGIS extension** — Native geospatial queries (`ST_DWithin`, `ST_Distance`) for "properties within 5km" searches. No external service needed for basic geo queries.
- **Row-level security (RLS)** — Database-level access control ensures a landlord can never query another landlord's financial data, even if the application layer has a bug.
- **Partitioning** — Table partitioning by date range for bookings, messages, and audit logs. Keeps query performance consistent as data grows to hundreds of millions of rows.
- **Logical replication** — Stream changes to read replicas, Elasticsearch, and analytics databases in real-time.
- **ACID compliance** — Financial transactions (payments, deposits, refunds) require strong consistency guarantees that NoSQL databases cannot provide.
- **Mature hosting options** — AWS RDS, Google Cloud SQL, Azure Database, and Supabase all offer managed PostgreSQL with automated backups, failover, and scaling.

**Alternatives considered:**

- **MySQL** — Lacks JSONB, PostGIS, and advanced partitioning. Less suitable for complex queries.
- **MongoDB** — Document model is tempting for listings but lacks ACID transactions (required for payments), makes joins expensive (required for booking + user + property queries), and the schema-less nature creates data integrity issues at scale.
- **CockroachDB** — Excellent for multi-region writes but overkill at our scale and significantly more expensive.

---

### 3.1.5 Cache Layer: Redis 8 (Cluster Mode)

**Choice:** Redis 8 as the distributed cache, session store, and rate limiter

**Why:**

- **Sub-millisecond latency** — Redis serves cached data in <1ms. Property search results, user sessions, and popular listings can be served from cache instead of hitting PostgreSQL on every request.
- **Data structures beyond key-value** — Sorted sets for leaderboards (top properties by views), HyperLogLog for unique visitor counting, Streams for event processing, and Pub/Sub for real-time cache invalidation across nodes.
- **Rate limiting** — Redis's atomic `INCR` + `EXPIRE` commands implement token bucket rate limiting with zero race conditions. Essential for API abuse prevention.
- **Session store** — JWT refresh tokens and session metadata stored in Redis with TTL auto-expiry. When a user logs out, invalidate their session across all API nodes instantly.
- **Distributed locking** — Redlock algorithm prevents double-booking of the same property/vehicle for the same dates, even when requests hit different API nodes simultaneously.
- **WebSocket session registry** — Track which API node handles which WebSocket connection. When a message arrives, route it to the correct node.
- **Elasticache compatibility** — AWS ElastiCache provides managed Redis with automatic failover, backup, and scaling.

**Alternatives considered:**

- **Memcached** — Faster for simple key-value but lacks data structures, persistence, and pub/sub. Cannot replace Redis for rate limiting or distributed locking.
- **Hazelcast** — Java-native distributed cache but less ecosystem support and fewer managed hosting options.

---

### 3.1.6 Search Engine: Elasticsearch 9.1

**Choice:** Elasticsearch 9.1 for full-text search, geo-search, and analytics

**Why:**

- **Full-text search with relevance scoring** — "Cozy apartment near downtown with parking" returns results ranked by relevance, not just filtered by keywords. Supports synonyms ("flat" = "apartment"), fuzzy matching (typo tolerance), and language-specific analyzers (French stemming).
- **Geo-spatial queries** — `geo_distance` queries for "listings within 10km of my location" with sub-100ms response times, even across millions of listings. Supports geo-bounding box, geo-polygon, and geo-shape queries.
- **Faceted search** — "Show me 3-bedroom apartments in Paris under €2000/month" with real-time aggregation counts ("42 apartments, 18 houses, 7 studios"). This is extremely expensive in PostgreSQL but native to Elasticsearch.
- **Autocomplete & suggestions** — Completion suggester for instant search-as-you-type in the search bar. "Par..." → "Paris, France", "Parking included".
- **Analytics aggregations** — Admin dashboard metrics (bookings per region, revenue trends, popular listing types) computed in Elasticsearch without loading PostgreSQL.
- **Horizontal scaling** — Shard data across nodes. As listings grow from 50K to 5M, add nodes without downtime.
- **Near-real-time indexing** — Changes in PostgreSQL are synced to Elasticsearch within 1 second via the outbox pattern + event consumers.

**Why not PostgreSQL full-text search alone:**

- PostgreSQL's `tsvector` works for simple keyword matching but cannot do relevance scoring, fuzzy matching, faceted aggregation, or geo-distance sorting efficiently. At 50K+ listings with complex filters, PostgreSQL queries become 100-500ms while Elasticsearch remains under 50ms.

**Alternatives considered:**

- **Apache Solr** — Comparable features but less momentum, fewer managed hosting options, and a more complex operational model.
- **Typesense** — Simpler API but limited aggregation capabilities and smaller ecosystem.
- **Meilisearch** — Excellent developer experience but lacks geo-spatial queries and enterprise security features.

---

### 3.1.7 Message Broker: RabbitMQ 4

**Choice:** RabbitMQ 4 for asynchronous messaging, event-driven architecture, and WebSocket message routing

**Why:**

- **Decouples services** — When a booking is confirmed, the booking service publishes a `BookingConfirmed` event. The notification service, payment service, calendar service, and analytics service each consume this event independently. If the notification service is down, the booking still succeeds and notifications are delivered when the service recovers.
- **Reliable message delivery** — Messages are persisted to disk with acknowledgment-based delivery. No event is lost, even during server restarts. Critical for payment events and booking state changes.
- **STOMP plugin** — RabbitMQ has a native STOMP plugin that replaces Spring's simple in-memory broker. WebSocket messages are routed through RabbitMQ, enabling chat to work across multiple API nodes. User A connected to Node 1 can message User B connected to Node 2.
- **Dead letter queues** — Failed messages are automatically routed to a DLQ for inspection and retry. Critical for debugging payment processing failures.
- **Priority queues** — Payment events get higher priority than notification events, ensuring financial operations are never delayed by a notification backlog.
- **Spring AMQP integration** — First-class Spring Boot support. Adding a consumer is a `@RabbitListener` annotation on a method.
- **Proven at scale** — Used by Goldman Sachs, Cisco, and Instagram. Battle-tested for financial messaging.

**Alternatives considered:**

- **Apache Kafka** — Better for event streaming and log aggregation but more complex to operate, higher resource requirements, and overkill for our message patterns (request/reply, pub/sub, work queues). Kafka shines at 100K+ messages/second; our volume is 1K-10K/second.
- **AWS SQS/SNS** — Vendor-locked, higher latency (50-100ms vs 1-5ms), and no STOMP support for WebSocket routing.
- **Redis Pub/Sub** — No message persistence. If a consumer is offline, messages are lost. Unacceptable for payment and booking events.

---

### 3.1.8 Object Storage: AWS S3 + CloudFront CDN

**Choice:** Amazon S3 for file storage, CloudFront for global content delivery

**Why S3:**

- **Unlimited storage** — No capacity planning needed. Store 10 images or 10 million images at the same operational complexity.
- **11 nines durability (99.999999999%)** — Data loss probability is near zero. Critical for legal documents (leases, KYC IDs).
- **Lifecycle policies** — Automatically move old files to cheaper storage tiers (S3 Infrequent Access after 90 days, Glacier after 1 year).
- **Pre-signed URLs** — Generate time-limited upload/download URLs. Users upload directly to S3 from the browser without routing through the API server, eliminating a major bottleneck.
- **Event notifications** — When a file is uploaded, S3 triggers a Lambda function for image resizing, thumbnail generation, or virus scanning.

**Why CloudFront:**

- **Global edge network** — 450+ edge locations worldwide. A user in Casablanca gets property images from a nearby edge server (20ms) instead of the origin in eu-west-1 (200ms).
- **Automatic WebP/AVIF conversion** — CloudFront Functions can transform images to modern formats on the fly, reducing bandwidth by 30-50%.
- **DDoS protection** — AWS Shield Standard is included at no extra cost. Protects against volumetric attacks on media endpoints.

**Alternatives considered:**

- **Google Cloud Storage + Cloud CDN** — Comparable but less mature CDN edge network.
- **Cloudflare R2** — No egress fees (attractive) but fewer integration options with the broader AWS ecosystem we're using.
- **Self-hosted MinIO** — Full S3-compatible API but requires managing storage infrastructure, replication, and backups ourselves.

---

### 3.1.9 Resilience: Resilience4j 🟢 Implemented

**Choice:** Resilience4j for circuit breaking, rate limiting, retry, and bulkhead isolation

**Why:**

- **Circuit breaker** — When Firebase push notifications are down, stop sending requests after 5 failures and return a fallback (queue for retry). Without this, a downstream outage causes thread pool exhaustion and cascading failures across the entire platform.
- **Rate limiter** — Limit property search to 30 requests/minute per user. Limit login attempts to 5/minute per IP. Prevent abuse without building custom rate limiting infrastructure.
- **Retry with exponential backoff** — Payment processing to Stripe occasionally fails due to network issues. Automatically retry 3 times with 1s, 2s, 4s delays before giving up.
- **Bulkhead** — Isolate thread pools per external service. If the email service blocks on SMTP connections, it doesn't consume threads needed for booking API requests.
- **Time limiter** — KYC verification via third-party API must respond within 10 seconds or timeout. Prevents hung requests.
- **Lightweight** — Unlike Hystrix (deprecated), Resilience4j is a library, not a framework. No runtime overhead when circuits are closed.
- **Spring Boot integration** — Annotate any method with `@CircuitBreaker`, `@RateLimiter`, `@Retry`. Configuration via application.yml.

**Alternatives considered:**

- **Netflix Hystrix** — Deprecated since 2018. No longer maintained.
- **Sentinel (Alibaba)** — Powerful but documentation is primarily in Chinese, and the ecosystem is Alibaba-centric.

---

### 3.1.10 API Documentation: SpringDoc OpenAPI 3.0.1

**Choice:** SpringDoc OpenAPI with Swagger UI

**Why:**

- **Auto-generated from code** — API documentation is always in sync with the actual implementation. No manual YAML/JSON maintenance.
- **Swagger UI** — Interactive API explorer for frontend developers and QA. Test endpoints directly from the browser.
- **Client code generation** — Generate TypeScript API clients from the OpenAPI spec, eliminating manual service creation in Angular.
- **Spring Boot 4 compatible** — Version 3.0.1 is specifically built for Spring Boot 4 and Spring Framework 7.

---

### 3.1.11 ORM: Hibernate 6 (via Spring Data JPA)

**Choice:** Hibernate 6 as the JPA implementation

**Why:**

- **Entity mapping** — Map Java objects to database tables with annotations. Relationships (OneToMany, ManyToMany) are declared once and Hibernate handles joins, cascades, and lazy loading.
- **Batch operations** — `spring.jpa.properties.hibernate.jdbc.batch_size=20` groups 20 INSERT/UPDATE statements into a single database round-trip. Critical for bulk listing imports and notification fanout.
- **Second-level cache** — Frequently accessed entities (amenities, categories, regions) cached across sessions with Redis as the cache provider.
- **Hibernate Envers** — Audit logging for entity changes. Every modification to a listing or booking is recorded with who, when, and what changed. Required for compliance and dispute resolution.
- **Database-agnostic** — Switch from PostgreSQL to MySQL or CockroachDB by changing a configuration line. No query rewrites.

---

## 3.2 Frontend Stack

### 3.2.1 Framework: Angular 21

**Choice:** Angular 21 (current active release, LTS planned)

**Why:**

- **Opinionated structure** — Angular enforces a consistent project structure (modules, components, services, guards, interceptors) that scales to large teams. Unlike React, which requires choosing routing, state management, and folder structure, Angular provides all of these out of the box.
- **TypeScript-first** — Angular is built in TypeScript and requires it. This catches type errors at compile time, not runtime. For a financial platform handling payments, type safety is non-negotiable.
- **Dependency injection** — Angular's DI system is the most mature in the frontend ecosystem. Services are singletons by default, testable via injection, and tree-shakeable.
- **Signals (Angular 21)** — Zone.js-free reactivity model. Components re-render only when their specific signal dependencies change, not when any async operation completes. 30-50% fewer unnecessary change detection cycles.
- **Standalone components** — No more NgModules. Each component declares its own imports, making code splitting and lazy loading granular.
- **Built-in i18n** — Native internationalization support with compile-time translation extraction and runtime locale switching.
- **Angular Material + CDK** — Component Dev Kit provides unstyled, accessible primitives (drag-drop, virtual scroll, overlay) that we style with Tailwind. No framework lock-in.
- **Enterprise adoption** — Google, Microsoft, Deutsche Bank, and UPS use Angular. Large talent pool, long-term support, and predictable release cadence (every 6 months).
- **Schematics** — `ng generate` scaffolds components, services, guards, and pipes with consistent structure. Enforces team conventions automatically.

**Alternatives considered:**

- **React 19** — Excellent component model but requires assembling routing (React Router), state management (Redux/Zustand), form handling (React Hook Form), and HTTP client (Axios) from separate libraries. More freedom but more decisions and inconsistency risk across a large team.
- **Vue 3** — Great developer experience but smaller enterprise ecosystem, fewer large-scale production references, and less TypeScript maturity than Angular.
- **Next.js / Nuxt / Analog** — SSR frameworks. We don't need server-side rendering — our app is a SPA behind authentication. SSR adds complexity without benefit for dashboard-heavy applications.

---

### 3.2.2 UI Framework: Tailwind CSS 4 + Ionic 8

**Choice:** Tailwind CSS 4 as the primary styling system, Ionic 8 for mobile-specific UI components

**Why Tailwind CSS 4:**

- **Utility-first** — Build UIs by composing small utility classes (`flex items-center gap-4 p-6 bg-white rounded-xl shadow-lg`) instead of writing custom CSS. Eliminates naming conventions (BEM), specificity wars, and dead CSS.
- **Zero runtime** — Tailwind generates only the CSS classes you actually use. Typical production CSS is 10-30KB gzipped, compared to 200KB+ for Bootstrap or Material Design CSS.
- **v4 is a ground-up rewrite** — 5x faster full builds, 100x faster incremental builds. Uses native CSS cascade layers, `@property` for custom properties, and `color-mix()` for dynamic colors.
- **CSS-first configuration** — v4 replaces `tailwind.config.js` with CSS `@theme` directives. Configuration lives alongside the styles it affects.
- **Design system enforcement** — Tailwind's constrained utility set (spacing: 1, 2, 3, 4... not arbitrary pixels) naturally produces consistent designs without a separate design system tool.
- **Responsive + dark mode** — `md:grid-cols-3 dark:bg-gray-900` handles responsive and dark mode with zero JavaScript.

**Why Ionic 8 (alongside Tailwind):**

- **Native mobile UI patterns** — `ion-modal`, `ion-action-sheet`, `ion-refresher`, `ion-infinite-scroll` provide platform-specific UX patterns that web CSS cannot replicate. Pull-to-refresh feels native on iOS because Ionic uses platform conventions.
- **Capacitor integration** — Ionic is built by the same team as Capacitor. Components are designed to work seamlessly in native container apps.
- **Gesture system** — Swipe-to-delete, drag-to-reorder, and pinch-to-zoom are built into Ionic's gesture API. Building these from scratch with Hammer.js or native events is a significant engineering effort.
- **We use Ionic selectively** — Only for mobile-specific interactions (modals, action sheets, infinite scroll, toast, loading). All layout and styling is Tailwind. This avoids the bloat of using Ionic for everything.

**Why NOT Angular Material (removing it):**

- **Redundant with Ionic** — Both provide buttons, cards, dialogs, and form controls. Having both means conflicting styles, doubled bundle size, and confused developers choosing between `mat-button` and `ion-button`.
- **Material Design aesthetic** — Forces Google's design language. Tailwind gives us complete design freedom.
- **Keep only Angular CDK** — The headless primitives (virtual scroll, drag-drop, overlay, accessibility) from `@angular/cdk` are valuable and framework-agnostic. We keep CDK, drop Material.

---

### 3.2.3 State Management: NgRx Signal Store 🟢

**Current implementation:** NgRx Signal Store (`@ngrx/signals`) for all entity and session state. `SessionStore` manages auth state; `WorkspaceStore` manages properties, bookings, unread counts, and notification state. All signals are exposed as `protected` for template access.

**Why NgRx Signal Store:**

- **Signal-based** — Built on Angular 21's signals, not RxJS Observables. Simpler mental model.
- **Centralized state** — All application state in typed stores with DevTools for debugging.
- **Entity management** — Normalized entity state, CRUD operations, and selection.
- **`takeUntilDestroyed` pattern** — All RxJS subscriptions inside components use `takeUntilDestroyed(destroyRef)`, eliminating manual `ngOnDestroy` unsubscription.

---

### 3.2.4 Real-Time: STOMP over SockJS 🟢

**Choice:** STOMP/WebSocket for real-time messaging

**Current implementation:**

- STOMP protocol over WebSocket with SockJS fallback
- In-memory Simple Broker (Spring's built-in)
- `WebSocketService` manages the STOMP connection on the frontend
- Chat messages via `/topic/chat.{roomId}`, typing indicators via `/topic/typing.{roomId}`
- JWT authentication on STOMP CONNECT frame

**Planned:**

- RabbitMQ-backed STOMP relay for multi-node message delivery
- Single unified connection for chat + notifications + booking updates

---

### 3.2.5 Mobile: Capacitor 8

**Choice:** Capacitor 8 for native iOS and Android builds

**Why:**

- **Web-to-native bridge** — Write once in Angular, deploy to web, iOS, and Android. Access native APIs (camera, GPS, push notifications, biometrics) through a JavaScript bridge.
- **Capacitor 8 improvements** — Built-in edge-to-edge support (no more status bar hacks), Swift Package Manager for iOS (faster builds), and improved plugin architecture.
- **No React Native / Flutter rewrite** — Building separate native apps means 3 codebases (web, iOS, Android) with 3x the maintenance cost. Capacitor gives us native distribution from a single codebase.
- **Full web compatibility** — Unlike React Native (which uses native views), Capacitor renders the actual web app in a native WebView. This means 100% feature parity between web and mobile — no "this feature is web-only" limitations.
- **Plugin ecosystem** — Camera, geolocation, push notifications, filesystem, biometrics, app updates — all available as official plugins.

**Trade-off acknowledged:** WebView performance is 10-20% slower than truly native rendering. For a content-heavy app (listings, images, text) this is imperceptible. For a 3D game, it would be unacceptable.

---

### 3.2.6 Maps: Leaflet 1.9

**Choice:** Leaflet for interactive maps

**Why:**

- **Free and open-source** — No API key required for the library itself. Google Maps charges $7/1000 loads after the free tier. At 100K MAU viewing maps, Google Maps would cost $2K+/month. Leaflet is $0.
- **Lightweight** — 42KB gzipped vs Google Maps (150KB+) or Mapbox GL (200KB+).
- **Tile provider flexibility** — Use OpenStreetMap (free), Mapbox (premium), or Stamen (artistic) tiles. Switch providers without code changes.
- **Marker clustering** — `leaflet.markercluster` plugin groups nearby listings into clusters at low zoom levels. Essential when displaying 1000+ listings on a single map.
- **Custom markers** — Style markers to differentiate property types, price ranges, or availability status.

---

### 3.2.7 Charts: Chart.js 4.5

**Choice:** Chart.js for admin dashboard visualizations

**Why:**

- **Simple API** — Bar, line, pie, doughnut, and radar charts with minimal configuration. The admin dashboard needs standard business charts, not D3-level custom visualizations.
- **Canvas-based** — Renders on HTML Canvas, not SVG. Better performance for charts with many data points (monthly revenue trends across 12 months × 5 regions = 60 data points).
- **8KB gzipped** — Tree-shakeable. Import only the chart types you use.
- **Responsive** — Charts resize automatically on window resize. No manual dimension management.

**Alternatives considered:**

- **D3.js** — Overkill. D3 is a low-level visualization library for custom data visualizations (network graphs, geographic heat maps). Our admin dashboard needs standard bar/line/pie charts.
- **ECharts** — More features but 300KB+ bundle size. Not worth it for our use case.

---

### 3.2.8 TypeScript 5.9

**Choice:** TypeScript 5.9 (required by Angular 21)

**Why:**

- **Required by Angular 21** — Angular 21's compiler requires TypeScript >=5.9.0 <6.0.0.
- **Improved type inference** — Better generic type inference reduces the need for explicit type annotations.
- **`satisfies` operator** — Validate that a value matches a type without widening the type. Useful for configuration objects and routing definitions.
- **Decorator metadata** — Native decorator support without `experimentalDecorators` flag.

---

## 3.3 Infrastructure Stack

### 3.3.1 Cloud Provider: AWS (Amazon Web Services)

**Choice:** AWS as the primary cloud provider

**Why:**

- **Most mature cloud platform** — 200+ services, 33 geographic regions, 105 availability zones. The largest selection of compute, storage, networking, and AI services.
- **Multi-region capability** — Deploy HomeFlex in `us-east-1` (North America), `eu-west-1` (Europe), and `me-south-1` (Middle East/North Africa) from day one. AWS has the most regions globally, including the MENA region that GCP and Azure have limited presence in.
- **Managed services reduce ops burden:**
  - **RDS** — Managed PostgreSQL with automated backups, read replicas, and failover
  - **ElastiCache** — Managed Redis with cluster mode
  - **Amazon MQ** — Managed RabbitMQ
  - **OpenSearch** — Managed Elasticsearch
  - **ECS Fargate** — Serverless container orchestration (no EC2 instances to manage)
  - **CloudFront** — Global CDN with 450+ edge locations
  - **S3** — Unlimited object storage
  - **Route 53** — DNS with latency-based routing for multi-region
  - **WAF** — Web Application Firewall for API protection
  - **Cognito** — Optional identity provider (we use our own JWT but Cognito can handle Google/Apple OAuth)
- **Cost optimization tools** — Savings Plans, Spot Instances, and Reserved Instances can reduce costs by 40-70%.
- **Compliance certifications** — SOC 1/2/3, ISO 27001, PCI DSS, HIPAA, GDPR. Critical for handling payment data and personal information.

**Alternatives considered:**

- **Google Cloud Platform (GCP)** — Strong in AI/ML and Kubernetes (GKE is excellent) but fewer regions (40 vs AWS's 33+ but AWS has more edge locations), and limited presence in MENA. Better choice if we were building ML-heavy features.
- **Azure** — Strong enterprise integration (Active Directory, Office 365) but less developer-friendly tooling and higher prices for comparable services. Better choice if we were building for enterprise IT departments.
- **Multi-cloud** — Running on multiple clouds increases complexity 3-5x (different IAM, networking, monitoring for each) with marginal benefit. We design for cloud portability (containerized, standard protocols) but deploy on one cloud.

---

### 3.3.2 Container Orchestration: AWS ECS Fargate

**Choice:** ECS Fargate for serverless container orchestration

**Why:**

- **No server management** — Fargate runs containers without EC2 instances. No OS patching, no capacity planning, no AMI updates. Define CPU/memory per container and AWS handles placement.
- **Auto-scaling** — Scale from 2 to 50 containers based on CPU, memory, or custom metrics (request count, queue depth). Scale back to 2 during off-peak hours automatically.
- **Cost-effective at our scale** — Kubernetes (EKS) has a $73/month control plane cost per cluster plus node management overhead. Fargate charges only for running containers. At <50 containers, Fargate is cheaper and simpler.
- **Service discovery** — AWS Cloud Map provides DNS-based service discovery. The API service finds the notification service by name (`notification.homeflex.local`), not by IP address.
- **Blue/green deployments** — ECS supports zero-downtime deployments by spinning up new containers, health-checking them, and draining old containers. No manual deployment scripts.

**When to upgrade to Kubernetes (EKS):**

- When we exceed 100 containers or need advanced scheduling (GPU workloads, spot instance management, custom operators). This is a v3.0+ consideration.

---

### 3.3.3 CI/CD: GitHub Actions

**Choice:** GitHub Actions for continuous integration and deployment

**Why:**

- **Integrated with GitHub** — Our source code is on GitHub. No external CI/CD tool to configure, authenticate, and maintain.
- **Workflow-as-code** — YAML pipeline definitions versioned alongside application code. Changes to the pipeline go through the same PR review process as code changes.
- **Matrix builds** — Test on Java 21, Java 25 simultaneously. Test on Node 20, Node 22 simultaneously. Catch compatibility issues before they reach production.
- **Caching** — Cache Gradle dependencies, npm packages, and Docker layers between runs. A 10-minute build becomes 3 minutes with warm caches.
- **Environment protection rules** — Production deployments require manual approval from designated reviewers. No accidental pushes to prod.
- **Free for public repos** — 2,000 CI/CD minutes/month for private repos on the Team plan.

---

### 3.3.4 Containerization: Docker

**Choice:** Docker for application packaging

**Why:**

- **Environment parity** — The exact same container image runs on a developer's laptop, in CI, in staging, and in production. "Works on my machine" is eliminated.
- **Multi-stage builds** — Build stage uses full JDK (400MB) for compilation. Runtime stage uses JRE-slim (150MB). Final image is 200MB instead of 600MB.
- **Docker Compose for local development** — `docker-compose up` starts PostgreSQL, Redis, RabbitMQ, Elasticsearch, and the API server with one command. New developers are productive in 5 minutes, not 5 hours.
- **Layer caching** — Dependency layers (rarely change) are cached separately from application code layers (change frequently). Rebuilds are fast.

---

### 3.3.5 Monitoring: Prometheus + Grafana + ELK

**Choice:** Prometheus for metrics, Grafana for dashboards, ELK for logs

**Why Prometheus:**

- **Pull-based metrics** — Prometheus scrapes `/actuator/prometheus` every 15 seconds. No agent installation, no SDK integration. Spring Boot Actuator exposes JVM, HTTP, database, and custom metrics automatically.
- **PromQL** — Powerful query language for alerts. `rate(http_requests_total{status="500"}[5m]) > 0.01` triggers an alert when error rate exceeds 1%.
- **AlertManager** — Route alerts to Slack, PagerDuty, or email based on severity and service.

**Why Grafana:**

- **Unified dashboards** — Visualize Prometheus metrics, Elasticsearch logs, and PostgreSQL stats in a single dashboard. No context switching between tools.
- **Pre-built dashboards** — Spring Boot, PostgreSQL, Redis, RabbitMQ, and JVM dashboards available from the Grafana community.

**Why ELK (Elasticsearch + Logstash + Kibana):**

- **Centralized logging** — Aggregate logs from all API nodes, workers, and infrastructure into a single searchable index.
- **Structured logging** — JSON log format with correlation IDs, user IDs, and request metadata. Search "all requests from user X that resulted in a 500 error" in seconds.
- **Log-based alerts** — Alert on error patterns before they become incidents.

---

## 3.4 External Services

### 3.4.1 Payment Processing: Stripe

**Choice:** Stripe for payment processing, escrow, and payouts

**Why:**

- **Stripe Connect** — Purpose-built for marketplace payments. Renters pay HomeFlex, HomeFlex holds funds in escrow, and HomeFlex pays out to owners minus commission. This is exactly our business model.
- **Global coverage** — Supports 135+ currencies, 46+ countries, and local payment methods (SEPA in Europe, Bancontact in Belgium, iDEAL in Netherlands).
- **PCI DSS Level 1** — Stripe handles all card data. We never see, store, or transmit card numbers. Our PCI compliance scope is minimal (SAQ-A).
- **Stripe Identity** — KYC verification (ID document scanning, selfie matching, address verification) built into the same platform. No separate KYC vendor needed.
- **Webhook-driven** — Payment events (succeeded, failed, refunded, disputed) are delivered via webhooks. Our system reacts to events rather than polling.
- **Subscription billing** — For monthly rent collection, Stripe Billing handles recurring charges, proration, and dunning (retry failed payments).

---

### 3.4.2 SMS Notifications: Twilio 🟢 Implemented

**Choice:** Twilio for SMS and WhatsApp notifications

**Why:**

- **Global SMS delivery** — Send SMS to 180+ countries with local number support.
- **WhatsApp Business API** — In MENA and Europe, WhatsApp has 90%+ penetration.
- **Verify API** — Phone number verification with OTP for KYC.

**Implementation:** `TwilioSmsGateway` integrated for booking lifecycle notifications. SMS sent on booking creation, approval, rejection, and cancellation events.

---

### 3.4.3 Email: Gmail SMTP (current) → AWS SES (planned)

**Current:** Gmail SMTP via Spring Mail (`EmailService`). Suitable for development and low-volume transactional email.

**Planned:** AWS SES for production-scale email.

- **$0.10 per 1,000 emails** — 10x cheaper than SendGrid at scale.
- **High deliverability** — Dedicated IP addresses, DKIM/SPF/DMARC.
- **AWS ecosystem integration** — Triggered directly from the API server.

---

### 3.4.4 Push Notifications: Firebase Cloud Messaging (FCM)

**Choice:** Firebase Cloud Messaging for mobile push notifications

**Why:**

- **Free** — No per-message cost, regardless of volume.
- **Cross-platform** — Single API for iOS (APNs) and Android (FCM).
- **Topic messaging** — Subscribe users to topics ("new-listings-paris") for targeted push.
- **Already integrated** — Current codebase uses Firebase Admin SDK.

---

### 3.4.5 OAuth Providers: Google (implemented) + Apple + Facebook (planned)

**Current:** Google OAuth is implemented (`OAuthProvider` entity, `AuthService.googleLogin()`).

**Planned:**

- **Apple Sign-In** — Required by Apple App Store guidelines for apps offering third-party login.
- **Facebook Login** — High adoption in MENA and European markets.

---

## 3.5 Stack Decision Matrix (Use, Tradeoffs, Revisit Criteria)

This matrix is normative for architectural governance. Every major choice includes purpose, accepted tradeoff, and trigger to revisit.

| Layer             | Selected Technology             | Primary Use                        | Status                                    |
| ----------------- | ------------------------------- | ---------------------------------- | ----------------------------------------- |
| Backend runtime   | Java 21                         | High-concurrency APIs, LTS         | 🟢                                        |
| Backend framework | Spring Boot 4                   | Enterprise API delivery            | 🟢                                        |
| Primary DB        | PostgreSQL 18                   | Transactions, relational integrity | 🟢                                        |
| Distributed cache | Redis 8                         | Rate-limits, sessions              | 🟡 Rate-limiting active; Redlock planned  |
| Search engine     | Elasticsearch 9.1               | Full-text, geo, faceted search     | 🟢 Active via outbox relay                |
| Event broker      | RabbitMQ 4                      | Async workflows, messaging         | 🟢 Outbox events consumed                 |
| Object storage    | S3                              | Media storage                      | 🟡 StorageService exists, dev fallback    |
| Frontend          | Angular 21 + Tailwind CSS 4     | SPA dashboard                      | 🟢                                        |
| State management  | NgRx Signal Store               | Reactive state                     | 🟢                                        |
| Mobile            | Flutter (separate repo)         | Native iOS/Android                 | 🟢 (separate repo, not in Docker Compose) |
| Payments          | Stripe Connect (MANUAL capture) | Escrow, payouts, KYC               | 🟢                                        |
| Notifications     | FCM + Gmail SMTP + Twilio       | Push + email + SMS/WhatsApp        | 🟢 (AWS SES planned for prod scale)       |
| Resilience        | Resilience4j                    | Circuit breakers, retry            | 🟢 On EmailService, Firebase, Stripe      |
| Observability     | Prometheus + Grafana + ELK      | Metrics, dashboards, logs          | 🟢 Deployed via docker-compose.monitoring |
| CI/CD             | GitHub Actions + Docker         | Build pipeline                     | 🟢 (ECS deployment planned)               |

# 4. System Architecture

## 4.0 Current State vs Target State

This section separates what is implemented from what is planned. Updated 2026-04-23.

| Area                  | Current State (implemented)                                                                                                                                 | Target State (planned)                                                    | Gap Priority |
| --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- | ------------ |
| Backend architecture  | Package-by-feature: `com.homeflex.core` + `com.homeflex.features.<feature>`. Layered within each module: `api/v1/` → `service/` → `domain/repository/` → DB | Evolve to DDD bounded contexts at scale                                   | Low          |
| Backend build/runtime | Gradle + Java 21 + Spring Boot 4                                                                                                                            | Keep stack, harden runtime controls                                       | Low          |
| Frontend composition  | Fully standalone components (Angular 21), NgRx Signal Store, `takeUntilDestroyed` subscriptions                                                             | Production deployment on ECS                                              | Medium       |
| Frontend state        | NgRx Signal Store (`SessionStore`, `WorkspaceStore`)                                                                                                        | DevTools integration, time-travel debugging                               | Low          |
| Auth token storage    | httpOnly cookies (ACCESS_TOKEN + REFRESH_TOKEN) + CSRF token flow                                                                                           | Secrets Manager for JWT secret                                            | Low          |
| WebSocket             | STOMP over WebSocket with in-memory Simple Broker                                                                                                           | RabbitMQ-backed STOMP relay for multi-node support                        | Medium       |
| Caching               | Redis rate-limiting active; Spring Cache not yet applied                                                                                                     | Redis for property search, session data, Redlock distributed locking      | Medium       |
| Messaging             | Transactional outbox + RabbitMQ consumers (PropertyIndexConsumer)                                                                                           | Full event worker fleet for all domain events                             | Medium       |
| Search                | Elasticsearch 9.1 via outbox-relay consumer, fuzzy + geo queries                                                                                            | Multi-field boosting, autocomplete, analytics aggregations                | Low          |
| Email                 | Gmail SMTP via Spring Mail                                                                                                                                  | AWS SES for production-scale transactional email                          | Low          |
| SMS/WhatsApp          | Twilio integrated (`TwilioSmsGateway`)                                                                                                                      | Phone OTP verification flow                                               | Low          |
| Storage               | StorageService exists (S3 + dev fallback)                                                                                                                   | Fully configured S3 + CloudFront CDN                                      | Medium       |
| Deployment            | Docker Compose (8 services on single host)                                                                                                                  | AWS ECS Fargate with auto-scaling, ALB, health checks                     | High         |
| Monitoring            | Prometheus + Grafana + ELK stack (docker-compose.monitoring.yml)                                                                                            | Production scraping, PagerDuty alerts, SLO dashboards                     | Medium       |
| Security              | Full RBAC, httpOnly cookies, CSRF, rate-limiting, WAF headers, Resilience4j                                                                                 | Secrets Manager, stricter CORS, policy as code                            | Medium       |
| OAuth providers       | Google only (Apple/Facebook UI shows "Soon" badge)                                                                                                          | Google + Apple + Facebook                                                 | Low          |
| Vehicle vertical      | Full implementation (CRUD, images, availability, bookings, split-payment, 10-state lifecycle)                                                                | Condition reports, insurance integration                                  | Low          |
| KYC                   | Stripe Identity implemented (KycVerification entity, webhook status updates, landlord publishing guard)                                                      | Mandatory KYC gate before first listing                                   | Low          |

## 4.1 High-Level Architecture Diagram

### 4.1.1 Current Architecture (Implemented) 🟢

```
┌──────────────────────────────────────────────────────────────┐
│                         CLIENTS                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                    │
│  │ Angular  │  │ iOS App  │  │ Android  │                    │
│  │   SPA    │  │(Capacitor)│  │(Capacitor)│                    │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘                    │
└───────┼──────────────┼──────────────┼──────────────────────────┘
        │              │              │
        └──────────────┼──────────────┘
                       │         HTTP / WS
                       ▼
            ┌──────────────────┐
            │     Nginx        │  (Docker: frontend container)
            │  - Serves SPA    │
            │  - Proxy /api/*  │──────┐
            │  - Proxy /ws/*   │      │
            │  - gzip, headers │      │
            └──────────────────┘      │
                                      ▼
                            ┌──────────────────┐
                            │  Spring Boot 4   │  (Docker: backend container)
                            │  REST API + WS   │
                            │  Port 8080       │
                            └────────┬─────────┘
                                     │
              ┌──────────────────────┼──────────────────┐
              │                      │                  │
              ▼                      ▼                  ▼
       ┌──────────┐        ┌──────────────┐    ┌──────────────┐
       │ Firebase │        │  PostgreSQL  │    │  Gmail SMTP  │
       │   FCM    │        │  16 (Docker) │    │  (Email)     │
       │  (Push)  │        │  DB: homeflex│    └──────────────┘
       └──────────┘        └──────────────┘
                                                ┌──────────────┐
              ┌──────────────┐                  │   Stripe     │
              │ Google OAuth │                  │  (Payments)  │
              └──────────────┘                  └──────────────┘

   Also connected (active):
   ┌──────┐    ┌─────────────┐    ┌────────────────┐
   │Redis │    │Elasticsearch│    │   RabbitMQ     │
   │  8   │    │    9.1      │    │      4         │
   └──────┘    └─────────────┘    └────────────────┘

   Monitoring stack (docker-compose.monitoring.yml):
   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
   │Logstash  │    │ Kibana   │    │Prometheus│    │ Grafana  │
   │  9.1     │    │  9.1     │    │  3.5     │    │  11.6    │
   └──────────┘    └──────────┘    └──────────┘    └──────────┘
```

All 8 Docker services run on a shared `rental-network` (bridge). The backend container waits for db, redis, rabbitmq, and elasticsearch to be healthy before starting.

### 4.1.2 Target Architecture (Planned) 🔴

```
Clients → CloudFront (CDN + WAF) → AWS ALB → ECS Fargate (auto-scaling API nodes)
  → PostgreSQL (RDS HA) + Redis (ElastiCache) + RabbitMQ (Amazon MQ)
  → Elasticsearch (OpenSearch) + Event Workers (Fargate)
  → Stripe + Twilio + Firebase + AWS SES + S3 + CloudFront
```

The target architecture adds: load balancing, auto-scaling, managed databases, RabbitMQ-backed messaging with event workers, Elasticsearch for search, and full AWS infrastructure. See `docs/ARCHITECTURE.md` for detailed diagrams.

## 4.2 Backend Package Structure

### 4.2.1 Implemented Structure 🟢

The backend uses a **package-by-feature** architecture. Cross-cutting concerns live under `com.homeflex.core`, and each business domain is a feature module under `com.homeflex.features.<feature>`:

```
com.homeflex/
├── HomeFlexApplication.java
├── core/
│   ├── CoreModuleConfig.java
│   ├── config/                          # AppProperties, SecurityConfig, WebSocketConfig,
│   │                                    # FirebaseConfig, DataInitializer, SampleDataInitializer
│   ├── security/                        # JwtAuthenticationFilter, JwtTokenProvider
│   ├── exception/                       # GlobalExceptionHandler + custom exceptions
│   ├── domain/
│   │   ├── entity/                      # User, RefreshToken, OAuthProvider, ChatRoom,
│   │   │                                # Message, Notification, FcmToken, TypingNotification
│   │   ├── repository/                  # 7 repos for above entities
│   │   ├── enums/                       # UserRole, NotificationType
│   │   └── event/                       # OutboxEvent, OutboxEventRepository
│   ├── dto/
│   │   ├── common/                      # ApiPageResponse, ApiListResponse, ApiValueResponse
│   │   ├── event/                       # OutboxEventMessage
│   │   ├── request/                     # Auth/user/chat request DTOs
│   │   └── response/                    # AuthResponse, UserDto, ChatRoomDto, MessageDto, NotificationDto
│   ├── mapper/                          # UserMapper, ChatMapper, NotificationMapper
│   ├── service/                         # AuthService, UserService, ChatService, EmailService,
│   │                                    # NotificationService, AdminService, StorageService,
│   │                                    # PaymentService, EventOutboxService, OutboxRelayService
│   ├── infrastructure/notification/     # NotificationGateway, FirebaseNotificationGateway
│   └── api/v1/                          # AdminController, AuthV1Controller, ChatController,
│                                        # NotificationController, UserController, WebSocketChatController
│
├── features/
│   ├── property/
│   │   ├── PropertyModuleConfig.java
│   │   ├── api/v1/                      # PropertyV1Controller, BookingV1Controller,
│   │   │                                # FavoriteController, ReviewController, StatsController
│   │   ├── domain/
│   │   │   ├── entity/                  # Property, PropertyImage, PropertyVideo, Amenity,
│   │   │   │                            # Booking, Favorite, Review, ReportedListing
│   │   │   ├── enums/                   # PropertyType, PropertyStatus, ListingType,
│   │   │   │                            # BookingStatus, BookingType, AmenityCategory
│   │   │   └── repository/             # PropertyRepository, BookingRepository, + 6 more
│   │   ├── dto/
│   │   │   ├── request/                 # PropertyCreateRequest, BookingCreateRequest, etc.
│   │   │   └── response/              # PropertyDto, BookingDto, FavoriteDto, ReviewDto, etc.
│   │   ├── mapper/                      # PropertyMapper, BookingMapper, FavoriteMapper,
│   │   │                                # ReviewMapper, ReportMapper, AdminMapper
│   │   └── service/                     # PropertyService, BookingService, FavoriteService, ReviewService
│   │
│   └── vehicle/
│       ├── VehicleModuleConfig.java
│       ├── api/v1/                      # VehicleV1Controller
│       ├── domain/
│       │   ├── entity/                  # Vehicle
│       │   ├── enums/                   # FuelType, Transmission, VehicleStatus
│       │   └── repository/             # VehicleRepository
│       ├── dto/
│       │   ├── request/                 # VehicleCreateRequest
│       │   └── response/              # VehicleResponse, VehicleSearchParams
│       ├── mapper/                      # VehicleMapper
│       └── service/                     # VehicleService
```

### 4.2.2 Feature Module Template

Every new feature module must follow this consistent structure (see `docs/architecture-guardrails.md`):

```
com.homeflex.features.<feature>/
├── <Feature>ModuleConfig.java       # @Configuration + @AutoConfigurationPackage + @EnableJpaRepositories
├── api/v1/                          # REST controllers
├── domain/
│   ├── entity/                      # JPA entities
│   ├── enums/                       # Domain enumerations
│   └── repository/                  # Spring Data JPA repositories
├── dto/
│   ├── request/                     # Inbound DTOs (Java records)
│   └── response/                    # Outbound DTOs
├── mapper/                          # MapStruct mappers
└── service/                         # Business logic (concrete classes, no interface+impl)
```

## 4.3 Event-Driven Architecture

### 4.3.1 Current State: Transactional Outbox (Partial) 🟡

The `EventOutboxService` writes domain events (e.g., booking created, property approved) to an `outbox_events` table in PostgreSQL. However, **no consumer/worker currently processes these events**. Side effects (notifications, emails) are triggered synchronously within service methods.

```
┌──────────┐     ┌──────────────┐     ┌─────────────┐
│  Tenant  │────▶│ Booking API  │────▶│ PostgreSQL  │
│  (HTTP)  │     │ Controller   │     │ (Booking +  │
└──────────┘     └──────┬───────┘     │ OutboxEvent)│
                        │             └─────────────┘
                        │ synchronous call
                        ▼
                 ┌──────────────┐     ┌──────────────┐
                 │ Notification │────▶│  Firebase    │
                 │ Service      │     │  FCM (Push)  │
                 └──────┬───────┘     └──────────────┘
                        │
                        ▼
                 ┌──────────────┐
                 │ Email Service│────▶ Gmail SMTP
                 └──────────────┘
```

### 4.3.2 Target State: RabbitMQ Event Workers 🔴

The target architecture adds RabbitMQ-backed workers that consume outbox events and handle side effects asynchronously. See the target architecture diagram in §4.1.2.

## 4.4 Booking State Machine

### Implemented States 🟢

The `BookingStatus` enum defines 10 states enforced by `BookingStateMachine`. All transitions are audit-logged in `BookingAuditLog`.

```
                              ┌─────────────────┐
                         ───▶ │     DRAFT        │  (POST /bookings/draft)
                              └────────┬─────────┘
                                       │ POST /bookings/{id}/pay
                                       ▼
                              ┌─────────────────┐
                              │ PAYMENT_PENDING  │
                              └──────┬─────┬─────┘
                                     │     │
                          success    │     │  failure
                                     │     ▼
                                     │  ┌──────────────────┐
                                     │  │  PAYMENT_FAILED  │
                                     │  └──────────────────┘
                                     ▼
                              ┌─────────────────┐
                              │ PENDING_APPROVAL │
                              └──┬──────────┬───┘
                                 │          │
                         approve │          │ reject
                                 ▼          ▼
                          ┌──────────┐  ┌──────────┐
                          │ APPROVED │  │ REJECTED │
                          └────┬─────┘  └──────────┘
                               │  │
                    check-in   │  │ cancel (pre-stay)
                               │  └────────────────▶ ┌───────────┐
                               ▼                      │ CANCELLED │
                          ┌──────────┐               └───────────┘
                          │  ACTIVE  │
                          └────┬──┬──┘
                               │  │
                     check-out │  │ cancel (early)
                               │  └────────────────▶ ┌───────────┐
                               ▼                      │ CANCELLED │
                          ┌───────────┐              └───────────┘
                          │ COMPLETED │
                          └───────────┘

                    PENDING_MODIFICATION — any party requests change
```

**Implemented state transitions:**
| From | To | Trigger | Side Effects |
|------|----|---------|----|
| DRAFT | PAYMENT_PENDING | Tenant pays | Stripe PaymentIntent created (MANUAL capture) |
| PAYMENT_PENDING | PENDING_APPROVAL | Stripe webhook: `payment_intent.succeeded` | Funds authorized in escrow |
| PAYMENT_PENDING | PAYMENT_FAILED | Stripe webhook: `payment_intent.payment_failed` | Notify tenant |
| PENDING_APPROVAL | APPROVED | Landlord approves | Capture PaymentIntent, notify tenant |
| PENDING_APPROVAL | REJECTED | Landlord rejects | Cancel PaymentIntent (release hold), notify tenant |
| PENDING_APPROVAL | CANCELLED | Tenant cancels | Cancel PaymentIntent, release dates |
| APPROVED | ACTIVE | Check-in date reached (scheduled) | Begin rent collection cycle |
| APPROVED | CANCELLED | Tenant early-cancel | Full Stripe refund if captured |
| ACTIVE | COMPLETED | Check-out date reached (scheduled) | Request review |
| ACTIVE | CANCELLED | Early checkout | Prorated refund for unused nights |
| Any | PENDING_MODIFICATION | Either party requests change | Notification sent |

---

# 5. Functional Requirements

> **Legend:** 🟢 Implemented | 🟡 Partial | 🔴 Planned

## 5.1 User Management

### FR-100: User Registration 🟢

| ID                      | FR-100                                                                            |
| ----------------------- | --------------------------------------------------------------------------------- | ---------- |
| **Description**         | Users register via email/password or Google OAuth                                 |
| **Roles**               | TENANT, LANDLORD, ADMIN                                                           |
| **Acceptance Criteria** | Status                                                                            |
| AC-1                    | Email registration requires: email, password, first name, last name, phone number | 🟢         |
| AC-2                    | Email verification link sent on registration                                      | 🟢 `GET /auth/verify?token=...` endpoint; email sent on register; sets `user.isVerified`. Gate enforced: bookings blocked until verified (`BookingService`); listings blocked via `KycService.requireVerified()`. Google OAuth users auto-verified. |
| AC-3                    | Google OAuth login creates account on first use, links on subsequent uses         | 🟢         |
| AC-4                    | Duplicate email registration returns descriptive error                            | 🟢         |
| AC-5                    | User selects role (TENANT or LANDLORD) at registration                            | 🟢         |
| AC-6                    | Phone number verified via OTP (Twilio)                                            | 🔴 Planned |

### FR-101: Authentication 🟢

| ID                      | FR-101                                                                     |
| ----------------------- | -------------------------------------------------------------------------- | -------------------------------------------------- |
| **Description**         | Users authenticate via credentials or Google OAuth                         |
| **Acceptance Criteria** | Status                                                                     |
| AC-1                    | JWT access token issued with 15-minute expiry                              | 🟢                                                 |
| AC-2                    | Refresh token issued with 7-day expiry                                     | 🟢 (stored in httpOnly Secure/SameSite=Strict cookie) |
| AC-3                    | Failed login attempts: lock account after 5 failures                       | 🟢 `LoginAttemptService` (Redis-backed); configurable max attempts (default 5) and lock duration (default 30 min) via `AppProperties` |
| AC-4                    | Multi-device support: user can be logged in on web + mobile simultaneously | 🟢                                                 |
| AC-5                    | Logout invalidates refresh token server-side (DB)                          | 🟢 (DB-backed, not Redis)                          |
| AC-6                    | Password reset via email link                                              | 🟢 (forgot-password + reset-password routes exist) |

### FR-102: User Profile 🟢

| ID                      | FR-102                                        |
| ----------------------- | --------------------------------------------- | ----------------------------------- |
| **Description**         | Users manage their profile and settings       |
| **Acceptance Criteria** | Status                                        |
| AC-1                    | Editable fields: name, phone, bio, avatar     | 🟢                                  |
| AC-2                    | Avatar upload                                 | 🟢                                  |
| AC-3                    | Language preference persisted to localStorage | 🟢 (localStorage, not user profile) |
| AC-4                    | Users can delete their account (GDPR erasure with typed confirmation guard) | 🟢   |
| AC-5                    | Profile completeness score                    | 🟢 Color-coded progress bar in profile tab (emerald ≥80%, amber ≥50%, rose <50%); reads `User.profileCompleteness` from session. |

### FR-103: KYC Verification (Landlords) 🟢

| ID                      | FR-103                                                                                          |
| ----------------------- | ----------------------------------------------------------------------------------------------- | ---- |
| **Description**         | Landlord identity verification via Stripe Identity before publishing listings                   |
| **Acceptance Criteria** | Status                                                                                          |
| AC-1                    | `KycVerification` entity tracks verification status per landlord                               | 🟢   |
| AC-2                    | Webhook-driven status updates from Stripe Identity events                                       | 🟢   |
| AC-3                    | Publishing guard prevents listing creation until KYC is verified                               | 🟢   |
| AC-4                    | KYC status and session launch available in the workspace Settings tab                          | 🟢   |

---

## 5.2 Listing Management

### FR-200: Property Listings 🟢

| ID                      | FR-200                                                                                                    |
| ----------------------- | --------------------------------------------------------------------------------------------------------- | -------------------------------------- |
| **Description**         | Landlords create, edit, and manage property listings                                                      |
| **Acceptance Criteria** | Status                                                                                                    |
| AC-1                    | Required fields: title, description, property type, listing type, price, currency, address, city, country | 🟢                                     |
| AC-2                    | Property types: Apartment, House, Villa, Studio, Room, Office, Land, Warehouse, Co-working Space          | 🟢 (PropertyType enum)                 |
| AC-3                    | Listing types: Long-term Rent, Short-term Rent, Sale                                                      | 🟢 (ListingType enum)                  |
| AC-4                    | Optional fields: bedrooms, bathrooms, area (sqm), floor number, total floors, year built, parking spots   | 🟢                                     |
| AC-5                    | Media: images uploaded via multipart form                                                                 | 🟢                                     |
| AC-6                    | Images auto-resized to multiple sizes                                                                     | 🟢 `StorageService.uploadImageWithThumbnail()`: full-size capped at 1200px (imgscalr) + 400px thumbnail uploaded to `thumbs/` sub-prefix; `PropertyImage.thumbnailUrl` populated on every upload |
| AC-7                    | Amenities: multi-select from predefined list (categorized by AmenityCategory)                             | 🟢                                     |
| AC-8                    | Geolocation: lat/lng stored on property                                                                   | 🟢 `GeocodingService` auto-populates via Nominatim on create; powers ES geo-distance sort |
| AC-9                    | Availability calendar — landlord blocks dates                                                             | 🟢 (`property_availability` table V11; `POST /properties/{id}/availability/block`)  |
| AC-10                   | Pricing rules: WEEKEND, SEASONAL, LONG_STAY multipliers                                                   | 🟢 (`PricingRule` entity V33; `PricingService` + `PricingController`)              |
| AC-11                   | Listing status flow: PENDING → APPROVED / REJECTED (PropertyStatus enum)                                  | 🟢                                     |
| AC-12                   | Admin reviews and approves/rejects listings                                                               | 🟢                                     |

### FR-201: Vehicle Listings 🟢 Fully Implemented

| ID              | FR-201                                                                                                                                                                                   |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Description** | Vehicle rental listings — full CRUD with images, condition reports, availability, and split-payment bookings                                                                             |
| **Note**        | Vehicle entity, repository, service, controller (`VehicleV1Controller`), image uploads, condition reports, soft-delete, 10-state booking lifecycle (`VehicleBookingStatus`), split `/draft`+`/pay` endpoints all implemented. Frontend: vehicle detail page with Stripe Elements, vehicles listing page with dark hero + filter sidebar. |

### FR-202: Property Search & Discovery 🟢

| ID                      | FR-202                                                                                       |
| ----------------------- | -------------------------------------------------------------------------------------------- | ----------------------------------- |
| **Description**         | Users search and discover properties with filters and pagination                             |
| **Acceptance Criteria** | Status                                                                                       |
| AC-1                    | Search across properties using JPA Specifications (LIKE queries on title, description, city) | 🟢 (not Elasticsearch)              |
| AC-2                    | Geo-search with map view                                                                     | 🟢 (Elasticsearch geo-distance queries in `PropertySearchService`; Leaflet map in frontend) |
| AC-3                    | Property filters: type, listing type, price range, bedrooms, bathrooms, amenities, city      | 🟢                                  |
| AC-4                    | Sort options: price, newest                                                                  | 🟢                                  |
| AC-5                    | Search results: paginated                                                                    | 🟢 (ApiPageResponse)                |
| AC-6                    | Full-text search via Elasticsearch                                                           | 🟢 (fuzzy matching via `PropertySearchService` + `PropertyIndexConsumer` outbox relay) |
| AC-7                    | Autocomplete, saved searches, similar listings, comparison                                   | 🔴 Planned                          |

---

## 5.3 Booking Management

### FR-300: Create Booking 🟢

| ID                      | FR-300                                               |
| ----------------------- | ---------------------------------------------------- | --------------------------------- |
| **Description**         | Tenants book available properties                    |
| **Acceptance Criteria** | Status                                               |
| AC-1                    | Property booking: select check-in/check-out dates    | 🟢                                |
| AC-2                    | Payment processed via Stripe at booking time         | 🟢 (PaymentService)               |
| AC-3                    | Booking confirmation notification sent (push)        | 🟢                                |
| AC-4                    | Double-booking prevention via Redis distributed lock | 🟢 `RedissonClient` lock in `BookingService.createDraftBooking()` — SRS was misclassified |
| AC-5                    | Price breakdown with service fee / taxes             | 🟢 4-row breakdown widget on property detail: base (nights × rate), cleaning fee (conditional), 15% platform service fee, grand total. Computed signals in `property-detail.page.ts`. |

### FR-301: Manage Booking 🟢

| ID                      | FR-301                                             |
| ----------------------- | -------------------------------------------------- | ----------------------- |
| **Description**         | Landlords and tenants manage booking lifecycle     |
| **Acceptance Criteria** | Status                                             |
| AC-1                    | Landlord approves or rejects booking               | 🟢                      |
| AC-2                    | Tenant cancels booking                             | 🟢                      |
| AC-3                    | Auto-reject after timeout                          | 🟢 (`BookingService.autoRejectExpiredPendingBookings()` scheduled at 24h) |
| AC-4                    | Cancellation policies (Flexible, Moderate, Strict) | 🟢 (`Property.cancellationPolicy` field; accepted in `PropertyCreateRequest`) |
| AC-5                    | Booking history accessible with filters            | 🟢 (bookings list page) |
| AC-6                    | Booking modification (date changes)                | 🟢 Tenant submits date-change request via `POST /bookings/{id}/modify`; landlord approves via `PATCH /bookings/{id}/modify/approve` or rejects via `/modify/reject`; `PENDING_MODIFICATION` info card in `BookingDetailPanel` shows proposed dates/reason; tenant "Request Date Change" button + landlord "Approve/Reject" action buttons wired. |

### FR-302: Post-Booking 🟡

| ID                      | FR-302                                             |
| ----------------------- | -------------------------------------------------- | ---------- |
| **Description**         | Post-booking actions                               |
| **Acceptance Criteria** | Status                                             |
| AC-1                    | Tenant can review property after booking completes | 🟢         |
| AC-2                    | Review prompt sent automatically                   | 🟢 `NotificationService.sendReviewPromptNotification()` called in `BookingService.completeActiveBookings()` scheduler (runs at noon daily). Tenant receives in-app + push notification linking to the property. |
| AC-3                    | Damage claims, security deposits                   | 🔴 Planned |
| AC-4                    | Maintenance requests during active booking         | 🟢 (`MaintenanceRequest` entity; workspace Maintenance tab) |

---

## 5.4 Payment System

### FR-400: Payment Processing 🟡

| ID                      | FR-400                                                            |
| ----------------------- | ----------------------------------------------------------------- | --------------------------- |
| **Description**         | Stripe-based payment processing                                   |
| **Acceptance Criteria** | Status                                                            |
| AC-1                    | Payments processed via Stripe; HomeFlex never stores card numbers | 🟢 (PaymentService)         |
| AC-2                    | Stripe payment intent creation for bookings                       | 🟢                          |
| AC-3                    | Client secret returned to frontend; `confirmCardPayment` called   | 🟢 (v3.4)                   |
| AC-4                    | Escrow: funds held until service delivery                         | 🟢 (MANUAL-capture `PaymentIntent`; capture-on-approve via `EscrowService`) |
| AC-5                    | Payout to landlord with platform commission                       | 🟢 (Stripe Connect Express; `POST /payouts/connect/onboard`; 15% platform commission) |
| AC-6                    | Refund processing                                                 | 🟢 (`PaymentService.refundPayment()`; full refund on cancel, prorated on early checkout) |
| AC-7                    | Multi-currency support                                            | 🔴 Planned                  |
| AC-8                    | Invoice / receipt generation                                      | 🟢 (`Receipt` entity; receipts API; Finance tab PDF download links) |
| AC-9                    | Recurring monthly rent collection                                 | 🔴 Planned (Stripe Billing subscriptions)                  |

### FR-401: Financial Dashboard (Landlords) 🟢 Implemented

| ID              | FR-401                                                                                                                                           |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Description** | Landlord financial overview — Finance workspace tab shows earnings tiles (Total Earned, Available, Pending, Escrow), receipts list with PDF download, and Stripe Connect onboarding panel |

---

## 5.5 Communication

### FR-500: Real-Time Chat 🟢

| ID                      | FR-500                                                                    |
| ----------------------- | ------------------------------------------------------------------------- | ------------------------------ |
| **Description**         | Tenants and landlords communicate via real-time messaging                 |
| **Acceptance Criteria** | Status                                                                    |
| AC-1                    | Chat room created between tenant and landlord                             | 🟢                             |
| AC-2                    | Real-time message delivery via WebSocket (STOMP/SockJS, in-memory broker) | 🟢                             |
| AC-3                    | Message types: text                                                       | 🟢 (image/document planned)    |
| AC-4                    | Typing indicators                                                         | 🟢 (TypingNotification entity) |
| AC-5                    | Message history paginated                                                 | 🟢                             |
| AC-6                    | Push notification for new messages                                        | 🟢 (FCM)                       |
| AC-7                    | Chat room linked to specific property                                     | 🟢                             |
| AC-8                    | Chat available to registered users only                                   | 🟢                             |
| AC-9                    | Read receipts                                                             | 🟢 Single checkmark (delivered, brand-200) / double checkmark (read, emerald-300) SVG icons on sent messages in messages tab; driven by `Message.isRead`. |

### FR-501: Notifications 🟡

| ID                      | FR-501                                                                      |
| ----------------------- | --------------------------------------------------------------------------- | ---------- |
| **Description**         | Notification system                                                         |
| **Acceptance Criteria** | Status                                                                      |
| AC-1                    | Channels: in-app (Notification entity), push (FCM), email (Gmail SMTP)      | 🟢         |
| AC-2                    | SMS (Twilio), WhatsApp (Twilio)                                             | 🟢 (`TwilioSmsGateway` wired into `NotificationService` and `OtpService`; fires on booking lifecycle events) |
| AC-3                    | Notification types: BOOKING, CHAT, PROPERTY, SYSTEM (NotificationType enum) | 🟢         |
| AC-4                    | In-app notifications with unread count                                      | 🟢         |
| AC-5                    | Header bell badge reactively combines notification + message unread counts  | 🟢 (v3.4)  |
| AC-6                    | User configures notification preferences per channel                        | 🟡 Boolean flags per channel (`emailNotificationsEnabled`, `smsNotificationsEnabled`, `pushNotificationsEnabled`) on `User`; no per-event-type granularity |
| AC-7                    | Notification templates localized                                            | 🔴 Planned |

---

## 5.6 Administration

### FR-600: Admin Dashboard 🟢

| ID                      | FR-600                                                            |
| ----------------------- | ----------------------------------------------------------------- | ----------------------------- |
| **Description**         | Admins manage content, users, and platform health                 |
| **Acceptance Criteria** | Status                                                            |
| AC-1                    | Dashboard overview: total users, active listings, bookings, stats | 🟢 (StatsController)          |
| AC-2                    | Property moderation: approve/reject queue                         | 🟢 (AdminController)          |
| AC-3                    | User management: view, manage users                               | 🟢 (AdminController)          |
| AC-4                    | Report management: view reported listings, take action            | 🟢 (ReportedListing entity)   |
| AC-5                    | KYC management                                                    | 🟢 (Admin can view KYC status via user records; webhook-driven updates) |
| AC-6                    | Dispute resolution                                                | 🟢 (`DisputeController`; admin resolve endpoint; workspace Disputes tab) |
| AC-7                    | Analytics: user growth, booking trends, revenue charts            | 🟢 Admin dashboard rebuilt with KPI grid, CSS bar charts (properties-by-type, top-cities, bookings-by-status), and ranked top-viewed/favorited property lists |
| AC-8                    | System config: manage amenities, commission rates                 | 🟡 Amenities: full CRUD via admin page (`/admin/amenities`) backed by `GET/POST/PUT/DELETE /admin/amenities`; commission rates: config endpoint exists (`/admin/config/{key}`) but no dedicated UI yet. |
| AC-9                    | Audit log                                                         | 🔴 Planned                    |

### FR-601: Support Agent Tools 🔴 Planned

| ID              | FR-601                                                                     |
| --------------- | -------------------------------------------------------------------------- |
| **Description** | Support agent tooling — not yet implemented. No SUPPORT_AGENT role exists. |

---

## 5.7 Reviews & Trust

### FR-700: Reviews & Responses 🟢

| ID                      | FR-700                                          |
| ----------------------- | ----------------------------------------------- | --------------------------------- |
| **Description**         | Property review system                          |
| **Acceptance Criteria** | Status                                          |
| AC-1                    | Tenant reviews property after booking           | 🟢 (Review entity, ReviewService) |
| AC-2                    | Review fields: rating (1-5 stars), text comment | 🟢                                |
| AC-3                    | Category ratings (cleanliness, accuracy, etc.)  | 🟢 Six sub-rating fields on `Review` entity (cleanlinessRating, accuracyRating, communicationRating, locationRating, checkinRating, valueRating); frontend Reviews tab renders sub-ratings grid when any are present. |
| AC-4                    | Aggregate rating displayed on property          | 🟢                                |
| AC-5                    | Two-way reviews (landlord reviews tenant)       | 🟢 `POST /reviews` with `targetUserId` creates tenant review; `GET /reviews/tenant/{userId}` retrieves them; `POST /reviews/{id}/reply` for landlord public response |
| AC-6                    | Landlord can post a public response             | 🟢 `POST /reviews/{id}/reply` with `@PreAuthorize("hasRole('LANDLORD')")` |

### FR-701: Trust Score 🟢 Implemented

| ID              | FR-701                                                                                                              |
| --------------- | ------------------------------------------------------------------------------------------------------------------- |
| **Description** | `User.trustScore` field (default 5.0) via V19 migration. `ReviewService` recalculates on every review create and delete: landlord trust score = average rating across all their property reviews (`getAveragePropertyRatingByLandlordId`); tenant trust score = average rating across all their tenant reviews (`getAverageRatingByUserId`). Score persisted to `users.trust_score`. |

---

## 5.8 Document Management 🟢 Implemented

### FR-800: Digital Leases

| ID              | FR-800                                                                                                                                          |
| --------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| **Description** | `PropertyLease` entity with full lifecycle: generation (`POST /leases/booking/{id}/generate`), e-signing (`POST /leases/{id}/sign`), and listing (`GET /leases/my`). Stores PDF URL and blockchain TX hash stub. `BlockchainLeaseService` is a no-op placeholder. |

---

## 5.9 Maintenance Requests 🟢 Implemented

### FR-900: Maintenance

| ID              | FR-900                                                                                                                                                              |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Description** | `MaintenanceRequest` entity with status flow `OPEN → IN_PROGRESS → RESOLVED`. Full REST API. Workspace Maintenance tab lets tenants file requests (with property selector) and landlords update status. Image attachments via `MaintenanceRequestImage`. |

## 5.10 Implemented Features Not Previously in SRS 🟢

The following features were discovered during the v4.6 audit — they exist in the codebase but were absent from earlier SRS versions.

| Feature | Evidence | Notes |
|---|---|---|
| **Dynamic pricing rules** | `PricingRule` entity (V33 migration); `PricingService`; `PricingController` | WEEKEND, SEASONAL, LONG_STAY multiplier rules on top of base price |
| **Room types & room inventory** | `RoomType`, `RoomInventory`, `RoomTypeImage` entities (V35–V37 migrations) | Hotel-style room-level granularity within a property; occupancy calendar |
| **Booking audit log** | `BookingAuditLog` entity; populated on every state transition in `BookingService` | Full history: who triggered what action and when, with optional reason |
| **Booking state machine** | `BookingStateMachine` class; 10-state `BookingStatus` enum | Enforces valid transitions; `DRAFT → PAYMENT_PENDING → PENDING_APPROVAL → APPROVED → ACTIVE → COMPLETED` |
| **Instant Book path** | State machine supports `DRAFT → APPROVED` skip-approval transition | Not yet exposed in UI; groundwork in place |
| **Agency multi-tenancy** | `Agency` entity; `agencyRole` on `User` | White-label foundation; no UI yet |
| **Resilience4j on Stripe** | Circuit breaker + retry with exponential backoff on all Stripe API calls | 3 attempts, 500ms base; trips after 5 consecutive failures |
| **OTP via Twilio** | `OtpService` + `TwilioSmsGateway` | Phone OTP flow exists in service layer; not yet exposed in registration flow |
| **`BookingStateMachine` for vehicles** | `VehicleBookingStatus` 10-state enum; split `/draft`+`/pay` on `VehicleV1Controller` | Full parity with property booking workflow |

---

# 6. Non-Functional Requirements

## 6.1 Performance

| ID      | Requirement                                  | Target               |
| ------- | -------------------------------------------- | -------------------- |
| NFR-P1  | API response time (95th percentile)          | < 200ms              |
| NFR-P2  | Search query response time (95th percentile) | < 300ms              |
| NFR-P3  | Page load time (first contentful paint)      | < 1.5s               |
| NFR-P4  | Time to interactive                          | < 3s                 |
| NFR-P5  | WebSocket message delivery latency           | < 100ms              |
| NFR-P6  | Image upload processing (resize + CDN)       | < 5s                 |
| NFR-P7  | Concurrent users supported                   | 10,000+              |
| NFR-P8  | API throughput                               | 1,000 req/s per node |
| NFR-P9  | Database query time (95th percentile)        | < 50ms               |
| NFR-P10 | Elasticsearch query time (95th percentile)   | < 100ms              |

## 6.2 Availability & Reliability

| ID     | Requirement                    | Target                                                  |
| ------ | ------------------------------ | ------------------------------------------------------- |
| NFR-A1 | Platform uptime                | 99.9% (8.76h downtime/year)                             |
| NFR-A2 | Recovery Time Objective (RTO)  | < 15 minutes                                            |
| NFR-A3 | Recovery Point Objective (RPO) | < 1 minute                                              |
| NFR-A4 | Database backup frequency      | Continuous (point-in-time recovery)                     |
| NFR-A5 | Zero-downtime deployments      | Required                                                |
| NFR-A6 | Multi-AZ deployment            | Required for all data stores                            |
| NFR-A7 | Graceful degradation           | If Elasticsearch is down, fallback to PostgreSQL search |

## 6.3 Scalability

| ID     | Requirement        | Target                               |
| ------ | ------------------ | ------------------------------------ |
| NFR-S1 | Horizontal scaling | Auto-scale API from 2 to 50 nodes    |
| NFR-S2 | Database scaling   | Read replicas for read-heavy queries |
| NFR-S3 | Cache scaling      | Redis cluster mode with sharding     |
| NFR-S4 | Media storage      | Unlimited (S3)                       |
| NFR-S5 | Listing capacity   | 5M+ active listings                  |
| NFR-S6 | User capacity      | 1M+ registered users                 |
| NFR-S7 | Message throughput | 10K messages/second                  |

## 6.4 Security

| ID        | Requirement           | Target                                   | Status                        |
| --------- | --------------------- | ---------------------------------------- | ----------------------------- |
| NFR-SEC1  | Authentication        | JWT with httpOnly/Secure cookies         | 🟢 Implemented                |
| NFR-SEC2  | Encryption at rest    | AES-256-GCM for PII data                 | 🟢 Implemented                |
| NFR-SEC3  | Encryption in transit | TLS 1.3 for external connections         | 🟡 (Docker internal is HTTP)  |
| NFR-SEC4  | PCI DSS compliance    | Level 1 (via Stripe)                     | 🟢 (Stripe handles card data) |
| NFR-SEC5  | GDPR compliance       | Right to erasure, consent tools          | 🟢 Implemented                |
| NFR-SEC6  | Rate limiting         | Distributed bucket-4j (Redis)            | 🟢 Implemented                |
| NFR-SEC7  | Input validation      | Jakarta validation on DTOs               | 🟢 Implemented                |
| NFR-SEC8  | Infrastructure        | Zero-trust isolation (Internal network)  | 🟢 Implemented                |
| NFR-SEC9  | Secrets management    | Environment-only (Secrets Manager ready) | 🟢 Implemented                |
| NFR-SEC10 | Penetration testing   | Annual pentest                           | 🔴 Planned                    |
| NFR-SEC11 | Dependency scanning   | CVE scanning in CI (Dependabot)          | 🟢 Implemented                |

## 6.5 Accessibility

| ID       | Requirement           | Target                           |
| -------- | --------------------- | -------------------------------- |
| NFR-ACC1 | WCAG compliance       | Level AA (WCAG 2.2)              |
| NFR-ACC2 | Screen reader support | All interactive elements labeled |
| NFR-ACC3 | Keyboard navigation   | Full app navigable without mouse |
| NFR-ACC4 | Color contrast        | Minimum 4.5:1 ratio              |
| NFR-ACC5 | Responsive design     | Mobile-first, 320px to 4K        |

## 6.6 Maintainability

| ID     | Requirement         | Target                                              |
| ------ | ------------------- | --------------------------------------------------- |
| NFR-M1 | Code coverage       | > 80% (unit + integration)                          |
| NFR-M2 | API documentation   | Auto-generated OpenAPI 3.1                          |
| NFR-M3 | Code style          | Enforced via linters (Checkstyle, ESLint, Prettier) |
| NFR-M4 | Dependency updates  | Monthly security patch cycle                        |
| NFR-M5 | Database migrations | Versioned via Flyway                                |

---

# 7. Data Model & Database Design

## 7.1 Entity Relationship Summary 🟢

### Implemented Entities (16 JPA entities in `domain/entity/`)

```
┌────────────────┐       ┌──────────────┐       ┌──────────────┐
│     User       │1─────*│   Property   │1─────*│   Booking    │
│                │       │              │       │              │
│ id (Long)      │       │ id (Long)    │       │ id (Long)    │
│ email          │       │ landlord(FK) │       │ property(FK) │
│ firstName      │       │ title        │       │ tenant(FK)   │
│ lastName       │       │ description  │       │ status       │
│ role (enum)    │       │ price        │       │ checkIn      │
│ password       │       │ propertyType │       │ checkOut     │
│ phone          │       │ listingType  │       │ totalPrice   │
└────────────────┘       │ status       │       └──────────────┘
      │                  │ city/address │
      │                  │ bedrooms     │
      │                  │ bathrooms    │
      │                  │ amenities    │────* ┌──────────┐
      │                  └──────────────┘      │ Amenity  │
      │                        │               │ name     │
      │                   1────*               │ category │
      │              ┌──────────────┐          └──────────┘
      │              │PropertyImage │
      │              │PropertyVideo │
      │              └──────────────┘
      │
      │    ┌──────────┐    ┌───────────┐   ┌──────────────┐
      ├───*│ Review   │    │ ChatRoom  │   │ Notification │
      │    │ rating   │    │ property  │   │ user(FK)     │
      │    │ comment  │    │ tenant    │   │ type (enum)  │
      │    └──────────┘    │ landlord  │   │ read         │
      │                    └─────┬─────┘   └──────────────┘
      │                          │
      │    ┌──────────┐    ┌─────▼─────┐   ┌──────────────┐
      ├───*│ Favorite │    │ Message   │   │   FcmToken   │
      │    │ property │    │ content   │   │ user(FK)     │
      │    └──────────┘    │ sender    │   │ token        │
      │                    └───────────┘   └──────────────┘
      │
      ├───*┌────────────────┐  ┌──────────────────┐
      │    │ OAuthProvider  │  │ RefreshToken      │
      │    │ provider       │  │ token             │
      │    │ providerId     │  │ expiryDate        │
      │    └────────────────┘  └──────────────────┘
      │
      ├───*┌────────────────────┐  ┌─────────────────┐
           │ ReportedListing    │  │TypingNotification│
           │ property(FK)       │  │ chatRoom(FK)     │
           │ reporter(FK)       │  │ user(FK)         │
           │ reason             │  └─────────────────┘
           └────────────────────┘

Also: OutboxEvent (domain/event/) — stores domain events for future async processing
```

**Not implemented:** KycVerification, Document, MaintenanceRequest, Payment (as separate entity — payments handled via Stripe API directly)

**Skeleton implemented:** Vehicle entity exists in `com.homeflex.features.vehicle.domain.entity` with its own `vehicles` schema and Flyway migration (`V100__create_vehicles_table.sql`)

## 7.2 Key Design Decisions

### 7.2.1 Single Property Table (No Listing Abstraction) 🟢

Unlike the v2.0 SRS which proposed a `listings` base table with Property/Vehicle subtypes, the current implementation uses a single `properties` table. The Property entity contains all property-specific fields directly. There is no abstract `BaseListing` or table-per-subclass pattern.

### 7.2.2 Database Migrations

Flyway manages schema migrations in `src/main/resources/db/migration/`. Migrations are immutable and versioned (`V1__`, `V2__`, etc.). Hibernate `ddl-auto: validate` in production.

### 7.2.3 Audit Columns

Entities use `createdAt` and `updatedAt` timestamps (JPA `@CreationTimestamp`/`@UpdateTimestamp`). Soft delete and optimistic locking are not yet implemented on all entities.

---

# 8. API Design

## 8.1 API Conventions

| Convention     | Standard                                                                 |
| -------------- | ------------------------------------------------------------------------ |
| Base path      | `/api/v1/`                                                               |
| Format         | JSON (application/json)                                                  |
| Authentication | Bearer token in Authorization header                                     |
| Pagination     | `?page=0&size=20&sort=createdAt,desc`                                    |
| Error format   | `{ "timestamp", "status", "error", "message", "path", "fieldErrors[]" }` |
| Date format    | ISO 8601 (`2026-03-24T14:30:00Z`)                                        |
| ID format      | UUID v4                                                                  |
| Naming         | camelCase for JSON fields                                                |
| Versioning     | URI path (`/v1/`, `/v2/`)                                                |

## 8.2 Endpoint Summary

### Authentication

| Method | Endpoint                            | Description                          |
| ------ | ----------------------------------- | ------------------------------------ |
| POST   | `/api/v1/auth/register`             | Register new user                    |
| POST   | `/api/v1/auth/login`                | Login with credentials               |
| POST   | `/api/v1/auth/oauth/{provider}`     | Social login (google/apple/facebook) |
| POST   | `/api/v1/auth/refresh`              | Refresh access token                 |
| POST   | `/api/v1/auth/logout`               | Invalidate session                   |
| POST   | `/api/v1/auth/forgot-password`      | Request password reset               |
| POST   | `/api/v1/auth/reset-password`       | Reset password with token            |
| POST   | `/api/v1/auth/verify-email/{token}` | Verify email address                 |
| POST   | `/api/v1/auth/verify-phone`         | Verify phone via OTP                 |

### Listings (Polymorphic)

| Method | Endpoint                        | Description                                      |
| ------ | ------------------------------- | ------------------------------------------------ |
| GET    | `/api/v1/listings`              | Search all listings (properties + vehicles)      |
| GET    | `/api/v1/listings/{id}`         | Get listing detail (returns property or vehicle) |
| POST   | `/api/v1/listings/{id}/view`    | Increment view count                             |
| GET    | `/api/v1/listings/{id}/similar` | Get similar listings                             |
| POST   | `/api/v1/listings/{id}/report`  | Report listing                                   |
| GET    | `/api/v1/listings/comparison`   | Compare listings (?ids=a,b,c,d)                  |

### Properties

| Method | Endpoint                                   | Description                    |
| ------ | ------------------------------------------ | ------------------------------ |
| GET    | `/api/v1/properties`                       | Search properties with filters |
| GET    | `/api/v1/properties/{id}`                  | Get property detail            |
| POST   | `/api/v1/properties`                       | Create property listing        |
| PUT    | `/api/v1/properties/{id}`                  | Update property                |
| DELETE | `/api/v1/properties/{id}`                  | Soft-delete property           |
| POST   | `/api/v1/properties/{id}/images`           | Upload images                  |
| DELETE | `/api/v1/properties/{id}/images/{imageId}` | Delete image                   |
| PUT    | `/api/v1/properties/{id}/availability`     | Update availability calendar   |

### Vehicles

| Method | Endpoint                             | Description                  |
| ------ | ------------------------------------ | ---------------------------- |
| GET    | `/api/v1/vehicles`                   | Search vehicles with filters |
| GET    | `/api/v1/vehicles/{id}`              | Get vehicle detail           |
| POST   | `/api/v1/vehicles`                   | Create vehicle listing       |
| PUT    | `/api/v1/vehicles/{id}`              | Update vehicle               |
| DELETE | `/api/v1/vehicles/{id}`              | Soft-delete vehicle          |
| POST   | `/api/v1/vehicles/{id}/images`       | Upload images                |
| PUT    | `/api/v1/vehicles/{id}/availability` | Update availability calendar |
| POST   | `/api/v1/vehicles/{id}/condition`    | Submit condition report      |

### Bookings

| Method | Endpoint                            | Description                     |
| ------ | ----------------------------------- | ------------------------------- |
| POST   | `/api/v1/bookings`                  | Create booking                  |
| GET    | `/api/v1/bookings`                  | List my bookings (renter/owner) |
| GET    | `/api/v1/bookings/{id}`             | Get booking detail              |
| PUT    | `/api/v1/bookings/{id}/confirm`     | Owner confirms booking          |
| PUT    | `/api/v1/bookings/{id}/reject`      | Owner rejects booking           |
| PUT    | `/api/v1/bookings/{id}/cancel`      | Cancel booking                  |
| PUT    | `/api/v1/bookings/{id}/check-in`    | Confirm check-in                |
| PUT    | `/api/v1/bookings/{id}/check-out`   | Confirm check-out               |
| POST   | `/api/v1/bookings/{id}/dispute`     | Raise dispute                   |
| POST   | `/api/v1/bookings/{id}/maintenance` | Submit maintenance request      |

### Payments

| Method | Endpoint                       | Description                    |
| ------ | ------------------------------ | ------------------------------ |
| POST   | `/api/v1/payments/intent`      | Create payment intent (Stripe) |
| GET    | `/api/v1/payments`             | List my payments               |
| GET    | `/api/v1/payments/{id}`        | Get payment detail             |
| POST   | `/api/v1/payments/{id}/refund` | Request refund                 |
| GET    | `/api/v1/payouts`              | List my payouts (owner)        |
| GET    | `/api/v1/payouts/summary`      | Financial summary              |
| POST   | `/api/v1/webhooks/stripe`      | Stripe webhook receiver        |

### Chat

| Method | Endpoint                           | Description                      |
| ------ | ---------------------------------- | -------------------------------- |
| GET    | `/api/v1/chat/rooms`               | List my chat rooms               |
| POST   | `/api/v1/chat/rooms`               | Create/get chat room for listing |
| GET    | `/api/v1/chat/rooms/{id}/messages` | Get messages (paginated)         |
| POST   | `/api/v1/chat/rooms/{id}/messages` | Send message (REST fallback)     |
| PUT    | `/api/v1/chat/messages/{id}/read`  | Mark message as read             |

### WebSocket Endpoints

| Destination                   | Direction       | Description                        |
| ----------------------------- | --------------- | ---------------------------------- |
| `/ws`                         | Connect         | STOMP connection endpoint (SockJS) |
| `/app/chat.send`              | Client → Server | Send chat message                  |
| `/app/chat.typing`            | Client → Server | Typing indicator                   |
| `/topic/chat.{roomId}`        | Server → Client | Receive chat messages              |
| `/user/queue/notifications`   | Server → Client | Personal notifications             |
| `/user/queue/booking-updates` | Server → Client | Booking status changes             |

### Users, Reviews, Favorites, Notifications, Documents, Admin

_(Similar RESTful patterns — full endpoint list in Appendix A)_

---

# 9. Security Architecture

## 9.1 Authentication Flow 🟢

```
┌────────┐                    ┌─────────┐                    ┌────────────┐
│ Client │                    │   API   │                    │ PostgreSQL │
└───┬────┘                    └────┬────┘                    └─────┬──────┘
    │  POST /api/v1/auth/login     │                               │
    │  {email, password}           │                               │
    │─────────────────────────────▶│                               │
    │                              │ Validate credentials          │
    │                              │ Generate JWT (15min)          │
    │                              │ Generate Refresh Token (7d)   │
    │                              │──────────────────────────────▶│
    │                              │  Store refresh token in DB    │
    │  200 OK                      │◀──────────────────────────────│
    │  Body: {accessToken,         │                               │
    │         refreshToken}        │  (both in response body,      │
    │◀─────────────────────────────│   stored in localStorage)     │
    │                              │                               │
    │  GET /api/v1/properties      │                               │
    │  Header: Authorization:      │                               │
    │    Bearer <accessToken>      │                               │
    │─────────────────────────────▶│                               │
    │                              │ JwtAuthenticationFilter       │
    │  200 OK {properties}         │ validates token               │
    │◀─────────────────────────────│                               │
    │                              │                               │
    │  (15min later - expired)     │                               │
    │  POST /api/v1/auth/refresh   │                               │
    │  Body: {refreshToken}        │                               │
    │─────────────────────────────▶│                               │
    │                              │──────────────────────────────▶│
    │                              │  Validate + rotate token      │
    │  200 OK {newAccessToken,     │◀──────────────────────────────│
    │         newRefreshToken}     │                               │
    │◀─────────────────────────────│                               │
```

> **Note:** Tokens are stored in httpOnly `Secure` cookies (`ACCESS_TOKEN`, `REFRESH_TOKEN`) with `SameSite=Lax`. CSRF protection is enforced via `X-XSRF-TOKEN` header. `localStorage` is not used.

## 9.2 Authorization Model (RBAC) 🟢

| Resource          | TENANT                    | LANDLORD                 | ADMIN       |
| ----------------- | ------------------------- | ------------------------ | ----------- |
| Create property   | -                         | Write own                | Write any   |
| View property     | Read public               | Read own + public        | Read any    |
| Create booking    | Write own                 | -                        | -           |
| Manage booking    | Own bookings              | Own properties' bookings | Any         |
| Chat              | Participant only          | Participant only         | Any         |
| Favorites         | Own                       | Own                      | -           |
| Reviews           | Write own (after booking) | -                        | Read any    |
| Admin dashboard   | -                         | -                        | Full access |
| User management   | -                         | -                        | Full access |
| Report management | Submit                    | Submit                   | Full access |

## 9.3 Security Controls

| Control              | Implementation                                                                       | Status                         |
| -------------------- | ------------------------------------------------------------------------------------ | ------------------------------ |
| Input validation     | Jakarta Bean Validation on request DTOs                                              | 🟢                             |
| Output encoding      | Jackson auto-escaping                                                                | 🟢                             |
| SQL injection        | JPA parameterized queries (never string concat)                                      | 🟢                             |
| XSS prevention       | Angular default escaping + CSP header via Nginx                                      | 🟢                             |
| CSRF                 | `CookieCsrfTokenRepository` + `SpaCsrfTokenRequestHandler`; `X-XSRF-TOKEN` header   | 🟢                             |
| Rate limiting        | Redis Lua atomic INCR+EXPIRE; 100 req/min auth, 20 req/min public                   | 🟢                             |
| Secrets              | All secrets via environment variables; no hardcoded defaults for sensitive values    | 🟢 (Secrets Manager planned)  |
| Security headers     | Nginx: X-Frame-Options, X-Content-Type-Options, Referrer-Policy, CSP, Permissions   | 🟢                             |
| File upload          | Content-Type validation, UUID filename, size limits (10 MB)                         | 🟡 (magic-number check planned) |
| Dependencies         | GitHub Actions CI                                                                    | 🟡 (Snyk/Dependabot planned)  |
| OAuth bypass         | Dummy-token shortcut removed from AuthService                                        | 🟢                             |
| User enumeration     | Password-reset endpoint silently ignores unknown emails                              | 🟢                             |
| Timing attacks       | Metrics token compared with `MessageDigest.isEqual()` (constant-time)               | 🟢                             |
| Rate-limit spoofing  | RateLimitFilter uses last X-Forwarded-For value (set by Nginx, not the client)      | 🟢                             |
| Test data leakage    | `DataInitializer` annotated `@Profile("!prod")` — never runs in production          | 🟢                             |
| Swagger exposure     | Swagger UI/api-docs disabled globally; re-enabled only under `dev` profile           | 🟢                             |

## 9.4 Security Hardening Requirements

| ID     | Requirement                                              | Status                                        |
| ------ | -------------------------------------------------------- | --------------------------------------------- |
| SEC-01 | Secret management (no secrets in source control)         | 🟢 .env untracked; all secrets via env vars   |
| SEC-02 | CORS policy (explicit allow-list per environment)        | 🟡 Configured but needs per-env strictness    |
| SEC-03 | Token storage (httpOnly cookies, not localStorage)       | 🟢 httpOnly cookies with SameSite=Lax         |
| SEC-04 | CSRF defense                                             | 🟢 CookieCsrfTokenRepository + SPA handler    |
| SEC-05 | API abuse protection (rate limiting)                     | 🟢 Redis-backed rate limiter                  |
| SEC-06 | Auditability (security event logging)                    | 🔴 Planned                                    |
| SEC-07 | Soft delete on user-generated entities                   | 🔴 Planned                                    |
| SEC-08 | Optimistic locking on critical entities                  | 🔴 Planned                                    |
| SEC-09 | File upload safety (magic-number validation, AV scan)   | 🔴 Planned                                    |
| SEC-10 | Dependency CVE scanning in CI                            | 🔴 Planned (Snyk/Dependabot)                  |
| SEC-11 | Production profile enforced in Docker/CI deployments     | 🟢 docker-compose defaults to prod profile    |
| SEC-12 | Content Security Policy                                  | 🟢 Strict CSP header in Nginx                 |

## 9.5 Known Remaining Risks (Accepted / Planned)

| Risk                                    | Mitigation Plan                                           |
| --------------------------------------- | --------------------------------------------------------- |
| No HTTPS in Docker Compose (dev only)   | TLS terminated at AWS ALB/CloudFront in production (IaC)  |
| Backend port 8080 exposed in dev stack  | Production: backend only on internal network, no host port |
| File upload magic-number validation     | Apache Tika integration planned for next sprint           |
| No AV scanning on uploaded files        | ClamAV / AWS Macie integration planned                    |
| Dependency CVEs not scanned in CI       | Snyk GitHub Action planned                                |

---

# 10. Infrastructure & Deployment

## 10.1 Current Deployment Architecture 🟢

The application runs via Docker Compose with 6 services on a shared bridge network (`rental-network`):

```
docker-compose.yml
├── frontend       — Nginx serving Angular SPA, reverse proxies /api/* and /ws/* to backend
├── backend        — Spring Boot 4 JAR (Java 21, non-root user, port 8080)
├── db             — PostgreSQL 18 (database: homeflex)
├── redis          — Redis 8 (rate-limiting active; Redlock/caching planned)
├── rabbitmq       — RabbitMQ 4 (outbox relay consumer active; management UI on :15672)
├── elasticsearch  — Elasticsearch 9.1 (full-text search via outbox relay consumer)
├── logstash       — Logstash 9.1 (JSON log aggregation from backend, port 50000)
└── kibana         — Kibana 9.1 (log visualization, port 5601)

docker-compose.monitoring.yml
├── prometheus     — Prometheus 3.5 (scrapes /actuator/prometheus)
└── grafana        — Grafana 11.6 (JVM, HTTP, booking/payment dashboards)
```

- All services have Docker health checks
- Backend waits for db, redis, rabbitmq, and elasticsearch to be healthy before starting
- Multi-stage Dockerfiles with layer caching for fast rebuilds
- Non-root users in both frontend (nginx) and backend containers
- Nginx config: gzip, security headers, static asset caching (1yr), WebSocket proxy, SPA fallback

## 10.2 Target Deployment Architecture 🔴 Planned

Multi-region AWS deployment with ECS Fargate, RDS, ElastiCache, Amazon MQ, OpenSearch, S3, CloudFront, WAF. See `docs/ARCHITECTURE.md` for the full target architecture diagram.

### Planned Managed Service Mapping (AWS)

| Capability  | AWS Service                                          |
| ----------- | ---------------------------------------------------- |
| API compute | ECS Fargate (auto-scaling)                           |
| Database    | Amazon RDS for PostgreSQL (Multi-AZ + read replicas) |
| Cache       | ElastiCache Redis                                    |
| Messaging   | Amazon MQ (RabbitMQ engine)                          |
| Search      | Amazon OpenSearch Service                            |
| Storage     | Amazon S3 + CloudFront CDN                           |
| Security    | AWS WAF + Shield + Secrets Manager + KMS             |

## 10.3 Environment Strategy

| Environment    | Purpose                   | Infrastructure                    | Status     |
| -------------- | ------------------------- | --------------------------------- | ---------- |
| **local**      | Developer machine         | Docker Compose (all services)     | 🟢         |
| **dev**        | Integration testing       | Single ECS cluster, shared RDS    | 🔴 Planned |
| **staging**    | Pre-production validation | Production-mirror, synthetic data | 🔴 Planned |
| **production** | Live users                | Multi-region, auto-scaling, HA    | 🔴 Planned |

## 10.4 CI/CD Pipeline 🟡

Current pipeline (GitHub Actions — `.github/workflows/ci.yml`):

```
Code Push / PR → GitHub Actions:
  1. Build backend (Gradle)
  2. Build frontend (Angular)
```

Planned additions:

```
  3. Run unit + integration tests
  4. Security scan (Snyk + Trivy)
  5. Build Docker images → Push to ECR
  6. Deploy to staging → E2E tests
  7. Deploy to production (manual approval)
```

---

# 11. Scalability & Performance Strategy

> Most of this section describes target-state optimizations. Current implementation relies on PostgreSQL directly with no caching or read replicas.

## 11.1 Caching Strategy 🔴 Planned

Redis is provisioned in Docker Compose but not consumed. Planned caching:

| Data                    | Cache                    | TTL      | Invalidation                   |
| ----------------------- | ------------------------ | -------- | ------------------------------ |
| Property search results | Redis                    | 5 min    | On listing update event        |
| Listing detail          | Redis                    | 15 min   | On listing update event        |
| User profile            | Redis                    | 30 min   | On profile update              |
| Amenity/feature lists   | Redis                    | 24 hours | On admin change                |
| Stats/analytics         | Redis                    | 10 min   | Timer-based                    |
| Static assets           | Nginx (1yr) / CloudFront | 1 year   | Cache-busting hash in filename |

## 11.2 Database Optimization

**Implemented:**

- 🟢 **Connection pooling**: HikariCP (Spring Boot default)
- 🟢 **`@Transactional(readOnly=true)`** on read service methods

**Planned:**

- 🔴 Read replicas
- 🔴 Table partitioning (messages by month, bookings by year)
- 🔴 Composite indexes on Specification query columns
- 🔴 `@EntityGraph` for N+1 prevention

## 11.3 Frontend Performance

- 🟢 **Lazy loading**: All feature routes lazy-loaded
- **Virtual scrolling**: CDK Virtual Scroll for listing grids (render only visible items)
- **Image optimization**: WebP format, srcset for responsive sizes, lazy loading with `loading="lazy"`
- **Bundle budget**: < 200KB initial JavaScript (gzipped)
- **Service worker**: Cache API responses for offline browsing of recently viewed listings
- **Preloading**: `PreloadAllModules` strategy for instant route transitions

---

# 12. Monitoring & Observability 🔴 Planned

> Current monitoring is limited to Spring Boot Actuator endpoints and application logs. The full observability stack below is planned.

## 12.1 Metrics (Prometheus) — Planned

| Category           | Metrics                                                        |
| ------------------ | -------------------------------------------------------------- |
| **Business**       | Bookings/hour, revenue/day, new users/day, listings/day        |
| **API**            | Request rate, error rate, latency (p50, p95, p99), by endpoint |
| **Database**       | Query time, connection pool utilization, slow queries          |
| **Cache**          | Hit rate, miss rate, eviction rate, memory usage               |
| **Queue**          | Queue depth, consumer lag, processing time, DLQ size           |
| **JVM**            | Heap usage, GC pause time, thread count                        |
| **Infrastructure** | CPU, memory, disk I/O, network I/O                             |

## 12.2 Alerting Rules

| Alert              | Condition                  | Severity | Channel           |
| ------------------ | -------------------------- | -------- | ----------------- |
| High error rate    | 5xx rate > 1% for 5 min    | Critical | PagerDuty + Slack |
| API latency        | p95 > 500ms for 10 min     | Warning  | Slack             |
| Database CPU       | > 80% for 15 min           | Warning  | Slack             |
| Queue backlog      | > 1000 messages for 10 min | Warning  | Slack             |
| Disk usage         | > 85%                      | Warning  | Email             |
| Certificate expiry | < 30 days                  | Warning  | Email             |
| Payment failures   | > 5% failure rate          | Critical | PagerDuty + Slack |

## 12.3 Distributed Tracing

- **Correlation ID**: Every request gets a UUID correlation ID, propagated through HTTP headers, RabbitMQ message headers, and log entries
- **Trace context**: OpenTelemetry integration for end-to-end trace visibility across API → Database → Cache → Queue → Worker
- **Grafana Tempo**: Trace storage and visualization, linked from Grafana dashboards

---

# 13. Internationalization & Multi-Region

## 13.1 Language Support

| Language | Code | Region               | Status                         |
| -------- | ---- | -------------------- | ------------------------------ |
| English  | en   | Global               | 🟢 Primary (implemented)       |
| French   | fr   | France, North Africa | 🟢 Implemented (ngx-translate) |
| Arabic   | ar   | MENA                 | 🔴 Planned                     |
| Spanish  | es   | Spain, Latin America | 🔴 Planned                     |

## 13.2 Localization Scope

| Element            | Strategy                              | Status                      |
| ------------------ | ------------------------------------- | --------------------------- |
| UI strings         | ngx-translate JSON files per language | 🟢 (EN, FR)                 |
| Dates              | Locale-aware formatting               | 🟡 Basic                    |
| Currency           | Single currency (no conversion)       | 🟡 (multi-currency planned) |
| RTL support        | Arabic layout                         | 🔴 Planned                  |
| Email templates    | Localized per language                | 🔴 Planned                  |
| Push notifications | Localized templates                   | 🔴 Planned                  |

## 13.3 Multi-Currency 🔴 Planned

Currently, properties have a single price field with no currency conversion. Planned:

- Prices stored in the listing's native currency (landlord sets this)
- Display converted to tenant's preferred currency using exchange rates
- Booking charged in the listing's native currency (Stripe handles conversion)

---

# 14. Third-Party Integrations

| Service            | Provider                 | Purpose                                           | Status                                       |
| ------------------ | ------------------------ | ------------------------------------------------- | -------------------------------------------- |
| Payment processing | Stripe                   | Payment intents for bookings                      | 🟢 Implemented (PaymentService)              |
| Push notifications | Firebase Cloud Messaging | iOS + Android + web push                          | 🟢 Implemented (FirebaseNotificationGateway) |
| Email              | Gmail SMTP               | Transactional email (booking confirmations, etc.) | 🟢 Implemented (EmailService)                |
| OAuth              | Google                   | Social login                                      | 🟢 Implemented (OAuthProvider entity)        |
| CI/CD              | GitHub Actions           | Build and deploy                                  | 🟢 Implemented (.github/workflows/ci.yml)    |
| File storage       | AWS S3                   | Media storage                                     | 🟡 StorageService exists (dev fallback)      |
| KYC verification   | Stripe Identity          | Document + selfie verification                    | 🟢 Implemented (KycVerification entity, webhooks) |
| SMS                | Twilio                   | OTP, booking alerts                               | 🟢 Implemented (TwilioSmsGateway)            |
| WhatsApp           | Twilio                   | Rich notifications (MENA)                         | 🟢 Implemented (TwilioSmsGateway)            |
| OAuth              | Apple, Facebook          | Social login                                      | 🔴 Planned — UI shows "Soon" badge           |
| Email (production) | AWS SES                  | High-volume transactional email                   | 🔴 Planned (replace Gmail SMTP)              |
| CDN                | AWS CloudFront           | Global content delivery                           | 🔴 Planned                                   |
| Monitoring         | Prometheus + Grafana     | Metrics and dashboards                            | 🟢 Implemented (docker-compose.monitoring.yml) |
| Logging            | ELK Stack                | Centralized logs                                  | 🟢 Implemented (logstash + kibana services)  |
| Geocoding          | OpenStreetMap Nominatim  | Address → lat/lng                                 | 🟢 `GeocodingService` — no API key; auto-called in `PropertyService.createProperty()` when client omits coordinates |
| Maps               | Leaflet + OSM tiles      | Interactive maps                                  | 🔴 Planned                                   |
| WAF                | AWS WAF                  | API protection                                    | 🔴 Planned                                   |
| Secrets            | AWS Secrets Manager      | Credentials management                            | 🔴 Planned (env vars currently)              |

---

# 15. Mobile Strategy

## 15.1 Platform Support 🟢

| Platform | Build Tool                   | Status                              |
| -------- | ---------------------------- | ----------------------------------- |
| Web      | Angular CLI                  | 🟢 Primary platform                 |
| iOS      | Capacitor 8 + Xcode          | 🟢 Configured (capacitor.config.ts) |
| Android  | Capacitor 8 + Android Studio | 🟢 Configured                       |

## 15.2 Native Features

| Feature            | Capacitor Plugin              | Status                |
| ------------------ | ----------------------------- | --------------------- |
| Push notifications | @capacitor/push-notifications | 🟢 (CapacitorService) |
| Camera             | @capacitor/camera             | 🔴 Planned            |
| Geolocation        | @capacitor/geolocation        | 🔴 Planned            |
| Biometrics         | @capacitor/biometrics         | 🔴 Planned            |
| Share              | @capacitor/share              | 🔴 Planned            |

## 15.3 Offline Strategy 🔴 Planned

No offline support is currently implemented. All features require network connectivity.

---

# 16. Testing Strategy

## 16.1 Testing Pyramid

| Level            | Tool                                             | Status                         | What to Test                                                   |
| ---------------- | ------------------------------------------------ | ------------------------------ | -------------------------------------------------------------- |
| **Unit**         | JUnit 5 + Mockito (backend), Vitest (frontend)   | 🟢 Implemented                 | Business logic, validators, mappers, guards, security fixes    |
| **Architecture** | ArchUnit                                         | 🟢 Implemented                 | Architectural rules (controllers can't access repos directly)  |
| **Integration**  | Testcontainers (`BaseIntegrationTest`)           | 🟡 Scaffold present            | Repository queries, service interactions (not yet activated)   |
| **API**          | REST Assured                                     | 🔴 Planned                     | Request/response contracts, auth, validation                   |
| **Component**    | Vitest + Angular `TestBed`                       | 🟡 Partial (12 of ~54 files)   | Component creation, guard redirects, signal state              |
| **E2E**          | Playwright                                       | 🔴 Planned                     | Critical user flows                                            |
| **Performance**  | k6                                               | 🔴 Planned                     | Load testing                                                   |
| **Security**     | Manual audit (completed); OWASP ZAP + Snyk       | 🟡 Manual done; tooling planned| Vulnerability scanning                                         |

## 16.2 Current Test Coverage (as of v3.3)

### Backend (8 test classes, 37+ test methods)

| Class | Service Under Test | Methods | Notes |
|---|---|---|---|
| `AuthServiceTest` | `AuthService` | 9 | login, register, logout, password-reset enumeration, OAuth stubs |
| `BookingServiceTest` | `BookingService` | 8 | create, approve, reject, cancel, double-booking prevention |
| `ReviewServiceTest` | `ReviewService` | 4 | create, ownership enforcement |
| `MaintenanceServiceTest` | `MaintenanceService` | 3 | create, status update |
| `VehicleAvailabilityServiceTest` | `VehicleAvailabilityService` | 13 | availability windows, overlap detection |
| `ArchitectureGuardrailsTest` | Architecture rules | — | Controllers must not import repositories directly |
| `HomeFlexApplicationTests` | Spring context | 1 | Context loads (`@Tag("integration")`) |
| `BaseIntegrationTest` | Testcontainers base | — | Scaffold: PostgreSQL + RabbitMQ + Elasticsearch containers |

**Coverage gap**: 30 services and all 25 controllers have no tests. Recommended next sprint.

### Frontend (13 spec files)

| Spec | What is Tested |
|---|---|
| `app.spec.ts` | App shell renders |
| `api.client.spec.ts` | Property search query params |
| `convert-currency.pipe.spec.ts` | Pipe transforms |
| `session.store.spec.ts` | Login sets auth state and role signal |
| `admin.guard.spec.ts` | ADMIN allowed; TENANT + unauthenticated redirect to `/admin/login` |
| `home.page.spec.ts` | Component creation |
| `support.page.spec.ts` | FAQ array populated |
| `properties.page.spec.ts` | Component creation |
| `property-detail.page.spec.ts` | Component creation with 5 mocked APIs |
| `vehicles.page.spec.ts` | Component creation |
| `vehicle-detail.page.spec.ts` | Component creation |
| `workspace.page.spec.ts` | Component creation with 17 mocked APIs |
| `app-header.component.spec.ts` | Language/currency menus closed by default |

**Coverage gap**: Admin pages, auth pages, and all API services have no specs.

## 16.3 CI Pipeline (GitHub Actions)

```
.github/workflows/ci.yml
├── backend-test job
│   ├── Services: PostgreSQL 18, Redis 8, RabbitMQ 4, Elasticsearch 9.1.2
│   ├── Env: JWT_SECRET, ADMIN_PASSWORD, PII_ENCRYPTION_KEY (all required, no fallback)
│   ├── ./gradlew test --no-daemon
│   └── ./gradlew build -x test --no-daemon
└── frontend-build job
    ├── npm ci
    ├── npm run lint        (prettier --check .)
    ├── npx ng test --watch=false
    └── npm run build -- --configuration=production
```

**Test profile** (`application-test.yml`) overrides: in-memory MailHog SMTP, disabled Firebase/AWS/outbox relay, static JWT secret, admin password, and PII key — no real external services required to run unit tests.

---

# 17. Release & Rollout Strategy

## 17.1 Release Process

| Phase                       | Duration | Activities                                     |
| --------------------------- | -------- | ---------------------------------------------- |
| **Feature development**     | Varies   | Feature branches, PR reviews, CI checks        |
| **Code freeze**             | 2 days   | Only bug fixes merged to release branch        |
| **Staging validation**      | 2 days   | E2E tests, manual QA, performance tests        |
| **Canary release**          | 1 day    | 5% traffic to new version; monitor error rates |
| **Progressive rollout**     | 2 days   | 25% → 50% → 100% traffic                       |
| **Post-release monitoring** | 7 days   | Watch for regressions, collect user feedback   |

## 17.2 Feature Flags

- New features deployed behind feature flags (LaunchDarkly or AWS AppConfig)
- Enables A/B testing, gradual rollout, and instant kill switch
- Vehicle rental vertical launched behind feature flag for initial beta

## 17.3 Rollback Strategy

- **Automatic**: If 5xx error rate exceeds 2% within 30 minutes of deployment, automatically revert to previous container image
- **Manual**: One-click rollback via ECS service update to previous task definition
- **Database**: Flyway migrations are forward-only; rollback scripts prepared but applied manually

## 17.4 Implementation Phasing Roadmap

| Phase                              | Primary Outcomes                                                                                                     | Status       |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------- | ------------ |
| **Phase 0: Core Platform**         | User auth, property CRUD, bookings, chat, reviews, admin dashboard, Docker Compose deployment, CI pipeline           | 🟢 Complete  |
| **Phase 1: Hardening**             | httpOnly cookie auth, CSRF, strict CORS, secrets management, Redis rate limiting, booking overlap validation          | 🟢 Complete  |
| **Phase 2: Scale Foundations**     | Redis caching, RabbitMQ consumers, Elasticsearch search, Resilience4j, ELK observability, Prometheus/Grafana        | 🟢 Complete  |
| **Phase 3: Marketplace Expansion** | Stripe Connect payouts, landlord KYC (Stripe Identity), vehicle rental vertical, digital leases, Twilio SMS          | 🟢 Complete  |
| **Phase 4: Operational Maturity**  | Full security audit (10 vulns fixed), CI hardening, `security` + `folder-structure` AI skills, SRS/docs updated      | 🟢 Complete  |
| **Phase 5: Test Coverage**         | Controller tests, service integration tests, E2E with Playwright, Snyk CVE scanning in CI                           | 🔴 Next      |
| **Phase 6: Production Readiness**  | AWS ECS/RDS/ElastiCache/OpenSearch deployment via Terraform, CloudFront CDN, WAF, multi-region                       | 🔴 Planned   |

---

# 18. Risk Analysis

| #   | Risk                               | Probability | Impact   | Mitigation                                                                              |
| --- | ---------------------------------- | ----------- | -------- | --------------------------------------------------------------------------------------- |
| R1  | Payment processing outage (Stripe) | Low         | Critical | Queue payments for retry; show user-friendly error; monitor Stripe status page          |
| R2  | Database corruption/loss           | Very Low    | Critical | Multi-AZ RDS, point-in-time recovery, daily S3 backups, tested restore procedures       |
| R3  | DDoS attack                        | Medium      | High     | AWS Shield + WAF + CloudFront + rate limiting                                           |
| R4  | Data breach                        | Low         | Critical | Encryption at rest/transit, minimal PII storage, annual pentest, bug bounty program     |
| R5  | Fraudulent listings                | High        | Medium   | Admin moderation queue (🟢); KYC verification and ML-based fraud detection (🔴 planned) |
| R6  | Scalability bottleneck             | Medium      | High     | Load testing at 2x capacity, auto-scaling, horizontal architecture                      |
| R7  | Third-party API deprecation        | Low         | Medium   | Adapter pattern isolates external services; swap providers without core changes         |
| R8  | Regulatory compliance (GDPR/PCI)   | Medium      | High     | Data minimization, consent management, Stripe handles PCI, legal review quarterly       |
| R9  | Key personnel loss                 | Medium      | Medium   | Comprehensive documentation, pair programming, knowledge sharing sessions               |
| R10 | Vehicle rental legal complexity    | High        | Medium   | 🔴 Deferred — vehicle vertical not yet implemented                                      |

---

# 19. Appendices

## Appendix A: Complete API Endpoint List

_(Detailed OpenAPI spec to be auto-generated from code via SpringDoc)_

## Appendix B: Database Migration Strategy

- **Tool**: Flyway
- **Convention**: `V{version}__{description}.sql` (e.g., `V2.0.0__add_vehicles_table.sql`)
- **Rule**: Migrations are forward-only; never edit a released migration
- **Rollback**: Separate `U{version}__{description}.sql` scripts prepared but applied manually

## Appendix C: Environment Variables

### Currently Used

| Variable                     | Description                            | Example                              |
| ---------------------------- | -------------------------------------- | ------------------------------------ |
| `SPRING_DATASOURCE_URL`      | PostgreSQL JDBC URL                    | `jdbc:postgresql://db:5432/homeflex` |
| `SPRING_DATASOURCE_USERNAME` | DB username                            | `postgres`                           |
| `SPRING_DATASOURCE_PASSWORD` | DB password                            | `(secret)`                           |
| `JWT_SECRET`                 | JWT signing key                        | `(secret)`                           |
| `STRIPE_API_KEY`             | Stripe secret key                      | `sk_test_...`                        |
| `GOOGLE_CLIENT_ID`           | Google OAuth client ID                 | `...apps.googleusercontent.com`      |
| `FIREBASE_*`                 | Firebase config for push notifications | `(service account JSON)`             |
| `SPRING_MAIL_*`              | Gmail SMTP config                      | `smtp.gmail.com`                     |

### Planned (not yet used)

| Variable                                      | Description                |
| --------------------------------------------- | -------------------------- |
| `REDIS_URL`                                   | Redis connection string    |
| `RABBITMQ_URL`                                | RabbitMQ connection string |
| `ELASTICSEARCH_URL`                           | Elasticsearch URL          |
| `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` | AWS credentials            |
| `S3_BUCKET_NAME`                              | S3 bucket for media        |
| `TWILIO_ACCOUNT_SID` / `TWILIO_AUTH_TOKEN`    | Twilio credentials         |

## Appendix D: Glossary of Domain Terms

| Term             | Definition                                                            |
| ---------------- | --------------------------------------------------------------------- |
| **Property**     | A real estate asset posted for rental by a landlord                   |
| **Booking**      | A reservation of a property by a tenant with check-in/check-out dates |
| **Landlord**     | A user with role `LANDLORD` who lists properties                      |
| **Tenant**       | A user with role `TENANT` who searches and books properties           |
| **Admin**        | A user with role `ADMIN` who moderates the platform                   |
| **Outbox Event** | A domain event written to DB for future async processing              |

## Appendix E: Source of Truth References

- `rental-backend/build.gradle` — Backend runtime/tooling (Gradle + Java 21 + Spring Boot 4)
- `homeflex-web/package.json` — Frontend runtime/tooling (Angular 21 + Ionic 8 + Tailwind 4)
- `docker-compose.yml` — Service orchestration and infrastructure
- `CLAUDE.md` — AI assistant context with current package structure
- `docs/ARCHITECTURE.md` — Architecture diagrams and system documentation

---

_End of Document — v2.1 (2026-03-29)_

**Completed:**

1. Core platform: auth, properties, bookings, chat, reviews, admin, notifications
2. Docker Compose deployment (6 services)
3. GitHub Actions CI pipeline
4. Stripe payment integration
5. Firebase push notifications
6. Google OAuth

**Next Steps:**

1. Harden security (httpOnly tokens, strict CORS, secrets management)
2. Wire Redis caching and rate limiting
3. Implement RabbitMQ event consumers for outbox events
4. Integrate Elasticsearch for advanced property search
5. Set up observability (Prometheus + Grafana)
6. Plan AWS infrastructure (ECS Fargate, RDS, managed services)
