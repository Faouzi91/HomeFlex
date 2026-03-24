# HomeFlex Migration Status

This document tracks the current migration state after the DDD/EDA refactor pass.

## Current Snapshot (March 2026)

- Backend build migrated from Maven to Gradle and aligned to Java 21.
- DDD module scaffolding and `/api/v1` entry points are in place.
- Core DTOs are converted to Java records.
- MapStruct is adopted for key mapping flows.
- Legacy auth/booking/property controllers were removed.
- Frontend has partial architectural alignment but still needs stabilization for framework compatibility.

## What Is Completed

### Backend
- Build system: Gradle wrapper in use.
- Security hardening baseline: centralized CORS + security headers + env-based secrets.
- Domain and application layering introduced (`domain`, `application`, `api/v1`, `shared`, `infrastructure`).
- Outbox/event scaffolding added for EDA evolution.
- Optimistic locking and soft-delete baseline introduced in core entities.

### Frontend
- Initial standalone-first and signal-based structure introduced.
- WebSocket ownership converged toward a single gateway path.
- Route configuration centralized for migration toward standalone routing.

## What Still Needs Work

- Frontend dependency/runtime compatibility stabilization (Angular/Ionic alignment).
- Final cleanup of remaining legacy patterns in untouched modules.
- Full regression test pass across backend + frontend together.

## Build Commands

### Backend
```bash
cd rental-backend
./gradlew build
./gradlew bootRun
```

### Frontend
```bash
cd rental-app-frontend
npm install
npm run build
npm start
```

## Verification Checklist

- [ ] `rental-backend`: `./gradlew build` passes
- [ ] `rental-backend`: `./gradlew test` passes
- [ ] `rental-app-frontend`: `npm run build` passes
- [ ] End-to-end flow works on `/api/v1` endpoints
- [ ] No sensitive secrets committed in config files

## Notes

- This file intentionally reflects current status and not a "done" declaration.
- For detailed change history, refer to `CHANGELOG.md`.

**Last Updated**: 2026-03-24
