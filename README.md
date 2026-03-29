# HomeFlex

A full-stack real estate rental platform where tenants can search and book properties, landlords can list and manage them, and admins oversee the entire marketplace. Supports real-time chat, Stripe payments, push notifications, and mobile deployment.

## Tech Stack

| Layer         | Technology                                       |
| ------------- | ------------------------------------------------ |
| Frontend      | Angular 21, Ionic 8, TailwindCSS, TypeScript 5.9 |
| Backend       | Spring Boot 4, Java 21, Gradle                   |
| Database      | PostgreSQL 16, Flyway migrations                 |
| Cache         | Redis 7                                          |
| Messaging     | RabbitMQ 3                                       |
| Search        | Elasticsearch 9                                  |
| Mobile        | Capacitor 8 (Android / iOS)                      |
| Auth          | JWT (access + refresh tokens), Google OAuth      |
| Payments      | Stripe                                           |
| Notifications | Firebase Cloud Messaging                         |
| CI            | GitHub Actions                                   |

## Features

- **Property search** with filters (city, price range, type, bedrooms, bathrooms, amenities) and pagination
- **Role-based access**: Tenant, Landlord, Admin
- **Booking system** with approve / reject / cancel workflow
- **Real-time chat** between tenants and landlords (WebSocket + STOMP)
- **Favorites** and **reviews** for properties
- **Admin dashboard** with property moderation, user management, and reports
- **Push notifications** via Firebase
- **Stripe payments** for bookings
- **i18n** support (English, French)
- **Dark / light theme** toggle
- **Mobile-ready** via Capacitor

## Quick Start (Docker)

The fastest way to run everything locally. Spins up 6 services: frontend, backend, PostgreSQL, Redis, RabbitMQ, and Elasticsearch.

### Prerequisites

- Docker & Docker Compose

### Run

```bash
git clone <repo-url> && cd HomeFlex
docker-compose up --build
```

Once all containers are healthy (~60s):

| Service             | URL                                    |
| ------------------- | -------------------------------------- |
| Frontend            | http://localhost                       |
| Backend API         | http://localhost:8080/api/v1           |
| Swagger UI          | http://localhost:8080/swagger-ui.html  |
| RabbitMQ Management | http://localhost:15672 (guest / guest) |

### Stop

```bash
docker-compose down            # stop services, keep data
docker-compose down -v         # stop services and delete volumes
```

## Development Setup

### Backend

Requires **Java 21** and a running PostgreSQL instance (or use the Docker `db` service).

```bash
cd rental-backend
./gradlew bootRun              # start on port 8080
./gradlew build                # build + run all tests
./gradlew test                 # tests only
```

Key environment variables (set in `.env` or export):

| Variable                | Purpose                                |
| ----------------------- | -------------------------------------- |
| `JWT_SECRET`            | Signing key for access/refresh tokens  |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL                    |
| `STRIPE_API_KEY`        | Stripe secret key                      |
| `GOOGLE_CLIENT_ID`      | Google OAuth client ID                 |
| `FIREBASE_*`            | Firebase config for push notifications |

### Frontend

Requires **Node 20+**.

```bash
cd rental-app-frontend
npm install
npm start                      # dev server on port 4200
npm run build:prod             # production build
npm test                       # unit tests (Karma + Jasmine)
npm run lint                   # lint
```

The dev server proxies `/api/*` to `http://localhost:8080` by default. Update `src/app/environments/environment.ts` to change the API URL.

### Full Stack (Docker hybrid)

Run infrastructure in Docker and the app locally:

```bash
# start only db, redis, rabbitmq, elasticsearch
docker-compose up -d db redis rabbitmq elasticsearch

# run backend and frontend locally
cd rental-backend && ./gradlew bootRun &
cd rental-app-frontend && npm start &
```

## Project Structure

```
HomeFlex/
├── rental-app-frontend/          # Angular + Ionic SPA
│   ├── src/app/
│   │   ├── core/                 # Guards, interceptors, services, state
│   │   ├── features/             # Lazy-loaded feature modules
│   │   │   ├── admin/            # Admin dashboard, properties, users, reports
│   │   │   ├── auth/             # Login, register, forgot/reset password
│   │   │   ├── bookings/         # Booking list and detail
│   │   │   ├── chat/             # Chat list and room (WebSocket)
│   │   │   ├── favorites/        # Saved properties
│   │   │   ├── landing/          # Public landing page
│   │   │   ├── profile/          # User profile, edit, change password
│   │   │   └── properties/       # Search, detail, add, my-properties
│   │   ├── shared/               # Header, footer, loader, skeleton
│   │   └── models/               # TypeScript interfaces
│   ├── Dockerfile                # Multi-stage build (Node -> Nginx)
│   └── nginx.conf                # Reverse proxy, gzip, SPA fallback
│
├── rental-backend/               # Spring Boot API
│   ├── src/main/java/.../
│   │   ├── api/v1/               # REST controllers (all /api/v1/*)
│   │   ├── domain/
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── enums/            # Domain enumerations
│   │   │   ├── event/            # Outbox events
│   │   │   └── repository/       # Spring Data JPA repos
│   │   ├── dto/                  # Request, response, and common DTOs
│   │   ├── service/              # Business logic
│   │   ├── mapper/               # Entity <-> DTO mapping
│   │   ├── security/             # JWT filter + token provider
│   │   ├── config/               # Spring configs (Security, WebSocket, etc.)
│   │   ├── exception/            # Global handler + custom exceptions
│   │   └── infrastructure/       # External integrations (notifications)
│   ├── src/main/resources/
│   │   └── db/migration/         # Flyway SQL migrations
│   └── Dockerfile                # Multi-stage build (JDK -> JRE)
│
├── docker-compose.yml            # 6-service orchestration
├── .github/workflows/ci.yml     # CI pipeline
└── CLAUDE.md                     # AI assistant context
```

## API Overview

All endpoints are prefixed with `/api/v1`. Public endpoints don't require authentication.

| Method | Endpoint                  | Auth     | Description                      |
| ------ | ------------------------- | -------- | -------------------------------- |
| POST   | `/auth/register`          | Public   | Register a new user              |
| POST   | `/auth/login`             | Public   | Login, returns JWT               |
| POST   | `/auth/google`            | Public   | Google OAuth login               |
| POST   | `/auth/refresh`           | Public   | Refresh access token             |
| GET    | `/properties/search`      | Public   | Search with filters + pagination |
| GET    | `/properties/{id}`        | Public   | Property detail                  |
| POST   | `/properties`             | Landlord | Create property (multipart)      |
| PUT    | `/properties/{id}`        | Landlord | Update property                  |
| DELETE | `/properties/{id}`        | Landlord | Delete property                  |
| POST   | `/bookings`               | Tenant   | Create booking                   |
| PATCH  | `/bookings/{id}/approve`  | Landlord | Approve booking                  |
| PATCH  | `/bookings/{id}/reject`   | Landlord | Reject booking                   |
| PATCH  | `/bookings/{id}/cancel`   | Tenant   | Cancel booking                   |
| GET    | `/chat/rooms`             | Auth     | List chat rooms                  |
| POST   | `/chat/rooms`             | Auth     | Create chat room                 |
| GET    | `/favorites`              | Auth     | List favorites                   |
| POST   | `/favorites/{propertyId}` | Auth     | Add to favorites                 |
| GET    | `/admin/users`            | Admin    | List all users                   |
| GET    | `/admin/properties`       | Admin    | List all properties              |

See Swagger UI at `/swagger-ui.html` for the complete API reference.

## Testing

```bash
# Backend
cd rental-backend
./gradlew test                                          # all tests
./gradlew test --tests "com.realestate.rental.SomeTest" # single class

# Frontend
cd rental-app-frontend
npm test                                                # all tests
ng test -- --include='**/some.spec.ts'                  # single file
```

The backend includes **ArchUnit** tests that enforce architectural rules (e.g., controllers must not directly access repositories).

## License

Private project.
