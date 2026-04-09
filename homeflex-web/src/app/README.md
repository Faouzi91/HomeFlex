# App Structure

This frontend is organized so the directory tree explains the architecture:

- `core/`
  Shared application-wide building blocks.
  Use this for API access, global state, domain models, and reusable utilities.
- `shell/`
  The application frame.
  Owns the global header, navigation, and the router outlet.
- `shared/`
  Reusable UI pieces that are not tied to a single business domain.
- `features/`
  Product domains.
  Each feature owns its pages and should grow with its own components, services, and state when needed.

Current feature boundaries:

- `features/marketing`
  Landing and public brand experience.
- `features/auth`
  Login, registration, and password recovery.
- `features/properties`
  Property discovery and property detail flows.
- `features/vehicles`
  Vehicle discovery and vehicle detail flows.
- `features/workspace`
  Authenticated user workspace for bookings, messaging, profile, hosting, and admin tools.

Placement rules:

- Put cross-cutting code in `core`, not inside a feature.
- Put domain-specific UI close to its feature.
- Put generic UI in `shared/ui`.
- Keep routes pointing to feature entry pages, not to random flat files.
