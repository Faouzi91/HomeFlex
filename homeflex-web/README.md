# HomeFlex Web Dashboard

Modern web frontend for the HomeFlex rental marketplace, built with **Angular 21** and **Tailwind CSS 4**.

## Workspace Tabs

| Tab                | Route                      | Roles            |
| ------------------ | -------------------------- | ---------------- |
| Overview           | `/workspace/overview`      | All              |
| Favorites          | `/workspace/favorites`     | All              |
| Bookings           | `/workspace/bookings`      | All              |
| Messages           | `/workspace/messages`      | All              |
| Alerts             | `/workspace/notifications` | All              |
| Settings           | `/workspace/profile`       | All              |
| Finance / Receipts | `/workspace/finance`       | All              |
| Disputes           | `/workspace/disputes`      | All              |
| Insurance          | `/workspace/insurance`     | All              |
| Hosting            | `/workspace/hosting`       | Landlord / Admin |
| Work Orders        | `/workspace/maintenance`   | Landlord / Admin |

## Key Features

- **Unified Workspace**: Single dashboard for tenants (bookings, favorites, chat) and landlords (listings, approvals, availability, payments).
- **Property & Vehicle Availability**: Landlord calendar UI for blocking dates and viewing reservations.
- **Digital Leases**: End-to-end lease lifecycle — generation and electronic signing.
- **Stripe Payments**: Property booking via Stripe Elements (MANUAL-capture escrow flow). Payout summary and Connect onboarding in Hosting → Payments.
- **Stripe Connect**: Conditional "Connect Stripe" banner in Finance tab only shown when account is not yet connected.
- **Identity (KYC)**: Landlord Stripe Identity verification status and session launch from Settings.
- **Real-time Chat**: WebSocket-based chat with per-room unread count badges in the sidebar.
- **Insurance Plans**: Tenant and landlord protection plans surfaced from the Insurance tab.
- **Disputes**: Structured dispute filing (6 reason categories) from the booking detail panel; list view in the Disputes tab.
- **Maintenance**: Work order creation and status tracking. Property selector dropdown for landlords.
- **GDPR**: Data export (JSON download) and account erasure with typed-confirmation guard.
- **Admin Console**: Separate login, dashboard, user management, property approvals, reports.

## Tech Stack

- **Framework**: Angular 21 (standalone components, signal-based change detection)
- **Styling**: Tailwind CSS 4 + PostCSS
- **State Management**: NgRx Signal Store (`@ngrx/signals`)
- **API Layer**: Domain-specific services (`BookingApi`, `PropertyApi`, etc.) backed by `HttpClient` with cookie-based JWT + CSRF
- **Reactive Patterns**: All subscriptions use `takeUntilDestroyed()` — no manual `ngOnDestroy` unsubscription

## Development

Requires Node.js 20+.

```bash
npm install
npm start          # Dev server on port 4200
npm run build      # Production build
npm test           # Unit tests (Vitest)
npm run lint       # Prettier check
```

The application uses an Nginx Docker configuration for production deployment.
