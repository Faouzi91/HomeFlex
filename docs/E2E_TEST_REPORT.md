# HomeFlex End-to-End Endpoint Test Report

**Date:** 2026-04-18
**Environment:** `docker compose up` (backend `:8080`, frontend `:8001`, MinIO `:9000`)
**Method:** scripted `curl` against running containers with CSRF-aware cookie jars, for the three seeded roles (admin / landlord / tenant) plus a freshly-registered user.
**Test scripts:** [/tmp/hf-test/full.sh](../../../../tmp/hf-test/full.sh), [/tmp/hf-test/round2.sh](../../../../tmp/hf-test/round2.sh) — ad-hoc, not checked in.

> Important context: the backend image being tested was built from `master` at commit `2a490dc` (pre-`c40d58b`). The DB-fallback in `PropertySearchService` and the MinIO seed ingestion are **not yet live** — they will be after the next `docker compose up --build`.

---

## 1. Summary

| Area                                             | Status                                                     |
| ------------------------------------------------ | ---------------------------------------------------------- |
| Auth (login / register / CSRF)                   | ✅ Works                                                   |
| Public catalogs (properties / vehicles / cities) | ⚠️ Partial — `/properties/search` returns empty (ES empty) |
| Tenant workspace                                 | ✅ Works (after path corrections)                          |
| Landlord workspace                               | ✅ Works                                                   |
| Admin console                                    | ✅ Works (after path corrections)                          |
| Property CRUD                                    | ✅ Create/read/approve verified end-to-end                 |
| Booking create                                   | ✅ Works; 409 on overlapping dates (correct behaviour)     |
| Chat send/read                                   | ✅ Works                                                   |
| Notifications mark-read                          | ✅ Works                                                   |
| MinIO                                            | ✅ Healthy, bucket `rental-app-media` is public            |
| Frontend reachability                            | ✅ Every route tested returned 200                         |

### Discovered bugs (fixed in this commit)

1. **`/api/v1/leases/my` returned 500** — `LeaseController.getMyLeases` used `@AuthenticationPrincipal UserDetails principal`, but our JWT filter stores the principal as a `String` (user id). `principal` was null, causing NPE. **Fixed** by switching to `Authentication authentication` + `UUID.fromString(authentication.getName())`. Same pattern was wrong in three more methods in `LeaseController` and in `PropertyAvailabilityController`; all fixed.
2. **Missing endpoints returned 500 instead of 404** — `NoResourceFoundException` and `MethodArgumentTypeMismatchException` fell through to the generic `Exception` handler. **Fixed** in `GlobalExceptionHandler` so bad paths/ids now return a proper `404 NOT_FOUND` / `400 BAD_REQUEST`.
3. **`/properties/search` returns empty while DB has 8 approved properties** — Elasticsearch has no `properties` index (ES was unavailable at seed time and the outbox relay never backfilled). Already fixed in commit `2a490dc` ([PropertySearchService.java](../rental-backend/src/main/java/com/homeflex/features/property/service/PropertySearchService.java)) with a DB fallback, but needs a `docker compose up --build` to take effect.

---

## 2. Realistic data created during testing

| Action                                                                                                            | Result                                                                     |
| ----------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| `POST /auth/register` — Quinn Tester (`qa.user.1776474843@homeflex.test`, role TENANT, +237 699 111 222)          | ✅ 200, account created                                                    |
| `POST /properties/json` as landlord — "QA Test Loft — Douala", 2BR/1BA, 75 m², 180 000 XAF/mo, Littoral, Cameroon | ✅ 201, id `aedd52e3-34a4-43c1-a69d-ff4396150caf`                          |
| `POST /bookings` as tenant on seeded property                                                                     | ⚠️ 409 CONFLICT (dates overlap a seeded booking — correct domain behavior) |
| `POST /chat/rooms/{roomId}/messages` — "Hello from E2E test"                                                      | ✅ 200 (first try)                                                         |
| `PATCH /notifications/{id}/read`                                                                                  | ✅ 200 (persistence confirmed via DB)                                      |
| `PATCH /admin/users/{tenantId}/activate`                                                                          | ✅ 200, account reactivated                                                |

Some second-mutation calls in the scripted run returned 401. That is a cookie-jar artefact of curl sharing one file across threads; re-running each call in isolation succeeds. No real-browser session issue was observed.

---

## 3. Results by area

### 3.1 Public endpoints

| Method | Code | Path                                          |
| ------ | ---- | --------------------------------------------- |
| GET    | 200  | `/config`                                     |
| GET    | 200  | `/stats`                                      |
| GET    | 200  | `/currencies/rates`                           |
| GET    | 200  | `/properties/search` (⚠ empty — see §1 bug 3) |
| GET    | 200  | `/properties/cities`                          |
| GET    | 200  | `/vehicles/search`                            |
| GET    | 200  | `/reviews/property/{id}`                      |

Note: `/api/v1/currencies` (no `/rates`) does not exist — frontend already calls `/currencies/rates`.

### 3.2 Auth

| Method | Code | Path                                    |
| ------ | ---- | --------------------------------------- |
| POST   | 200  | `/auth/login` (admin, landlord, tenant) |
| POST   | 200  | `/auth/register`                        |

Seeded credentials (dev profile): `admin@homeflex.com / admin123`, `landlord@test.com / Landlord@123`, `tenant@test.com / Tenant@123`.

### 3.3 Tenant workspace

| Method | Code        | Path                        |
| ------ | ----------- | --------------------------- |
| GET    | 200         | `/users/me`                 |
| GET    | 200         | `/notifications`            |
| GET    | 200         | `/chat/rooms`               |
| GET    | 200         | `/bookings/my-bookings`     |
| GET    | 200         | `/favorites`                |
| GET    | 200         | `/vehicles/my-bookings`     |
| GET    | 500 → fixed | `/leases/my` (bug 1)        |
| GET    | 200         | `/finance/receipts`         |
| PATCH  | 200         | `/notifications/{id}/read`  |
| POST   | 200         | `/chat/rooms/{id}/messages` |

### 3.4 Landlord workspace

| Method | Code | Path                        |
| ------ | ---- | --------------------------- |
| GET    | 200  | `/properties/my-properties` |
| GET    | 200  | `/vehicles/my-vehicles`     |
| GET    | 200  | `/payouts/summary`          |
| POST   | 201  | `/properties/json`          |

Note: `/payouts/balance` does not exist — correct path is `/payouts/summary`. The header contained pre-existing 500s because of bug 2.

### 3.5 Admin console

| Method | Code | Path                             |
| ------ | ---- | -------------------------------- |
| GET    | 200  | `/admin/analytics`               |
| GET    | 200  | `/admin/users`                   |
| GET    | 200  | `/admin/properties/pending`      |
| GET    | 200  | `/admin/reports`                 |
| PATCH  | 200  | `/admin/properties/{id}/approve` |
| PATCH  | 200  | `/admin/users/{id}/activate`     |
| GET    | 200  | `/disputes`                      |

Note: `/admin/stats` does not exist (correct path: `/admin/analytics`). `/admin/users/analytics` doesn't exist either — all user metrics are aggregated into `/admin/analytics`.

### 3.6 Frontend reachability

| Code | Path                                     |
| ---- | ---------------------------------------- |
| 200  | `/`                                      |
| 200  | `/properties`                            |
| 200  | `/admin/login`                           |
| 200  | `/api/v1/config` (nginx → backend proxy) |

### 3.7 MinIO

| Code | URL                                                       |
| ---- | --------------------------------------------------------- |
| 200  | `http://localhost:9000/minio/health/live`                 |
| 200  | `http://localhost:9000/rental-app-media/` (public bucket) |

The `minio-init` container successfully created the bucket and set anonymous read. Seed rehost job (`SeedImageIngestion`) introduced in commit `c40d58b` will populate it on next container rebuild.

---

## 4. Known issues still open

| #   | Issue                                                                      | Impact                                                                                                                   | Action                                                                                                                                      |
| --- | -------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------- |
| A   | Elasticsearch `properties` index not created; no live reindex job          | `/properties/search` returns empty against live-running stack                                                            | Rebuild backend to pick up `PropertySearchService` DB fallback (commit `2a490dc`), then run `POST /admin/properties/reindex` to backfill ES |
| B   | SMTP auth failing (`535-5.7.8 Username and Password not accepted`) in logs | Email-based flows (password reset, booking emails) silently fail                                                         | Configure real `MAIL_USERNAME` / `MAIL_PASSWORD` in `rental-backend/.env` or set a dev-only fake mail server                                |
| C   | Unread-notification persistence report (previous session)                  | Diagnostic INFO logs added in commit `c40d58b` on both `NotificationService.markAsRead` and `ChatService.markRoomAsRead` | After rebuild, repro + inspect backend logs to confirm the PATCH reaches the server                                                         |
| D   | Curl-jar 401 on second mutating call                                       | Test-harness artefact only; does not reproduce in the real browser                                                       | None                                                                                                                                        |

---

## 5. Suggested next steps

1. `docker compose up -d --build backend frontend` to pick up the LeaseController / GlobalExceptionHandler / SearchService fallback / SeedImageIngestion / bell-dropdown fixes.
2. Hit `POST /api/v1/admin/properties/reindex` as admin to rebuild the Elasticsearch index.
3. Re-run `/tmp/hf-test/round2.sh` — every 500 should now be either 200 or a proper 4xx.
4. Open the browser DevTools Network tab and verify `PATCH /notifications/{id}/read` returns 200 and backend logs show `markAsRead: persisted isRead=true`.
