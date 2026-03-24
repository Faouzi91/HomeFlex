# HomeFlex Architecture Guardrails

## Backend
- New features must be implemented under DDD targets:
  - `domain/*`
  - `application/*`
  - `api/*`
  - `infrastructure/*`
  - `shared/*`
- New REST endpoints should default to `/api/v1/*`.
- Legacy `controller/service/repository/utils` changes are allowed only for migration bridge code.

## Frontend
- New features should prefer standalone components and route-based composition.
- `WebSocketService` is the single owner of STOMP lifecycle.
- New global state should be added via `core/state/*`, not ad-hoc service subjects.
- New NgModules require explicit migration exception in PR description.
