# Changelog

## 2026-03-24 - DDD + EDA Full Refactor Drive

### Added
- DDD scaffolding and migration guardrails (`domain`, `application`, `api/v1`, `infrastructure`, `shared`).
- API v1 compatibility controllers for auth, booking, and property.
- Outbox event foundation for event-driven architecture.
- Architecture ADRs and architecture guardrail documentation.

### Changed
- Security hardening (strict CORS + security headers + secrets externalization).
- Booking overlap validation and initial optimistic locking.
- Property delete flow moved to soft-delete baseline.
- Frontend websocket ownership converged on a single gateway service.

### Completed
- Converted core backend DTOs to Java records.
- Switched key mapping paths to MapStruct (`UserMapper`, `PropertyMapper`, `BookingMapper`).
- Retired legacy auth/booking/property controllers in favor of `/api/v1`.
- Added architecture guardrails (ArchUnit baseline + ADR documentation).
- Added outbox/event scaffolding and domain-layer exception baseline.

### DTO/Mapping Refactor Update
- Converted core DTOs to Java records:
  - `UserDto`, `AmenityDto`, `PropertyImageDto`, `PropertyVideoDto`, `PropertyDto`, `BookingDto`.
- Added MapStruct and introduced generated mappers:
  - `UserMapper`, `PropertyMapper`, `BookingMapper`.
- Removed manual mapper methods from:
  - `PropertyService`
  - `BookingService`

### Legacy Architecture Removal (in progress)
- Removed legacy layer controllers where v1 replacements exist:
  - `controller/AuthController.java`
  - `controller/BookingController.java`
  - `controller/PropertyController.java`

### Record DTO Expansion
- Converted additional DTOs to records:
  - `FavoriteDto`, `NotificationDto`, `MessageDto`, `ChatRoomDto`, `ReviewDto`, `ReportDto`, `TopPropertyDto`, `AuthResponse`, `AnalyticsDto`
- Updated service constructors/mapping calls to instantiate record DTOs directly where mapper coverage is pending.

### Status Note
- Backend compile path is green after record + mapper migration.
- Frontend migration remains partially blocked by deeper Angular/Ionic compatibility issues and requires a dedicated stabilization pass.
