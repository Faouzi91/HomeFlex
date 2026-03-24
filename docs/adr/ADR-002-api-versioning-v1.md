# ADR-002: API Versioning with `/api/v1`

## Status
Accepted

## Context
Legacy endpoints are under `/api/*`. SRS requires `/api/v1/*` as the stable contract baseline.

## Decision
Introduce parallel v1 controllers under `api.v1` while keeping legacy routes temporarily.

## Consequences
- Frontend can migrate incrementally.
- Contract tests can target v1 without a big-bang cutover.
- Legacy route decommissioning can happen after adoption milestones.
