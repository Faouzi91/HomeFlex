# HomeFlex Web Dashboard

Modern web frontend for the HomeFlex rental marketplace, built with **Angular 21** and **Tailwind CSS 4**.

## Key Features

- **Unified Workspace**: Single dashboard for both tenants (bookings, favorites, chat) and landlords (listings, approvals, availability).
- **Property Availability**: Interactive calendar for landlords to manage blocked dates and view reservations.
- **Digital Leases**: End-to-end lease lifecycle management—generation for landlords and electronic signing for tenants.
- **Stripe Integration**: Payout summaries, Connect account onboarding, and secure payment flows.
- **Identity (KYC)**: Landlord identity verification status tracking via Stripe Identity.
- **Real-time Communication**: WebSocket-based chat with typing indicators and notification alerts.

## Tech Stack

- **Framework**: Angular 21 (Zone-less rendering)
- **Styling**: Tailwind CSS 4 + PostCSS
- **State Management**: NgRx Signal Store (`@ngrx/signals`)
- **API Client**: Reactive `HttpClient` with cookie-based JWT interceptors
- **Icons & UI**: Heroicons + Lucide (integrated as SVG)

## Development

Requires Node.js 20+.

```bash
npm install
npm start                      # Dev server on port 4200
npm run build                  # Production build
npm test                       # Unit tests (Vitest)
```

The application uses an Nginx-ready Docker configuration for production deployment.
