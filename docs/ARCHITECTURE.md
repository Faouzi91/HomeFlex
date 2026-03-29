# HomeFlex Architecture

## System Overview

```
                                    INTERNET
                                       |
                           +-----------+-----------+
                           |      Port 80 (HTTP)   |
                           |                       |
                    +------v-------+        +------v-------+
                    |   Browser    |        |   Mobile     |
                    |  (Angular)   |        | (Capacitor)  |
                    +------+-------+        +------+-------+
                           |                       |
                           +-----------+-----------+
                                       |
                        +--------------v--------------+
                        |         NGINX               |
                        |    (rental-frontend:80)     |
                        |                             |
                        |  /           -> SPA (index) |
                        |  /api/*      -> backend     |
                        |  /ws/*       -> backend WS  |
                        |  /swagger-ui -> backend     |
                        +---------+--------+----------+
                             HTML |        | Proxy
                             CSS  |        |
                             JS   |        |
                                  |  +-----v-----------+
                                  |  |  SPRING BOOT    |
                                  |  | (backend:8080)  |
                                  |  +--+-+-+-+-+--+---+
                                  |     | | | | |  |
                       +----------+     | | | | |  +----------+
                       |                | | | | |             |
              +--------v-----+   +------v-+ | +-v------+  +--v---------+
              | PostgreSQL   |   | Redis  | | |RabbitMQ|  |Elastic     |
              | (db:5432)    |   | (:6379)| | |(:5672) |  |search      |
              |              |   |        | | |        |  |(:9200)     |
              | - users      |   | rate   | | | outbox |  | property   |
              | - properties |   | limit  | | | relay  |  | full-text  |
              | - bookings   |   | counters| | | events|  | + geo      |
              | - vehicles   |   |        | | |        |  | search     |
              | - messages   |   +--------+ | +--------+  +------------+
              +--------------+              |
                                  +---------v---------+
                                  |  External APIs    |
                                  |                   |
                                  |  - Stripe         |
                                  |  - Firebase (FCM) |
                                  |  - Google OAuth   |
                                  |  - Gmail SMTP     |
                                  |  - AWS S3         |
                                  +-------------------+
```

## Docker Compose Network

All 6 services run on a single bridge network (`rental-network`). The backend waits for all infrastructure services to report healthy before starting. The frontend waits for the backend.

```
rental-network (bridge)
|
+-- rental-frontend   (Nginx)           :80   -> serves SPA, proxies API/WS
+-- rental-backend    (Spring Boot)     :8080 -> REST API + WebSocket
+-- rental-db         (PostgreSQL 16)   :5432 -> primary data store
+-- rental-redis      (Redis 7)         :6379 -> rate limiting (Lua INCR+EXPIRE)
+-- rental-rabbitmq   (RabbitMQ 3)      :5672 -> outbox relay, ES index events
+-- rental-elasticsearch (ES 9)         :9200 -> property full-text + geo search
```

**Startup order** (health-check gated):

```
db, redis, rabbitmq, elasticsearch   (start in parallel)
            |
            v  (all healthy)
         backend
            |
            v  (healthy)
         frontend
```

## Request Flow

### REST API Request

```
Browser                  Nginx                 Spring Boot            PostgreSQL
  |                        |                        |                      |
  |-- GET /api/v1/props -->|                        |                      |
  |                        |-- proxy_pass --------->|                      |
  |                        |                        |-- SQL query -------->|
  |                        |                        |<-- result set -------|
  |                        |<-- JSON response ------|                      |
  |<-- HTTP 200 + JSON ----|                        |                      |
```

### Authenticated Request (Cookie-based JWT)

```
Browser                  Nginx        JwtFilter  RateLimit    Controller     Service
  |                        |              |          |             |            |
  |-- POST /api/v1/... -->|              |          |             |            |
  |   Cookie: ACCESS_TOKEN |-- proxy --->|          |             |            |
  |   Cookie: XSRF-TOKEN  |             |-- read   |             |            |
  |   X-XSRF-TOKEN header |             |   cookie |             |            |
  |                        |             |-- JWT OK |             |            |
  |                        |             +--------->|             |            |
  |                        |                        |-- INCR key  |            |
  |                        |                        |   (Redis)   |            |
  |                        |                        |-- under  -->|            |
  |                        |                        |   limit     |            |
  |                        |                        |             |-- logic -->|
  |                        |                        |             |<-- result -|
  |                        |<------------- JSON response ---------|            |
  |<-- HTTP 200 + JSON ----|             |          |             |            |
```

### Token Refresh (401 Auto-Retry)

```
Browser               authInterceptor        Nginx / Backend
  |                        |                        |
  |-- API request -------->|                        |
  |   (cookies sent auto)  |-- forward request ---->|
  |                        |<-- 401 Unauthorized ---|
  |                        |                        |
  |                        |-- POST /auth/refresh ->|
  |                        |   (REFRESH_TOKEN cookie)|
  |                        |<-- Set-Cookie: new ----|
  |                        |      ACCESS_TOKEN      |
  |                        |-- retry original req ->|
  |                        |   (new cookie auto)    |
  |                        |<-- 200 OK -------------|
  |<-- response -----------|                        |
```

## WebSocket Chat Flow

```
Browser                 Nginx              WebSocketConfig          ChatService       DB
  |                       |                      |                       |             |
  |-- CONNECT /ws ------->|                      |                       |             |
  |   + SockJS fallback   |-- upgrade ---------> |                       |             |
  |   + Authorization:    |   Connection         |-- validate JWT        |             |
  |     Bearer <token>    |                      |-- set user principal  |             |
  |                       |                      |                       |             |
  |<== CONNECTED =========|=====================>|                       |             |
  |                       |                      |                       |             |
  |-- SEND ------------->|                       |                       |             |
  |   /app/chat.send     |-- frame ------------->|                       |             |
  |   {roomId, text}     |                       |-- @MessageMapping --->|             |
  |                      |                       |   "chat.send"         |-- INSERT -->|
  |                      |                       |                       |             |
  |                      |                       |<-- broadcast ---------|             |
  |<== MESSAGE ==========|<======================|                       |             |
  |   /topic/chat.{room} |                       |                       |             |
  |                      |                       |                       |             |
  |-- SEND ------------>|                        |                       |             |
  |   /app/chat.typing  |-- frame ------------->|                        |             |
  |   {roomId, typing}  |                       |-- broadcast           |             |
  |                     |                        |   /topic/typing.{rm}  |             |
  |<== MESSAGE =========|<======================|                        |             |
```

**STOMP destinations:**

| Direction        | Destination              | Purpose                     |
| ---------------- | ------------------------ | --------------------------- |
| Client -> Server | `/app/chat.send`         | Send a message              |
| Client -> Server | `/app/chat.typing`       | Typing indicator            |
| Server -> Client | `/topic/chat.{roomId}`   | New messages in room        |
| Server -> Client | `/topic/typing.{roomId}` | Typing events in room       |
| Server -> Client | `/queue/notifications`   | User-specific notifications |

## Authentication & Authorization

```
                          +-------------------+
                          |   JWT Token       |
                          |   Provider        |
                          +--------+----------+
                                   |
               +-------------------+-------------------+
               |                                       |
    +----------v----------+              +-------------v-----------+
    |   Access Token      |              |   Refresh Token         |
    |   (15 min TTL)      |              |   (7 day TTL)           |
    |   httpOnly cookie   |              |   httpOnly cookie       |
    |   SameSite=Strict   |              |   Path=/api/v1/auth     |
    |   Stateless check   |              |   Stored in DB          |
    +---------------------+              +-------------------------+

                    ROLE HIERARCHY
    +------------------------------------------------+
    |                    ADMIN                        |
    |   +------------------------------------------+ |
    |   |              LANDLORD                    | |
    |   |   +------------------------------------+ | |
    |   |   |            TENANT                  | | |
    |   |   |   +------------------------------+ | | |
    |   |   |   |         PUBLIC (no auth)     | | | |
    |   |   |   +------------------------------+ | | |
    |   |   +------------------------------------+ | |
    |   +------------------------------------------+ |
    +------------------------------------------------+
```

**Public endpoints** (no token required):

- `POST /auth/register`, `/auth/login`, `/auth/google`, `/auth/refresh`
- `GET /properties/search`, `/properties/{id}`, `/properties/{id}/similar`
- `POST /properties/{id}/view`
- `GET /stats`
- `GET /reviews/property/{id}`
- `/ws/**`, `/swagger-ui/**`, `/actuator/**`

**Role-restricted endpoints:**

| Endpoint               | TENANT | LANDLORD | ADMIN |
| ---------------------- | ------ | -------- | ----- |
| Create booking         | x      |          |       |
| Cancel booking         | x      |          |       |
| Approve/reject booking |        | x        | x     |
| Create property        |        | x        | x     |
| Update/delete property |        | x        | x     |
| Create/update vehicle  |        | x        | x     |
| Vehicle condition rpt  |        | x        | x     |
| Chat                   | x      | x        | x     |
| Favorites              | x      | x        | x     |
| Admin dashboard        |        |          | x     |
| Manage users           |        |          | x     |
| View reports           |        |          | x     |

## Backend Layered Architecture

```
+---------------------------------------------------------------------+
|                        API LAYER (api/v1/)                          |
|  Controllers: thin, validate input, delegate, return ResponseEntity |
|  Auth, Booking, Property, Chat, Favorite, Review, Notification,     |
|  Admin, User, Stats, WebSocketChat                                  |
+-------------------------------+-------------------------------------+
                                |
                                v
+---------------------------------------------------------------------+
|                      SERVICE LAYER (service/)                       |
|  Business logic, @Transactional boundaries, orchestration           |
|  AuthService, BookingService, PropertyService, PropertySearchService,|
|  ChatService, NotificationService, AdminService, PaymentService,    |
|  EmailService, FavoriteService, ReviewService, UserService,         |
|  StorageService, EventOutboxService, OutboxRelayService,            |
|  VehicleService, VehicleAvailabilityService                         |
+----------+----------+----------+----------+-------------------------+
           |          |          |          |
           v          v          v          v
+----------+--+ +-----+-----+ +-+--------+ +----------+
| MAPPER      | | DOMAIN     | | DTO      | | INFRA    |
| (mapper/)   | | (domain/)  | | (dto/)   | |          |
|             | |            | |          | | Firebase |
| Entity <->  | | entity/   | | request/ | | Gateway  |
| DTO mapping | | enums/    | | response/| |          |
|             | | event/    | | common/  | +----------+
+-------------+ | repository/|+----------+
                +-----+------+
                      |
                      v
              +-------+--------+
              |  PostgreSQL    |
              |  (Flyway)     |
              +----------------+
```

**Key rules enforced by ArchUnit tests:**

- Controllers must NOT access repositories directly (always go through services)
- `@Transactional` only on service methods, never on controllers or repositories

## Database Schema (ERD)

```
+------------------+       +-------------------+       +------------------+
|     users        |       |    properties     |       |    amenities     |
+------------------+       +-------------------+       +------------------+
| id (PK, UUID)   |<--+   | id (PK, UUID)    |<--+   | id (PK, UUID)   |
| email (unique)   |   |   | landlord_id (FK) |   |   | name             |
| password_hash    |   |   | title            |   |   | category         |
| first_name       |   |   | description      |   |   +------------------+
| last_name        |   |   | property_type    |   |         |
| phone_number     |   |   | listing_type     |   |   +-----v-----------+
| role (enum)      |   |   | price / currency |   |   | property_       |
| avatar_url       |   |   | address / city   |   |   | amenities (M2M) |
| is_active        |   |   | lat / lng        |   |   +-----------------+
| is_verified      |   |   | bedrooms/bath    |   |
| created_at       |   |   | status (enum)    |   |
+------------------+   |   | view_count       |   |
        |              |   | created_at       |   |
        |              |   +-------------------+   |
        |              |           |               |
        |              |   +-------v----------+    |
        |              |   | property_images  |    |
        |              |   +------------------+    |
        |              |   | property_id (FK) |    |
        |              |   | image_url        |    |
        |              |   | is_primary       |    |
        |              |   +------------------+    |
        |              |                           |
        |              |   +------------------+    |
        |              |   | property_videos  |    |
        |              |   +------------------+    |
        |              |   | property_id (FK) |    |
        |              |   | video_url        |    |
        |              |   +------------------+    |
        |              |                           |
   +----v-------------v---+                        |
   |      bookings        |                        |
   +-----------------------+                        |
   | id (PK, UUID)        |                        |
   | property_id (FK) ----+------------------------+
   | tenant_id (FK)       |
   | start_date / end_date|
   | status (enum)        |
   | total_price           |
   | landlord_message      |
   | created_at            |
   +-----------------------+

   +---------------------+        +-------------------+
   |    chat_rooms       |        |    messages        |
   +---------------------+        +-------------------+
   | id (PK, UUID)      |<-------| chat_room_id (FK) |
   | property_id (FK)   |        | sender_id (FK)    |
   | tenant_id (FK)     |        | message_text      |
   | landlord_id (FK)   |        | is_read           |
   | last_message_at    |        | read_at           |
   | created_at         |        | created_at        |
   +---------------------+        +-------------------+

   +---------------------+        +-------------------+
   |    favorites        |        |    reviews         |
   +---------------------+        +-------------------+
   | id (PK, UUID)      |        | id (PK, UUID)     |
   | user_id (FK)       |        | property_id (FK)  |
   | property_id (FK)   |        | user_id (FK)      |
   | created_at         |        | rating (1-5)      |
   | UNIQUE(user,prop)  |        | comment            |
   +---------------------+        | created_at        |
                                  +-------------------+

   +---------------------+        +-------------------+
   |  reported_listings  |        |   notifications    |
   +---------------------+        +-------------------+
   | id (PK, UUID)      |        | id (PK, UUID)     |
   | property_id (FK)   |        | user_id (FK)      |
   | reporter_id (FK)   |        | title / message   |
   | reason / details   |        | notification_type |
   | status             |        | related_entity_*  |
   | admin_notes        |        | is_read / read_at |
   | created_at         |        | created_at        |
   +---------------------+        +-------------------+

   +---------------------+        +-------------------+
   |  refresh_tokens     |        |  oauth_providers   |
   +---------------------+        +-------------------+
   | id (PK, UUID)      |        | id (PK, UUID)     |
   | user_id (FK)       |        | user_id (FK)      |
   | token (unique)     |        | provider (enum)   |
   | expires_at         |        | provider_user_id  |
   | created_at         |        | access_token      |
   +---------------------+        | refresh_token     |
                                  | UNIQUE(provider,  |
   +---------------------+        |   provider_uid)   |
   |    fcm_tokens       |        +-------------------+
   +---------------------+
   | id (PK, UUID)      |        +-------------------+
   | user_id (FK)       |        |  outbox_events     |
   | token (unique)     |        +-------------------+
   | device_type (enum) |        | id (PK, UUID)     |
   | created_at         |        | aggregate_type    |
   +---------------------+        | aggregate_id      |
                                  | event_type        |
                                  | payload (JSON)    |
                                  | processed (bool)  |
                                  | created_at        |
                                  +-------------------+
```

## External Service Integrations

### Stripe (Payments)

```
Browser                     Backend                        Stripe
  |                            |                              |
  |-- POST /bookings -------->|                              |
  |                            |-- create PaymentIntent ---->|
  |                            |   amount, currency (XAF)    |
  |                            |   15% platform commission   |
  |                            |<-- client_secret -----------|
  |<-- {clientSecret} --------|                              |
  |                            |                              |
  |-- stripe.confirmPayment ->|                              |
  |   (client-side SDK)       +------------------------------+-->|
  |                                                              |
  |<-- payment confirmation ------------------------------------|
```

- SDK: `com.stripe:stripe-java:28.3.0`
- Webhook endpoint: `/api/v1/webhooks/**` (public, should validate Stripe signature)

### Firebase Cloud Messaging (Push Notifications)

```
Backend                         Firebase
  |                                |
  |-- sendMulticast() ----------->|
  |   title, body, FCM tokens     |
  |                                |----> Android device
  |                                |----> iOS device
  |                                |----> Web browser
  |<-- success/failure counts ----|
  |                                |
  |-- cleanup invalid tokens      |
```

- SDK: `com.google.firebase:firebase-admin:9.8.0`
- Conditionally enabled: `app.firebase.enabled=true`
- Credentials: service account JSON file path

### Google OAuth

```
Browser                     Backend                     Google
  |                            |                           |
  |-- POST /auth/google ------>|                           |
  |   {idToken}                |-- verify ID token ------->|
  |                            |<-- user info (email, ------|
  |                            |    name, picture)         |
  |                            |                           |
  |                            |-- find or create user     |
  |                            |-- store OAuth provider    |
  |                            |-- generate JWT tokens     |
  |<-- {accessToken, user} ----|                           |
```

- SDK: `com.google.api-client:google-api-client:2.9.0`
- Supports: GOOGLE, FACEBOOK, APPLE (extensible via `oauth_providers` table)

### AWS S3 (File Storage)

```
Backend                         AWS S3
  |                                |
  |-- uploadFile() --------------->|
  |   bucket, key, content-type    |
  |<-- public URL -----------------|
  |                                |
  |   Supports S3-compatible:      |
  |   MinIO, DigitalOcean Spaces   |
```

- SDK: `software.amazon.awssdk:s3:2.42.18`
- Dev fallback: returns placeholder URLs when not configured

### Gmail SMTP (Email)

```
Backend                         Gmail SMTP
  |                                |
  |-- send(to, subject, body) ---->|
  |   STARTTLS on port 587         |
  |                                |----> recipient inbox
  |                                |
  |   Used for:                    |
  |   - Password reset links       |
  |   - Email verification         |
  |   - Booking notifications      |
```

## Frontend Architecture

```
+----------------------------------------------------------------------+
|                           AppComponent                               |
|   <ion-app>                                                          |
|     <app-header/>          (shared, always visible)                   |
|     <router-outlet/>       (renders active route)                    |
|   </ion-app>                                                         |
+----+--------+--------+--------+--------+--------+--------+----------+
     |        |        |        |        |        |        |
     v        v        v        v        v        v        v
  Landing   Auth    Properties Bookings  Chat   Profile   Admin
  (eager)  (lazy)   (lazy)    (lazy)   (lazy)  (lazy)   (lazy)
     |        |        |
     |     +--+--+   +-+--------+-------+---------+------+
     |     |     |   |          |       |         |      |
     v     v     v   v          v       v         v      v
   home  login register search detail  add     my-props card
                      list            property
```

### State & Data Flow

```
+----------------+     HTTP      +----------------+     SQL       +---------+
|   Component    |<------------>|   Service       |<------------>| Backend |
|                |              |   (Injectable)  |              |  API    |
|  template      |  subscribe   |                 |   Observable |         |
|  bindings      |<-------------|  BehaviorSubject|              |         |
+-------+--------+              +--------+--------+              +---------+
        |                                |
        |   Signal-based state           |
        +<---------- PropertyState ------+
        +<---------- AuthState ----------+
```

### HTTP Interceptor Chain

```
Component -> HttpClient
                |
      +---------v---------+
      | authInterceptor   |  Sends withCredentials (cookies auto-attached)
      +---------+---------+
                |
      +---------v---------+
      | errorInterceptor  |  Catches errors, shows toast, redirects on 401
      +---------+---------+
                |
      +---------v---------+
      |    Nginx proxy    |  /api/* -> backend:8080
      +-------------------+
```

On 401 response, `authInterceptor` automatically attempts token refresh via `POST /auth/refresh` before retrying the original request.

## Infrastructure Integration

| Service           | Status | Usage                                                                         |
| ----------------- | ------ | ----------------------------------------------------------------------------- |
| **Redis**         | Active | API rate limiting via Lua atomic INCR+EXPIRE (RateLimitFilter)                |
| **RabbitMQ**      | Active | Outbox relay publishes domain events; PropertyIndexConsumer indexes to ES     |
| **Elasticsearch** | Active | Property full-text search (fuzzy match), faceted filtering, geo-distance sort |

### Outbox Relay Flow

```
EventOutboxService.enqueue()  →  outbox_events table
        ↓
OutboxRelayService (@Scheduled, virtual threads)
  SELECT ... FOR UPDATE SKIP LOCKED
        ↓
  RabbitMQ publish (publisher confirms, ACK)
        ↓
  Mark event as processed
        ↓
PropertyIndexConsumer (@RabbitListener)
  → Fetch from PostgreSQL → Index to Elasticsearch
```

### Vehicle Schema (vehicles.\*)

```
+-------------------+       +-------------------+       +--------------------+
| vehicles.vehicles |       | vehicles.vehicle_ |       | vehicles.vehicle_  |
+-------------------+       | images            |       | bookings           |
| id (PK, UUID)    |<--+   +-------------------+       +--------------------+
| owner_id (FK)    |   |   | id (PK, UUID)    |       | id (PK, UUID)      |
| brand / model    |   +---| vehicle_id (FK)  |       | vehicle_id (FK)    |
| daily_price      |   |   | image_url (TEXT) |       | tenant_id (FK)     |
| transmission     |   |   | display_order    |       | start_date / end   |
| fuel_type        |   |   | is_primary       |       | total_price        |
| status           |   |   +-------------------+       | status (enum)      |
| deleted_at       |   |                               | entity_version     |
+-------------------+   |   +-------------------+       +--------------------+
                        |   | vehicles.condition|
                        |   | _reports          |       +--------------------+
                        |   +-------------------+       | vehicles.condition_|
                        +---| vehicle_id (FK)  |       | report_images      |
                            | booking_id (FK)  |       +--------------------+
                            | reporter_id (FK) |       | report_id (FK)     |
                            | notes / mileage  |       | image_url          |
                            | fuel_level       |       +--------------------+
                            +-------------------+
```
