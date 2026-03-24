# Software Requirements Specification (SRS)

## HomeFlex — Multi-Vertical Rental Marketplace Platform

**Version:** 2.0
**Date:** March 24, 2026
**Classification:** Confidential
**Status:** Draft

---

## Document Control

| Version | Date       | Author        | Description                     |
|---------|------------|---------------|---------------------------------|
| 1.0     | 2024-XX-XX | Original Team | Initial real estate platform    |
| 2.0     | 2026-03-24 | Architect      | Full enterprise-grade overhaul + vehicle rentals |

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [System Overview & Vision](#2-system-overview--vision)
3. [Technology Stack & Justification](#3-technology-stack--justification)
4. [System Architecture](#4-system-architecture)
5. [Functional Requirements](#5-functional-requirements)
6. [Non-Functional Requirements](#6-non-functional-requirements)
7. [Data Model & Database Design](#7-data-model--database-design)
8. [API Design](#8-api-design)
9. [Security Architecture](#9-security-architecture)
10. [Infrastructure & Deployment](#10-infrastructure--deployment)
11. [Scalability & Performance Strategy](#11-scalability--performance-strategy)
12. [Monitoring & Observability](#12-monitoring--observability)
13. [Internationalization & Multi-Region](#13-internationalization--multi-region)
14. [Third-Party Integrations](#14-third-party-integrations)
15. [Mobile Strategy](#15-mobile-strategy)
16. [Testing Strategy](#16-testing-strategy)
17. [Release & Rollout Strategy](#17-release--rollout-strategy)
18. [Risk Analysis](#18-risk-analysis)
19. [Appendices](#19-appendices)

---

# 1. Introduction

## 1.1 Purpose

This Software Requirements Specification defines the complete technical and functional requirements for **HomeFlex**, an enterprise-grade multi-vertical rental marketplace platform. HomeFlex connects asset owners (landlords, vehicle owners) with tenants/renters through a secure, scalable, and globally-accessible platform.

This document serves as the authoritative source of truth for all development, QA, DevOps, and product decisions. It is written from the perspective of a senior software architect applying enterprise-level best practices, domain-driven design principles, and cloud-native architecture patterns.

## 1.2 Scope

HomeFlex is a **multi-vertical rental marketplace** supporting two primary rental verticals at launch:

1. **Real Estate Rentals** — Apartments, houses, villas, studios, rooms, offices, land
2. **Vehicle Rentals** — Cars, motorcycles, trucks, vans, RVs, boats

The platform is designed with a **pluggable vertical architecture**, allowing future expansion to equipment rentals, event space rentals, storage units, and other asset categories without architectural changes.

### In Scope (v2.0)

- Multi-vertical rental listings (properties + vehicles)
- Advanced search with geo-location, faceted filters, and full-text search
- Booking management with state machine workflows
- Integrated payment processing (deposits, rent collection, payouts)
- Real-time messaging (WebSocket-based chat)
- Push, email, and SMS notifications
- KYC verification for asset owners
- Reviews, ratings, and trust scoring
- Admin dashboard with analytics
- Multi-region deployment with i18n (EN, FR, AR, ES)
- Native mobile apps (iOS + Android via Capacitor)
- Document management (leases, insurance, registration)
- Maintenance request system (post-booking)

### Out of Scope (v2.0)

- AI-powered price recommendations (planned v3.0)
- Blockchain-based lease contracts (planned v3.0)
- Insurance marketplace integration (planned v3.0)
- White-label platform for agencies (planned v4.0)

## 1.3 Decision Baseline (Approved)

This SRS is based on explicit product and architecture decisions approved for v2.0.

| Decision Area | Approved Direction | Rationale |
|---|---|---|
| Product scope | Full enterprise vision | Avoid rework from under-scoped MVP architecture |
| Payments | Stripe-enabled rent collection and payouts | Native marketplace support (escrow + split payouts) |
| Deployment model | Multi-region from launch | Required for global growth and low latency |
| Cloud provider | AWS | Best fit for MENA + EU + NA regional footprint and managed services |
| Trust & safety | Mandatory owner/landlord KYC | Fraud reduction and compliance posture |
| Platform verticals | Real estate + vehicle rentals at launch | Shared marketplace primitives with higher TAM |

## 1.4 Definitions & Acronyms

| Term | Definition |
|------|-----------|
| **Owner** | A user who lists assets (landlord or vehicle owner) |
| **Renter** | A user who searches for and books rental assets |
| **Vertical** | A rental category (real estate, vehicles) |
| **Listing** | An asset posted for rent/sale by an owner |
| **Booking** | A confirmed reservation of a listing by a renter |
| **KYC** | Know Your Customer — identity verification process |
| **CQRS** | Command Query Responsibility Segregation |
| **DDD** | Domain-Driven Design |
| **SLA** | Service Level Agreement |
| **CDN** | Content Delivery Network |
| **WAF** | Web Application Firewall |
| **RBAC** | Role-Based Access Control |

## 1.5 References

- IEEE 830-1998 — Recommended Practice for Software Requirements Specifications
- OWASP Top 10 (2025) — Web Application Security Risks
- PCI DSS v4.0 — Payment Card Industry Data Security Standard
- GDPR — General Data Protection Regulation
- WCAG 2.2 — Web Content Accessibility Guidelines
- 12-Factor App Methodology

---

# 2. System Overview & Vision

## 2.1 Product Vision

HomeFlex aims to become the **unified rental marketplace** — a single platform where users can rent anything from a studio apartment to a pickup truck. Unlike niche platforms (Airbnb for stays, Turo for cars), HomeFlex aggregates multiple rental verticals into one experience with a shared identity, payment system, trust network, and communication layer.

## 2.2 Business Goals

| # | Goal | Metric | Target |
|---|------|--------|--------|
| BG-1 | User acquisition | Monthly active users | 100K within 12 months |
| BG-2 | Listing volume | Total active listings | 50K within 12 months |
| BG-3 | Transaction volume | Monthly bookings | 10K within 12 months |
| BG-4 | Revenue | Monthly GMV | $2M within 12 months |
| BG-5 | Global reach | Supported regions | 3 regions (NA, EU, MENA) |
| BG-6 | Platform reliability | Uptime SLA | 99.9% |

## 2.3 User Personas

### Persona 1: Renter (Tenant / Vehicle Renter)
- **Who:** Individual looking for a property to rent or a vehicle to hire
- **Goals:** Find affordable, verified listings quickly; book securely; communicate with owners
- **Pain points:** Scam listings, hidden fees, unresponsive owners, complex booking processes

### Persona 2: Owner (Landlord / Vehicle Owner)
- **Who:** Individual or business that owns properties or vehicles for rent
- **Goals:** Maximize occupancy/utilization; receive payments reliably; manage bookings efficiently
- **Pain points:** No-show renters, payment disputes, property damage, manual booking management

### Persona 3: Platform Administrator
- **Who:** HomeFlex operations team member
- **Goals:** Moderate content, resolve disputes, monitor platform health, manage KYC
- **Pain points:** Fraudulent listings, compliance risks, scaling support operations

### Persona 4: Support Agent
- **Who:** Customer support representative
- **Goals:** Resolve user issues quickly, escalate complex disputes
- **Pain points:** Lack of context, manual processes, no unified view

## 2.4 Core User Flows

### Flow 1: Renter Books a Property
```
Search → Filter → View Detail → Check Availability → Book → Pay Deposit
→ Owner Accepts → Lease Signed → Move-in → Monthly Rent → Review
```

### Flow 2: Renter Books a Vehicle
```
Search → Filter → View Detail → Select Dates → Book → Pay Full Amount
→ Owner Accepts → Pickup → Return → Inspection → Deposit Refund → Review
```

### Flow 3: Owner Onboarding
```
Register → Email Verification → KYC Submission → KYC Approved
→ Create Listing → Upload Media → Set Pricing → Publish → Go Live
```

### Flow 4: Payment Lifecycle
```
Renter Pays → Escrow Hold → Owner Confirms Service → Platform Takes Commission
→ Payout to Owner → Receipt Generated
```

---

# 3. Technology Stack & Justification

## 3.1 Backend Stack

### 3.1.1 Language & Runtime: Java 21 (LTS)

**Choice:** Java 21 Long-Term Support

**Why:**
- **LTS until September 2028** — guarantees security patches and stability for 2+ years without forced upgrades. Non-LTS versions (22, 23, 24) lose support in 6 months.
- **Virtual threads (Project Loom)** — Java 21 introduces lightweight virtual threads that allow handling thousands of concurrent connections without thread pool exhaustion. Critical for WebSocket connections and high-throughput booking APIs.
- **Pattern matching & record patterns** — Modern language features that reduce boilerplate in DTOs and domain model transformations.
- **Mature ecosystem** — The largest ecosystem of enterprise libraries, monitoring tools, and developer talent pool. Finding Java developers is significantly easier than Kotlin, Scala, or Go for enterprise projects.
- **GraalVM compatibility** — Java 21 works seamlessly with GraalVM native image for optional ahead-of-time compilation, reducing cold start times for serverless deployments.

**Alternatives considered:**
- **Kotlin** — More concise but smaller talent pool, and Spring Boot's Java support is first-class.
- **Node.js (TypeScript)** — Single-threaded event loop struggles with CPU-intensive operations (image processing, report generation). Not ideal for enterprise-grade backend with complex business logic.
- **Go** — Excellent performance but lacks the ORM maturity (Hibernate), security frameworks (Spring Security), and enterprise integration libraries that Spring provides.

---

### 3.1.2 Framework: Spring Boot 4.0.4

**Choice:** Spring Boot 4 with Spring Framework 7

**Why:**
- **Industry standard for Java enterprise applications** — Used by Netflix, Amazon, Alibaba, and most Fortune 500 companies. Battle-tested at massive scale.
- **Spring Security** — The most comprehensive security framework in any language. Provides JWT authentication, OAuth2, CSRF protection, method-level authorization, and CORS configuration out of the box. Building this from scratch in other frameworks would take months.
- **Spring Data JPA** — Eliminates 80% of boilerplate data access code. Repository interfaces auto-generate queries, and Specifications enable type-safe dynamic queries for complex search features.
- **Spring WebSocket** — First-class STOMP protocol support with SockJS fallback, message broker abstraction (swap in-memory for RabbitMQ without code changes), and JWT authentication on WebSocket connections.
- **Spring Boot Actuator** — Production-ready health checks, metrics export (Prometheus), and environment inspection. Critical for Kubernetes readiness/liveness probes.
- **Auto-configuration** — Convention over configuration dramatically reduces setup time. Adding Redis caching, RabbitMQ messaging, or Elasticsearch is a dependency + annotation away.
- **Spring Boot 4 specifically** — Requires Java 17+ (we use 21), supports Gradle 9, and introduces improved observability APIs with Micrometer 2.0.

**Alternatives considered:**
- **Quarkus** — Faster startup (important for serverless, less so for long-running services). Smaller community and fewer production case studies at enterprise scale.
- **Micronaut** — Similar to Quarkus. Compile-time DI is elegant but Spring's runtime DI has more flexibility for plugin architectures.
- **Django (Python)** — Excellent for rapid prototyping but Python's GIL limits true concurrency. Not suitable for real-time WebSocket-heavy applications.

---

### 3.1.3 Build Tool: Gradle 9.4.1

**Choice:** Gradle 9 with Kotlin DSL

**Why:**
- **Incremental builds** — Gradle only recompiles changed modules. In a multi-module project (which HomeFlex should become), this saves 60-80% of build time compared to Maven.
- **Build cache** — Remote build cache allows sharing compiled outputs across CI machines and developer laptops. A clean build on CI that would take 5 minutes becomes 30 seconds with cache hits.
- **Dependency locking** — `gradle.lockfile` ensures reproducible builds across environments. Critical for compliance and debugging production issues.
- **Parallel execution** — Gradle 9 runs independent tasks in parallel by default, utilizing all CPU cores.
- **Convention plugins** — Share build configuration across modules without copy-pasting. Define Java version, testing framework, code quality tools once.
- **Spring Boot 4 compatibility** — Spring Boot 4 officially supports Gradle 9.

**Alternatives considered:**
- **Maven** — XML-based configuration is verbose and slow. No build cache, no parallel execution by default. Still dominant in legacy enterprises but losing ground.

---

### 3.1.4 Primary Database: PostgreSQL 16

**Choice:** PostgreSQL 16 as the primary relational database

**Why:**
- **JSONB columns** — Store semi-structured data (vehicle features, property amenities, KYC metadata) without schema migration. Query JSONB with indexes for sub-millisecond lookups.
- **Full-text search** — Built-in `tsvector`/`tsquery` provides basic full-text search capabilities for simple queries without Elasticsearch overhead.
- **PostGIS extension** — Native geospatial queries (`ST_DWithin`, `ST_Distance`) for "properties within 5km" searches. No external service needed for basic geo queries.
- **Row-level security (RLS)** — Database-level access control ensures a landlord can never query another landlord's financial data, even if the application layer has a bug.
- **Partitioning** — Table partitioning by date range for bookings, messages, and audit logs. Keeps query performance consistent as data grows to hundreds of millions of rows.
- **Logical replication** — Stream changes to read replicas, Elasticsearch, and analytics databases in real-time.
- **ACID compliance** — Financial transactions (payments, deposits, refunds) require strong consistency guarantees that NoSQL databases cannot provide.
- **Mature hosting options** — AWS RDS, Google Cloud SQL, Azure Database, and Supabase all offer managed PostgreSQL with automated backups, failover, and scaling.

**Alternatives considered:**
- **MySQL** — Lacks JSONB, PostGIS, and advanced partitioning. Less suitable for complex queries.
- **MongoDB** — Document model is tempting for listings but lacks ACID transactions (required for payments), makes joins expensive (required for booking + user + property queries), and the schema-less nature creates data integrity issues at scale.
- **CockroachDB** — Excellent for multi-region writes but overkill at our scale and significantly more expensive.

---

### 3.1.5 Cache Layer: Redis 7 (Cluster Mode)

**Choice:** Redis 7 as the distributed cache, session store, and rate limiter

**Why:**
- **Sub-millisecond latency** — Redis serves cached data in <1ms. Property search results, user sessions, and popular listings can be served from cache instead of hitting PostgreSQL on every request.
- **Data structures beyond key-value** — Sorted sets for leaderboards (top properties by views), HyperLogLog for unique visitor counting, Streams for event processing, and Pub/Sub for real-time cache invalidation across nodes.
- **Rate limiting** — Redis's atomic `INCR` + `EXPIRE` commands implement token bucket rate limiting with zero race conditions. Essential for API abuse prevention.
- **Session store** — JWT refresh tokens and session metadata stored in Redis with TTL auto-expiry. When a user logs out, invalidate their session across all API nodes instantly.
- **Distributed locking** — Redlock algorithm prevents double-booking of the same property/vehicle for the same dates, even when requests hit different API nodes simultaneously.
- **WebSocket session registry** — Track which API node handles which WebSocket connection. When a message arrives, route it to the correct node.
- **Elasticache compatibility** — AWS ElastiCache provides managed Redis with automatic failover, backup, and scaling.

**Alternatives considered:**
- **Memcached** — Faster for simple key-value but lacks data structures, persistence, and pub/sub. Cannot replace Redis for rate limiting or distributed locking.
- **Hazelcast** — Java-native distributed cache but less ecosystem support and fewer managed hosting options.

---

### 3.1.6 Search Engine: Elasticsearch 8

**Choice:** Elasticsearch 8 for full-text search, geo-search, and analytics

**Why:**
- **Full-text search with relevance scoring** — "Cozy apartment near downtown with parking" returns results ranked by relevance, not just filtered by keywords. Supports synonyms ("flat" = "apartment"), fuzzy matching (typo tolerance), and language-specific analyzers (French stemming).
- **Geo-spatial queries** — `geo_distance` queries for "listings within 10km of my location" with sub-100ms response times, even across millions of listings. Supports geo-bounding box, geo-polygon, and geo-shape queries.
- **Faceted search** — "Show me 3-bedroom apartments in Paris under €2000/month" with real-time aggregation counts ("42 apartments, 18 houses, 7 studios"). This is extremely expensive in PostgreSQL but native to Elasticsearch.
- **Autocomplete & suggestions** — Completion suggester for instant search-as-you-type in the search bar. "Par..." → "Paris, France", "Parking included".
- **Analytics aggregations** — Admin dashboard metrics (bookings per region, revenue trends, popular listing types) computed in Elasticsearch without loading PostgreSQL.
- **Horizontal scaling** — Shard data across nodes. As listings grow from 50K to 5M, add nodes without downtime.
- **Near-real-time indexing** — Changes in PostgreSQL are synced to Elasticsearch within 1 second via the outbox pattern + event consumers.

**Why not PostgreSQL full-text search alone:**
- PostgreSQL's `tsvector` works for simple keyword matching but cannot do relevance scoring, fuzzy matching, faceted aggregation, or geo-distance sorting efficiently. At 50K+ listings with complex filters, PostgreSQL queries become 100-500ms while Elasticsearch remains under 50ms.

**Alternatives considered:**
- **Apache Solr** — Comparable features but less momentum, fewer managed hosting options, and a more complex operational model.
- **Typesense** — Simpler API but limited aggregation capabilities and smaller ecosystem.
- **Meilisearch** — Excellent developer experience but lacks geo-spatial queries and enterprise security features.

---

### 3.1.7 Message Broker: RabbitMQ 3.13

**Choice:** RabbitMQ for asynchronous messaging, event-driven architecture, and WebSocket message routing

**Why:**
- **Decouples services** — When a booking is confirmed, the booking service publishes a `BookingConfirmed` event. The notification service, payment service, calendar service, and analytics service each consume this event independently. If the notification service is down, the booking still succeeds and notifications are delivered when the service recovers.
- **Reliable message delivery** — Messages are persisted to disk with acknowledgment-based delivery. No event is lost, even during server restarts. Critical for payment events and booking state changes.
- **STOMP plugin** — RabbitMQ has a native STOMP plugin that replaces Spring's simple in-memory broker. WebSocket messages are routed through RabbitMQ, enabling chat to work across multiple API nodes. User A connected to Node 1 can message User B connected to Node 2.
- **Dead letter queues** — Failed messages are automatically routed to a DLQ for inspection and retry. Critical for debugging payment processing failures.
- **Priority queues** — Payment events get higher priority than notification events, ensuring financial operations are never delayed by a notification backlog.
- **Spring AMQP integration** — First-class Spring Boot support. Adding a consumer is a `@RabbitListener` annotation on a method.
- **Proven at scale** — Used by Goldman Sachs, Cisco, and Instagram. Battle-tested for financial messaging.

**Alternatives considered:**
- **Apache Kafka** — Better for event streaming and log aggregation but more complex to operate, higher resource requirements, and overkill for our message patterns (request/reply, pub/sub, work queues). Kafka shines at 100K+ messages/second; our volume is 1K-10K/second.
- **AWS SQS/SNS** — Vendor-locked, higher latency (50-100ms vs 1-5ms), and no STOMP support for WebSocket routing.
- **Redis Pub/Sub** — No message persistence. If a consumer is offline, messages are lost. Unacceptable for payment and booking events.

---

### 3.1.8 Object Storage: AWS S3 + CloudFront CDN

**Choice:** Amazon S3 for file storage, CloudFront for global content delivery

**Why S3:**
- **Unlimited storage** — No capacity planning needed. Store 10 images or 10 million images at the same operational complexity.
- **11 nines durability (99.999999999%)** — Data loss probability is near zero. Critical for legal documents (leases, KYC IDs).
- **Lifecycle policies** — Automatically move old files to cheaper storage tiers (S3 Infrequent Access after 90 days, Glacier after 1 year).
- **Pre-signed URLs** — Generate time-limited upload/download URLs. Users upload directly to S3 from the browser without routing through the API server, eliminating a major bottleneck.
- **Event notifications** — When a file is uploaded, S3 triggers a Lambda function for image resizing, thumbnail generation, or virus scanning.

**Why CloudFront:**
- **Global edge network** — 450+ edge locations worldwide. A user in Casablanca gets property images from a nearby edge server (20ms) instead of the origin in eu-west-1 (200ms).
- **Automatic WebP/AVIF conversion** — CloudFront Functions can transform images to modern formats on the fly, reducing bandwidth by 30-50%.
- **DDoS protection** — AWS Shield Standard is included at no extra cost. Protects against volumetric attacks on media endpoints.

**Alternatives considered:**
- **Google Cloud Storage + Cloud CDN** — Comparable but less mature CDN edge network.
- **Cloudflare R2** — No egress fees (attractive) but fewer integration options with the broader AWS ecosystem we're using.
- **Self-hosted MinIO** — Full S3-compatible API but requires managing storage infrastructure, replication, and backups ourselves.

---

### 3.1.9 Resilience: Resilience4j

**Choice:** Resilience4j for circuit breaking, rate limiting, retry, and bulkhead isolation

**Why:**
- **Circuit breaker** — When Firebase push notifications are down, stop sending requests after 5 failures and return a fallback (queue for retry). Without this, a downstream outage causes thread pool exhaustion and cascading failures across the entire platform.
- **Rate limiter** — Limit property search to 30 requests/minute per user. Limit login attempts to 5/minute per IP. Prevent abuse without building custom rate limiting infrastructure.
- **Retry with exponential backoff** — Payment processing to Stripe occasionally fails due to network issues. Automatically retry 3 times with 1s, 2s, 4s delays before giving up.
- **Bulkhead** — Isolate thread pools per external service. If the email service blocks on SMTP connections, it doesn't consume threads needed for booking API requests.
- **Time limiter** — KYC verification via third-party API must respond within 10 seconds or timeout. Prevents hung requests.
- **Lightweight** — Unlike Hystrix (deprecated), Resilience4j is a library, not a framework. No runtime overhead when circuits are closed.
- **Spring Boot integration** — Annotate any method with `@CircuitBreaker`, `@RateLimiter`, `@Retry`. Configuration via application.yml.

**Alternatives considered:**
- **Netflix Hystrix** — Deprecated since 2018. No longer maintained.
- **Sentinel (Alibaba)** — Powerful but documentation is primarily in Chinese, and the ecosystem is Alibaba-centric.

---

### 3.1.10 API Documentation: SpringDoc OpenAPI 3.0.1

**Choice:** SpringDoc OpenAPI with Swagger UI

**Why:**
- **Auto-generated from code** — API documentation is always in sync with the actual implementation. No manual YAML/JSON maintenance.
- **Swagger UI** — Interactive API explorer for frontend developers and QA. Test endpoints directly from the browser.
- **Client code generation** — Generate TypeScript API clients from the OpenAPI spec, eliminating manual service creation in Angular.
- **Spring Boot 4 compatible** — Version 3.0.1 is specifically built for Spring Boot 4 and Spring Framework 7.

---

### 3.1.11 ORM: Hibernate 6 (via Spring Data JPA)

**Choice:** Hibernate 6 as the JPA implementation

**Why:**
- **Entity mapping** — Map Java objects to database tables with annotations. Relationships (OneToMany, ManyToMany) are declared once and Hibernate handles joins, cascades, and lazy loading.
- **Batch operations** — `spring.jpa.properties.hibernate.jdbc.batch_size=20` groups 20 INSERT/UPDATE statements into a single database round-trip. Critical for bulk listing imports and notification fanout.
- **Second-level cache** — Frequently accessed entities (amenities, categories, regions) cached across sessions with Redis as the cache provider.
- **Hibernate Envers** — Audit logging for entity changes. Every modification to a listing or booking is recorded with who, when, and what changed. Required for compliance and dispute resolution.
- **Database-agnostic** — Switch from PostgreSQL to MySQL or CockroachDB by changing a configuration line. No query rewrites.

---

## 3.2 Frontend Stack

### 3.2.1 Framework: Angular 21

**Choice:** Angular 21 (current active release, LTS planned)

**Why:**
- **Opinionated structure** — Angular enforces a consistent project structure (modules, components, services, guards, interceptors) that scales to large teams. Unlike React, which requires choosing routing, state management, and folder structure, Angular provides all of these out of the box.
- **TypeScript-first** — Angular is built in TypeScript and requires it. This catches type errors at compile time, not runtime. For a financial platform handling payments, type safety is non-negotiable.
- **Dependency injection** — Angular's DI system is the most mature in the frontend ecosystem. Services are singletons by default, testable via injection, and tree-shakeable.
- **Signals (Angular 21)** — Zone.js-free reactivity model. Components re-render only when their specific signal dependencies change, not when any async operation completes. 30-50% fewer unnecessary change detection cycles.
- **Standalone components** — No more NgModules. Each component declares its own imports, making code splitting and lazy loading granular.
- **Built-in i18n** — Native internationalization support with compile-time translation extraction and runtime locale switching.
- **Angular Material + CDK** — Component Dev Kit provides unstyled, accessible primitives (drag-drop, virtual scroll, overlay) that we style with Tailwind. No framework lock-in.
- **Enterprise adoption** — Google, Microsoft, Deutsche Bank, and UPS use Angular. Large talent pool, long-term support, and predictable release cadence (every 6 months).
- **Schematics** — `ng generate` scaffolds components, services, guards, and pipes with consistent structure. Enforces team conventions automatically.

**Alternatives considered:**
- **React 19** — Excellent component model but requires assembling routing (React Router), state management (Redux/Zustand), form handling (React Hook Form), and HTTP client (Axios) from separate libraries. More freedom but more decisions and inconsistency risk across a large team.
- **Vue 3** — Great developer experience but smaller enterprise ecosystem, fewer large-scale production references, and less TypeScript maturity than Angular.
- **Next.js / Nuxt / Analog** — SSR frameworks. We don't need server-side rendering — our app is a SPA behind authentication. SSR adds complexity without benefit for dashboard-heavy applications.

---

### 3.2.2 UI Framework: Tailwind CSS 4 + Ionic 8

**Choice:** Tailwind CSS 4 as the primary styling system, Ionic 8 for mobile-specific UI components

**Why Tailwind CSS 4:**
- **Utility-first** — Build UIs by composing small utility classes (`flex items-center gap-4 p-6 bg-white rounded-xl shadow-lg`) instead of writing custom CSS. Eliminates naming conventions (BEM), specificity wars, and dead CSS.
- **Zero runtime** — Tailwind generates only the CSS classes you actually use. Typical production CSS is 10-30KB gzipped, compared to 200KB+ for Bootstrap or Material Design CSS.
- **v4 is a ground-up rewrite** — 5x faster full builds, 100x faster incremental builds. Uses native CSS cascade layers, `@property` for custom properties, and `color-mix()` for dynamic colors.
- **CSS-first configuration** — v4 replaces `tailwind.config.js` with CSS `@theme` directives. Configuration lives alongside the styles it affects.
- **Design system enforcement** — Tailwind's constrained utility set (spacing: 1, 2, 3, 4... not arbitrary pixels) naturally produces consistent designs without a separate design system tool.
- **Responsive + dark mode** — `md:grid-cols-3 dark:bg-gray-900` handles responsive and dark mode with zero JavaScript.

**Why Ionic 8 (alongside Tailwind):**
- **Native mobile UI patterns** — `ion-modal`, `ion-action-sheet`, `ion-refresher`, `ion-infinite-scroll` provide platform-specific UX patterns that web CSS cannot replicate. Pull-to-refresh feels native on iOS because Ionic uses platform conventions.
- **Capacitor integration** — Ionic is built by the same team as Capacitor. Components are designed to work seamlessly in native container apps.
- **Gesture system** — Swipe-to-delete, drag-to-reorder, and pinch-to-zoom are built into Ionic's gesture API. Building these from scratch with Hammer.js or native events is a significant engineering effort.
- **We use Ionic selectively** — Only for mobile-specific interactions (modals, action sheets, infinite scroll, toast, loading). All layout and styling is Tailwind. This avoids the bloat of using Ionic for everything.

**Why NOT Angular Material (removing it):**
- **Redundant with Ionic** — Both provide buttons, cards, dialogs, and form controls. Having both means conflicting styles, doubled bundle size, and confused developers choosing between `mat-button` and `ion-button`.
- **Material Design aesthetic** — Forces Google's design language. Tailwind gives us complete design freedom.
- **Keep only Angular CDK** — The headless primitives (virtual scroll, drag-drop, overlay, accessibility) from `@angular/cdk` are valuable and framework-agnostic. We keep CDK, drop Material.

---

### 3.2.3 State Management: NgRx SignalStore

**Choice:** NgRx SignalStore for centralized, reactive state management

**Why:**
- **Signal-based** — Built on Angular 21's signals, not RxJS Observables. Simpler mental model: state is a value, not a stream. No `subscribe()`, no `unsubscribe()`, no memory leaks.
- **Centralized state** — Instead of 14 services each holding their own `BehaviorSubject`, all application state lives in typed stores: `AuthStore`, `PropertyStore`, `BookingStore`, `ChatStore`. Debugging is trivial — inspect one store to see the entire application state.
- **DevTools** — NgRx DevTools provides time-travel debugging, action logging, and state diffing. When QA reports "the booking button was disabled when it shouldn't be," you can replay the exact sequence of state changes.
- **Entity management** — NgRx `withEntities()` provides normalized entity state (no duplicate property objects across different views), CRUD operations, and selection.
- **Computed state** — Derived state (filtered properties, unread notification count, total booking revenue) is automatically recalculated when dependencies change, with memoization to prevent unnecessary recomputation.
- **Lightweight** — SignalStore is 3KB gzipped. The full NgRx suite (Store + Effects + Entity) is 15KB. Compared to Redux Toolkit (30KB+), it's minimal.

**Alternatives considered:**
- **Plain RxJS BehaviorSubjects** — Current approach. Works for small apps but becomes unmaintainable with 14+ services, no debugging tools, and no action history.
- **NGXS** — Simpler than NgRx but less ecosystem support and no signal-based variant.
- **Akita** — Unmaintained. Last release was 2023.

---

### 3.2.4 Real-Time: STOMP over SockJS (Single Connection)

**Choice:** Single STOMP/WebSocket connection via `@stomp/stompjs`

**Why:**
- **STOMP protocol** — Provides pub/sub semantics over WebSocket. Subscribe to `/topic/chat.{roomId}` for chat messages, `/user/queue/notifications` for personal notifications. The protocol handles message routing, not our code.
- **SockJS fallback** — When WebSocket is blocked by corporate firewalls or proxies, SockJS falls back to HTTP long-polling transparently. No user impact.
- **Single connection** — The current architecture creates TWO separate STOMP connections (WebSocketService + ChatService). We consolidate to ONE connection managed by `WebSocketService`, with ChatService, NotificationService, and BookingService all subscribing through it. This halves bandwidth and connection overhead.
- **RabbitMQ backing** — In production, STOMP messages are routed through RabbitMQ, enabling cross-node message delivery. User A on API Node 1 can message User B on API Node 3.

---

### 3.2.5 Mobile: Capacitor 8

**Choice:** Capacitor 8 for native iOS and Android builds

**Why:**
- **Web-to-native bridge** — Write once in Angular, deploy to web, iOS, and Android. Access native APIs (camera, GPS, push notifications, biometrics) through a JavaScript bridge.
- **Capacitor 8 improvements** — Built-in edge-to-edge support (no more status bar hacks), Swift Package Manager for iOS (faster builds), and improved plugin architecture.
- **No React Native / Flutter rewrite** — Building separate native apps means 3 codebases (web, iOS, Android) with 3x the maintenance cost. Capacitor gives us native distribution from a single codebase.
- **Full web compatibility** — Unlike React Native (which uses native views), Capacitor renders the actual web app in a native WebView. This means 100% feature parity between web and mobile — no "this feature is web-only" limitations.
- **Plugin ecosystem** — Camera, geolocation, push notifications, filesystem, biometrics, app updates — all available as official plugins.

**Trade-off acknowledged:** WebView performance is 10-20% slower than truly native rendering. For a content-heavy app (listings, images, text) this is imperceptible. For a 3D game, it would be unacceptable.

---

### 3.2.6 Maps: Leaflet 1.9

**Choice:** Leaflet for interactive maps

**Why:**
- **Free and open-source** — No API key required for the library itself. Google Maps charges $7/1000 loads after the free tier. At 100K MAU viewing maps, Google Maps would cost $2K+/month. Leaflet is $0.
- **Lightweight** — 42KB gzipped vs Google Maps (150KB+) or Mapbox GL (200KB+).
- **Tile provider flexibility** — Use OpenStreetMap (free), Mapbox (premium), or Stamen (artistic) tiles. Switch providers without code changes.
- **Marker clustering** — `leaflet.markercluster` plugin groups nearby listings into clusters at low zoom levels. Essential when displaying 1000+ listings on a single map.
- **Custom markers** — Style markers to differentiate property types, price ranges, or availability status.

---

### 3.2.7 Charts: Chart.js 4.5

**Choice:** Chart.js for admin dashboard visualizations

**Why:**
- **Simple API** — Bar, line, pie, doughnut, and radar charts with minimal configuration. The admin dashboard needs standard business charts, not D3-level custom visualizations.
- **Canvas-based** — Renders on HTML Canvas, not SVG. Better performance for charts with many data points (monthly revenue trends across 12 months × 5 regions = 60 data points).
- **8KB gzipped** — Tree-shakeable. Import only the chart types you use.
- **Responsive** — Charts resize automatically on window resize. No manual dimension management.

**Alternatives considered:**
- **D3.js** — Overkill. D3 is a low-level visualization library for custom data visualizations (network graphs, geographic heat maps). Our admin dashboard needs standard bar/line/pie charts.
- **ECharts** — More features but 300KB+ bundle size. Not worth it for our use case.

---

### 3.2.8 TypeScript 5.9

**Choice:** TypeScript 5.9 (required by Angular 21)

**Why:**
- **Required by Angular 21** — Angular 21's compiler requires TypeScript >=5.9.0 <6.0.0.
- **Improved type inference** — Better generic type inference reduces the need for explicit type annotations.
- **`satisfies` operator** — Validate that a value matches a type without widening the type. Useful for configuration objects and routing definitions.
- **Decorator metadata** — Native decorator support without `experimentalDecorators` flag.

---

## 3.3 Infrastructure Stack

### 3.3.1 Cloud Provider: AWS (Amazon Web Services)

**Choice:** AWS as the primary cloud provider

**Why:**
- **Most mature cloud platform** — 200+ services, 33 geographic regions, 105 availability zones. The largest selection of compute, storage, networking, and AI services.
- **Multi-region capability** — Deploy HomeFlex in `us-east-1` (North America), `eu-west-1` (Europe), and `me-south-1` (Middle East/North Africa) from day one. AWS has the most regions globally, including the MENA region that GCP and Azure have limited presence in.
- **Managed services reduce ops burden:**
  - **RDS** — Managed PostgreSQL with automated backups, read replicas, and failover
  - **ElastiCache** — Managed Redis with cluster mode
  - **Amazon MQ** — Managed RabbitMQ
  - **OpenSearch** — Managed Elasticsearch
  - **ECS Fargate** — Serverless container orchestration (no EC2 instances to manage)
  - **CloudFront** — Global CDN with 450+ edge locations
  - **S3** — Unlimited object storage
  - **Route 53** — DNS with latency-based routing for multi-region
  - **WAF** — Web Application Firewall for API protection
  - **Cognito** — Optional identity provider (we use our own JWT but Cognito can handle Google/Apple OAuth)
- **Cost optimization tools** — Savings Plans, Spot Instances, and Reserved Instances can reduce costs by 40-70%.
- **Compliance certifications** — SOC 1/2/3, ISO 27001, PCI DSS, HIPAA, GDPR. Critical for handling payment data and personal information.

**Alternatives considered:**
- **Google Cloud Platform (GCP)** — Strong in AI/ML and Kubernetes (GKE is excellent) but fewer regions (40 vs AWS's 33+ but AWS has more edge locations), and limited presence in MENA. Better choice if we were building ML-heavy features.
- **Azure** — Strong enterprise integration (Active Directory, Office 365) but less developer-friendly tooling and higher prices for comparable services. Better choice if we were building for enterprise IT departments.
- **Multi-cloud** — Running on multiple clouds increases complexity 3-5x (different IAM, networking, monitoring for each) with marginal benefit. We design for cloud portability (containerized, standard protocols) but deploy on one cloud.

---

### 3.3.2 Container Orchestration: AWS ECS Fargate

**Choice:** ECS Fargate for serverless container orchestration

**Why:**
- **No server management** — Fargate runs containers without EC2 instances. No OS patching, no capacity planning, no AMI updates. Define CPU/memory per container and AWS handles placement.
- **Auto-scaling** — Scale from 2 to 50 containers based on CPU, memory, or custom metrics (request count, queue depth). Scale back to 2 during off-peak hours automatically.
- **Cost-effective at our scale** — Kubernetes (EKS) has a $73/month control plane cost per cluster plus node management overhead. Fargate charges only for running containers. At <50 containers, Fargate is cheaper and simpler.
- **Service discovery** — AWS Cloud Map provides DNS-based service discovery. The API service finds the notification service by name (`notification.homeflex.local`), not by IP address.
- **Blue/green deployments** — ECS supports zero-downtime deployments by spinning up new containers, health-checking them, and draining old containers. No manual deployment scripts.

**When to upgrade to Kubernetes (EKS):**
- When we exceed 100 containers or need advanced scheduling (GPU workloads, spot instance management, custom operators). This is a v3.0+ consideration.

---

### 3.3.3 CI/CD: GitHub Actions

**Choice:** GitHub Actions for continuous integration and deployment

**Why:**
- **Integrated with GitHub** — Our source code is on GitHub. No external CI/CD tool to configure, authenticate, and maintain.
- **Workflow-as-code** — YAML pipeline definitions versioned alongside application code. Changes to the pipeline go through the same PR review process as code changes.
- **Matrix builds** — Test on Java 21, Java 25 simultaneously. Test on Node 20, Node 22 simultaneously. Catch compatibility issues before they reach production.
- **Caching** — Cache Gradle dependencies, npm packages, and Docker layers between runs. A 10-minute build becomes 3 minutes with warm caches.
- **Environment protection rules** — Production deployments require manual approval from designated reviewers. No accidental pushes to prod.
- **Free for public repos** — 2,000 CI/CD minutes/month for private repos on the Team plan.

---

### 3.3.4 Containerization: Docker

**Choice:** Docker for application packaging

**Why:**
- **Environment parity** — The exact same container image runs on a developer's laptop, in CI, in staging, and in production. "Works on my machine" is eliminated.
- **Multi-stage builds** — Build stage uses full JDK (400MB) for compilation. Runtime stage uses JRE-slim (150MB). Final image is 200MB instead of 600MB.
- **Docker Compose for local development** — `docker-compose up` starts PostgreSQL, Redis, RabbitMQ, Elasticsearch, and the API server with one command. New developers are productive in 5 minutes, not 5 hours.
- **Layer caching** — Dependency layers (rarely change) are cached separately from application code layers (change frequently). Rebuilds are fast.

---

### 3.3.5 Monitoring: Prometheus + Grafana + ELK

**Choice:** Prometheus for metrics, Grafana for dashboards, ELK for logs

**Why Prometheus:**
- **Pull-based metrics** — Prometheus scrapes `/actuator/prometheus` every 15 seconds. No agent installation, no SDK integration. Spring Boot Actuator exposes JVM, HTTP, database, and custom metrics automatically.
- **PromQL** — Powerful query language for alerts. `rate(http_requests_total{status="500"}[5m]) > 0.01` triggers an alert when error rate exceeds 1%.
- **AlertManager** — Route alerts to Slack, PagerDuty, or email based on severity and service.

**Why Grafana:**
- **Unified dashboards** — Visualize Prometheus metrics, Elasticsearch logs, and PostgreSQL stats in a single dashboard. No context switching between tools.
- **Pre-built dashboards** — Spring Boot, PostgreSQL, Redis, RabbitMQ, and JVM dashboards available from the Grafana community.

**Why ELK (Elasticsearch + Logstash + Kibana):**
- **Centralized logging** — Aggregate logs from all API nodes, workers, and infrastructure into a single searchable index.
- **Structured logging** — JSON log format with correlation IDs, user IDs, and request metadata. Search "all requests from user X that resulted in a 500 error" in seconds.
- **Log-based alerts** — Alert on error patterns before they become incidents.

---

## 3.4 External Services

### 3.4.1 Payment Processing: Stripe

**Choice:** Stripe for payment processing, escrow, and payouts

**Why:**
- **Stripe Connect** — Purpose-built for marketplace payments. Renters pay HomeFlex, HomeFlex holds funds in escrow, and HomeFlex pays out to owners minus commission. This is exactly our business model.
- **Global coverage** — Supports 135+ currencies, 46+ countries, and local payment methods (SEPA in Europe, Bancontact in Belgium, iDEAL in Netherlands).
- **PCI DSS Level 1** — Stripe handles all card data. We never see, store, or transmit card numbers. Our PCI compliance scope is minimal (SAQ-A).
- **Stripe Identity** — KYC verification (ID document scanning, selfie matching, address verification) built into the same platform. No separate KYC vendor needed.
- **Webhook-driven** — Payment events (succeeded, failed, refunded, disputed) are delivered via webhooks. Our system reacts to events rather than polling.
- **Subscription billing** — For monthly rent collection, Stripe Billing handles recurring charges, proration, and dunning (retry failed payments).

---

### 3.4.2 SMS Notifications: Twilio

**Choice:** Twilio for SMS and WhatsApp notifications

**Why:**
- **Global SMS delivery** — Send SMS to 180+ countries with local number support.
- **WhatsApp Business API** — In MENA and Europe, WhatsApp has 90%+ penetration. WhatsApp notifications have 98% open rates vs 20% for email.
- **Programmable messaging** — Templates for booking confirmations, payment receipts, and verification codes.
- **Verify API** — Phone number verification with OTP (one-time password) for KYC.

---

### 3.4.3 Email: AWS SES (Simple Email Service)

**Choice:** AWS SES for transactional email

**Why:**
- **$0.10 per 1,000 emails** — 10x cheaper than SendGrid or Mailgun at scale.
- **High deliverability** — Dedicated IP addresses, DKIM/SPF/DMARC configuration, and reputation management.
- **AWS ecosystem integration** — Triggered from Lambda, SQS, or directly from the API server.
- **Templates** — Store email templates in SES and render with dynamic variables.

---

### 3.4.4 Push Notifications: Firebase Cloud Messaging (FCM)

**Choice:** Firebase Cloud Messaging for mobile push notifications

**Why:**
- **Free** — No per-message cost, regardless of volume.
- **Cross-platform** — Single API for iOS (APNs) and Android (FCM).
- **Topic messaging** — Subscribe users to topics ("new-listings-paris") for targeted push.
- **Already integrated** — Current codebase uses Firebase Admin SDK.

---

### 3.4.5 OAuth Providers: Google + Apple + Facebook

**Choice:** Multi-provider social login

**Why:**
- **Google Sign-In** — Already implemented. 70%+ of users prefer social login over email/password.
- **Apple Sign-In** — Required by Apple App Store guidelines for any app offering third-party login.
- **Facebook Login** — High adoption in MENA and European markets.
- **Spring Security OAuth2 Client** — All three providers are configured declaratively in application.yml. No custom code per provider.

---

## 3.5 Stack Decision Matrix (Use, Tradeoffs, Revisit Criteria)

This matrix is normative for architectural governance. Every major choice includes purpose, accepted tradeoff, and trigger to revisit.

| Layer | Selected Technology | Primary Use in HomeFlex | Accepted Tradeoff | Revisit Trigger |
|---|---|---|---|---|
| Backend runtime | Java 21 | High-concurrency APIs and long-term support | Heavier memory footprint vs Go/Node | If infra cost grows >25% without throughput gains |
| Backend framework | Spring Boot 4 | Secure, fast delivery of enterprise APIs | Framework complexity for small features | If app decomposes into many lightweight microservices |
| Primary DB | PostgreSQL 16+ | Transactions, relational integrity, ACID bookings | Query tuning needed at high scale | If write throughput or geo-write latency exceeds SLA |
| Distributed cache | Redis 7 | Hot data caching, token/session data, rate-limits | Operational complexity in cluster mode | If cache hit ratio stays <60% after optimization |
| Search engine | Elasticsearch/OpenSearch | Full-text, geo, faceted search | Extra indexing pipeline and ops overhead | If search volume stays low and Postgres FTS meets SLA |
| Event broker | RabbitMQ | Async workflows, cross-node messaging, retries | Requires queue design and dead-letter policy | If event streams require replay at very large scale |
| Object storage + CDN | S3 + CloudFront | Media/doc storage and global delivery | Vendor-specific CDN tuning | If multicloud CDN strategy becomes mandatory |
| Frontend | Angular 21 + Ionic 8 + Tailwind 4 | SPA + mobile-web UI consistency | Learning curve with Ionic patterns | If design system cannot support required UX velocity |
| State management | NgRx Signal Store | Centralized reactive state and debugging | Boilerplate vs ad hoc BehaviorSubjects | If app complexity decreases and store overhead dominates |
| Mobile | Capacitor 8 | Shared codebase for iOS/Android | Native plugin lifecycle management | If native feature demand outgrows hybrid model |
| Payments | Stripe Connect/Billing | Escrow-like flows, payouts, subscriptions | Processor lock-in and platform fees | If target countries lack Stripe coverage |
| Notifications | FCM + SES + Twilio | Push + email + SMS omni-channel delivery | Multi-provider failure modes | If delivery reliability <99.5% for 3 months |
| Resilience | Resilience4j | Retry, circuit breakers, bulkheads, rate-limits | Tuning complexity | If failure patterns show persistent cascading despite tuning |
| Observability | Prometheus + Grafana + ELK | Metrics, alerting, structured log search | Toolchain breadth and maintenance | If operational overhead exceeds SRE capacity |
| CI/CD | GitHub Actions + Docker + ECS | Repeatable build/test/deploy pipeline | Build minutes cost and workflow complexity | If monorepo scale causes chronic CI bottlenecks |

# 4. System Architecture

## 4.0 Current State vs Target State

This section intentionally separates repository reality from target architecture to prevent planning ambiguity.

| Area | Current State (from codebase) | Target State (this SRS) | Gap Priority |
|---|---|---|---|
| Backend build/runtime | Gradle + Java 21 + Spring Boot 4 (`rental-backend/build.gradle`) | Keep stack, harden runtime controls | High |
| Frontend composition | Mixed NgModule + standalone routes | Fully standalone architecture | Medium |
| Frontend state | Service-local RxJS subjects | Centralized NgRx Signal Store | High |
| Auth token storage | `localStorage` tokens | httpOnly cookies + CSRF token flow | Critical |
| WebSocket architecture | Multiple connection entry points | Single gateway connection/service | High |
| Env configuration | Production env values incomplete | Strict environment contract per stage | High |
| Messaging | In-memory/simple broker patterns present | RabbitMQ-backed distributed messaging | High |
| Search | JPA/specification-centric | Elasticsearch/OpenSearch for advanced search | Medium |
| Security posture | Hardcoded secrets/CORS drift observed | Secrets Manager + strict CORS + policy as code | Critical |

## 4.1 High-Level Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                         CLIENTS                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐    │
│  │ Angular  │  │ iOS App  │  │ Android  │  │ Admin Dashboard  │    │
│  │   SPA    │  │(Capacitor)│  │(Capacitor)│  │   (Angular)      │    │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └───────┬──────────┘    │
└───────┼──────────────┼──────────────┼───────────────┼────────────────┘
        │              │              │               │
        └──────────────┼──────────────┼───────────────┘
                       │         HTTPS / WSS
                       ▼
            ┌──────────────────┐
            │   CloudFront     │──── Static Assets (S3)
            │   (CDN + WAF)    │
            └────────┬─────────┘
                     │
            ┌────────▼─────────┐
            │  AWS ALB         │
            │  (Load Balancer) │
            └────────┬─────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
   ┌────▼────┐ ┌────▼────┐ ┌────▼────┐
   │ API     │ │ API     │ │ API     │    ECS Fargate
   │ Node 1  │ │ Node 2  │ │ Node 3  │    (Auto-scaling)
   └────┬────┘ └────┬────┘ └────┬────┘
        │            │            │
        └────────────┼────────────┘
                     │
   ┌─────────────────┼──────────────────────┐
   │                 │                      │
   ▼                 ▼                      ▼
┌──────┐    ┌──────────────┐    ┌────────────────┐
│Redis │    │  PostgreSQL  │    │  RabbitMQ      │
│Cache │    │  (RDS HA)    │    │  (Amazon MQ)   │
│Cluster│    │  Primary +   │    │                │
│      │    │  Read Replica │    │  Exchanges:    │
└──────┘    └──────────────┘    │  - booking     │
                                │  - notification│
   ┌─────────────┐              │  - payment     │
   │Elasticsearch│              │  - search-index│
   │ (OpenSearch) │              └───────┬────────┘
   └─────────────┘                      │
                                ┌───────▼────────┐
                          ┌─────┤  Event Workers  ├─────┐
                          │     │  (Fargate)      │     │
                          │     └─────────────────┘     │
                          ▼              ▼              ▼
                    ┌──────────┐  ┌──────────┐  ┌──────────┐
                    │ Stripe   │  │ Twilio   │  │ Firebase │
                    │ Payments │  │ SMS      │  │ Push     │
                    └──────────┘  └──────────┘  └──────────┘
```

## 4.2 Domain-Driven Design Structure

```
com.homeflex/
├── domain/                          # Core business logic
│   ├── property/                    # Real estate vertical
│   │   ├── entity/                  # Property, PropertyImage, PropertyVideo
│   │   ├── repository/              # PropertyRepository
│   │   ├── service/                 # PropertyService
│   │   ├── event/                   # PropertyCreated, PropertyApproved
│   │   └── specification/           # PropertySearchSpecification
│   │
│   ├── vehicle/                     # Vehicle rental vertical
│   │   ├── entity/                  # Vehicle, VehicleImage, VehicleFeature
│   │   ├── repository/              # VehicleRepository
│   │   ├── service/                 # VehicleService
│   │   ├── event/                   # VehicleCreated, VehicleApproved
│   │   └── specification/           # VehicleSearchSpecification
│   │
│   ├── listing/                     # Shared listing abstraction
│   │   ├── entity/                  # BaseListing (abstract)
│   │   ├── service/                 # ListingOrchestrationService
│   │   └── event/                   # ListingPublished, ListingDeactivated
│   │
│   ├── booking/                     # Booking management
│   │   ├── entity/                  # Booking, BookingItem
│   │   ├── repository/              # BookingRepository
│   │   ├── service/                 # BookingService
│   │   ├── statemachine/            # BookingStateMachine
│   │   └── event/                   # BookingCreated, BookingConfirmed
│   │
│   ├── payment/                     # Payment processing
│   │   ├── entity/                  # Payment, Payout, Commission
│   │   ├── repository/              # PaymentRepository
│   │   ├── service/                 # PaymentService, EscrowService
│   │   └── event/                   # PaymentReceived, PayoutCompleted
│   │
│   ├── user/                        # User management
│   │   ├── entity/                  # User, UserProfile, KycVerification
│   │   ├── repository/              # UserRepository
│   │   ├── service/                 # UserService, KycService
│   │   └── event/                   # UserRegistered, KycApproved
│   │
│   ├── chat/                        # Real-time messaging
│   │   ├── entity/                  # ChatRoom, Message
│   │   ├── repository/              # ChatRoomRepository, MessageRepository
│   │   └── service/                 # ChatService
│   │
│   ├── review/                      # Reviews & ratings
│   │   ├── entity/                  # Review
│   │   ├── repository/              # ReviewRepository
│   │   └── service/                 # ReviewService
│   │
│   ├── notification/                # Notification engine
│   │   ├── entity/                  # Notification, NotificationPreference
│   │   ├── repository/              # NotificationRepository
│   │   ├── service/                 # NotificationService (single!)
│   │   └── channel/                 # PushChannel, EmailChannel, SmsChannel
│   │
│   ├── document/                    # Document management
│   │   ├── entity/                  # Document, LeaseAgreement
│   │   ├── repository/              # DocumentRepository
│   │   └── service/                 # DocumentService
│   │
│   └── maintenance/                 # Maintenance requests
│       ├── entity/                  # MaintenanceRequest
│       ├── repository/              # MaintenanceRequestRepository
│       └── service/                 # MaintenanceService
│
├── application/                     # Use cases & orchestration
│   ├── usecase/                     # Application-level workflows
│   │   ├── BookPropertyUseCase.java
│   │   ├── BookVehicleUseCase.java
│   │   ├── ProcessPaymentUseCase.java
│   │   └── VerifyOwnerUseCase.java
│   └── event/                       # Event handlers
│       ├── BookingEventHandler.java
│       ├── PaymentEventHandler.java
│       └── NotificationEventHandler.java
│
├── api/                             # REST & WebSocket controllers
│   ├── v1/
│   │   ├── controller/
│   │   ├── dto/request/
│   │   ├── dto/response/
│   │   └── mapper/
│   └── websocket/
│       ├── ChatWebSocketController.java
│       └── NotificationWebSocketController.java
│
├── infrastructure/                  # External service adapters
│   ├── persistence/                 # JPA configurations
│   ├── messaging/                   # RabbitMQ configurations
│   ├── search/                      # Elasticsearch configurations
│   ├── cache/                       # Redis configurations
│   ├── storage/                     # S3 adapter
│   ├── payment/                     # Stripe adapter
│   ├── notification/                # Firebase, Twilio, SES adapters
│   ├── identity/                    # Stripe Identity / KYC adapter
│   └── security/                    # JWT, OAuth, CORS, Security config
│
└── shared/                          # Cross-cutting concerns
    ├── exception/                   # Custom domain exceptions
    ├── event/                       # Base event classes
    ├── audit/                       # Audit trail
    ├── util/                        # Common utilities
    └── config/                      # App-wide config
```

## 4.3 Event-Driven Architecture

### Event Flow Example: Booking Confirmation

```
┌──────────┐     ┌──────────────┐     ┌─────────────┐
│  Renter  │────▶│ Booking API  │────▶│ BookingDB   │
│  (HTTP)  │     │ Controller   │     │ (PostgreSQL)│
└──────────┘     └──────┬───────┘     └─────────────┘
                        │
                        │ publish BookingConfirmed event
                        ▼
                 ┌──────────────┐
                 │  RabbitMQ    │
                 │  Exchange:   │
                 │  booking     │
                 └──────┬───────┘
                        │
           ┌────────────┼────────────┬──────────────┐
           ▼            ▼            ▼              ▼
    ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
    │Notify    │ │Payment   │ │Search    │ │Calendar      │
    │Worker    │ │Worker    │ │Indexer   │ │Worker        │
    │          │ │          │ │          │ │              │
    │- Email   │ │- Charge  │ │- Update  │ │- Block dates │
    │- Push    │ │  renter  │ │  listing │ │- Sync        │
    │- SMS     │ │- Escrow  │ │  avail.  │ │  calendar    │
    └──────────┘ └──────────┘ └──────────┘ └──────────────┘
```

## 4.4 Booking State Machine

```
                    ┌─────────┐
           ┌───────▶│CANCELLED│
           │        └─────────┘
           │
    ┌──────┴──┐     ┌─────────┐     ┌────────┐     ┌─────────┐
───▶│ PENDING  │────▶│CONFIRMED│────▶│ ACTIVE │────▶│COMPLETED│
    └──────┬──┘     └────┬────┘     └────┬───┘     └─────────┘
           │             │              │
           ▼             ▼              ▼
    ┌──────────┐  ┌──────────┐   ┌──────────┐
    │ REJECTED │  │CANCELLED │   │ DISPUTED │
    └──────────┘  └──────────┘   └──────┬───┘
                                        │
                                        ▼
                                 ┌──────────┐
                                 │ RESOLVED │
                                 └──────────┘
```

**State transitions:**
| From | To | Trigger | Side Effects |
|------|----|---------|-------------|
| PENDING | CONFIRMED | Owner approves | Charge renter, notify both |
| PENDING | REJECTED | Owner rejects | Notify renter, release dates |
| PENDING | CANCELLED | Renter cancels | Release dates |
| CONFIRMED | ACTIVE | Check-in date reached | Start rent collection cycle |
| CONFIRMED | CANCELLED | Either party cancels | Refund per cancellation policy |
| ACTIVE | COMPLETED | Check-out date reached | Final payment, request review |
| ACTIVE | DISPUTED | Either party raises dispute | Assign to support agent |
| DISPUTED | RESOLVED | Admin resolves | Refund/payout per resolution |

---

# 5. Functional Requirements

## 5.1 User Management

### FR-100: User Registration
| ID | FR-100 |
|----|--------|
| **Description** | Users shall register via email/password or social login (Google, Apple, Facebook) |
| **Roles** | RENTER, OWNER, ADMIN, SUPPORT_AGENT |
| **Acceptance Criteria** | |
| AC-1 | Email registration requires: email, password (min 8 chars, 1 uppercase, 1 number, 1 special), first name, last name, phone number |
| AC-2 | Email verification link sent within 30 seconds, expires in 24 hours |
| AC-3 | Social login creates account on first use, links on subsequent uses |
| AC-4 | Duplicate email registration returns descriptive error |
| AC-5 | User selects primary role (RENTER or OWNER) at registration; can add second role later |
| AC-6 | Phone number verified via OTP (Twilio) |

### FR-101: Authentication
| ID | FR-101 |
|----|--------|
| **Description** | Users shall authenticate via credentials or social providers |
| **Acceptance Criteria** | |
| AC-1 | JWT access token issued with 15-minute expiry |
| AC-2 | Refresh token issued with 7-day expiry, stored in httpOnly secure cookie |
| AC-3 | Failed login attempts: lock account after 5 failures for 15 minutes |
| AC-4 | Multi-device support: user can be logged in on web + mobile simultaneously |
| AC-5 | Logout invalidates refresh token server-side (Redis) |
| AC-6 | Password reset via email link, expires in 1 hour |

### FR-102: User Profile
| ID | FR-102 |
|----|--------|
| **Description** | Users shall manage their profile, preferences, and settings |
| **Acceptance Criteria** | |
| AC-1 | Editable fields: name, phone, bio, avatar, language preference, notification preferences |
| AC-2 | Avatar upload supports JPEG, PNG, WebP up to 5MB; auto-resized to 256x256 |
| AC-3 | Language preference persisted to user profile and synced across devices |
| AC-4 | Users can delete their account (GDPR right to erasure) with 30-day grace period |
| AC-5 | Profile completeness score displayed (% of fields filled) |

### FR-103: KYC Verification (Owners)
| ID | FR-103 |
|----|--------|
| **Description** | Asset owners must complete identity verification before listing |
| **Acceptance Criteria** | |
| AC-1 | KYC flow: Upload government ID (passport, national ID, driver's license) + selfie photo |
| AC-2 | Stripe Identity performs document verification + face matching |
| AC-3 | Address verification via utility bill or bank statement |
| AC-4 | Vehicle owners additionally upload: driver's license, vehicle registration, insurance proof |
| AC-5 | KYC status: PENDING, APPROVED, REJECTED, EXPIRED |
| AC-6 | KYC approval enables listing creation; rejection includes reason and re-submission option |
| AC-7 | KYC expires annually; re-verification required |
| AC-8 | Admin can manually override KYC status |

---

## 5.2 Listing Management

### FR-200: Property Listings
| ID | FR-200 |
|----|--------|
| **Description** | Verified owners shall create, edit, and manage property listings |
| **Acceptance Criteria** | |
| AC-1 | Required fields: title, description, property type, listing type, price, currency, address, city, country |
| AC-2 | Property types: Apartment, House, Villa, Studio, Room, Office, Land, Warehouse, Co-working Space |
| AC-3 | Listing types: Long-term Rent, Short-term Rent, Sale |
| AC-4 | Optional fields: bedrooms, bathrooms, area (sqm), floor number, total floors, year built, parking spots |
| AC-5 | Media: up to 20 images (JPEG/PNG/WebP, max 10MB each) + 3 videos (MP4, max 100MB each) |
| AC-6 | Images auto-resized to: thumbnail (150x150), medium (800x600), large (1920x1080) |
| AC-7 | Amenities: multi-select from predefined list (Wi-Fi, Parking, Pool, Gym, AC, Heating, Elevator, etc.) |
| AC-8 | Geolocation: address auto-completed via geocoding API; lat/lng stored for map search |
| AC-9 | Availability calendar: owner marks available/blocked dates |
| AC-10 | Pricing rules: base price, weekend price, weekly discount %, monthly discount % |
| AC-11 | Listing status flow: DRAFT → PENDING_REVIEW → APPROVED → ACTIVE / REJECTED |
| AC-12 | Admin reviews and approves/rejects listings (FR-600) |

### FR-201: Vehicle Listings
| ID | FR-201 |
|----|--------|
| **Description** | Verified vehicle owners shall create and manage vehicle rental listings |
| **Acceptance Criteria** | |
| AC-1 | Required fields: title, description, vehicle type, make, model, year, price/day, currency, pickup location |
| AC-2 | Vehicle types: Car, SUV, Truck, Van, Motorcycle, RV/Camper, Boat, Bicycle, Scooter |
| AC-3 | Additional fields: transmission (Auto/Manual), fuel type (Gasoline/Diesel/Electric/Hybrid), seats, doors, mileage, color |
| AC-4 | Features: multi-select (GPS, Bluetooth, Backup Camera, Child Seat, Roof Rack, Tow Hitch, etc.) |
| AC-5 | Media: up to 15 images + 2 videos |
| AC-6 | Insurance: owner uploads proof of insurance; optional renter insurance add-on |
| AC-7 | Pickup/return: address with lat/lng; options for delivery to renter (additional fee) |
| AC-8 | Mileage policy: unlimited / daily limit with overage charge per km |
| AC-9 | Availability calendar with blocked dates |
| AC-10 | Pricing: daily rate, weekly rate, monthly rate, cleaning fee, delivery fee |
| AC-11 | Vehicle condition checklist: pre-rental and post-rental photo documentation |
| AC-12 | Same approval flow as properties (DRAFT → PENDING_REVIEW → APPROVED) |

### FR-202: Listing Search & Discovery
| ID | FR-202 |
|----|--------|
| **Description** | Users shall search and discover listings with advanced filters |
| **Acceptance Criteria** | |
| AC-1 | Full-text search across title, description, address (Elasticsearch) |
| AC-2 | Geo-search: "listings within X km of location" with map view |
| AC-3 | Faceted filters with real-time count updates |
| AC-4 | Property filters: type, listing type, price range, bedrooms, bathrooms, area, amenities, availability dates |
| AC-5 | Vehicle filters: type, make, model, year range, transmission, fuel, seats, features, price/day range |
| AC-6 | Sort options: relevance, price (low/high), newest, rating, distance |
| AC-7 | Autocomplete suggestions in search bar |
| AC-8 | Saved searches: users save filter combinations; notified when new matching listings appear |
| AC-9 | Similar listings: "You might also like" recommendations on detail page |
| AC-10 | Recently viewed listings persisted in user profile |
| AC-11 | Search results: paginated (20/page) with infinite scroll |
| AC-12 | Map view with clustered markers and price labels |
| AC-13 | Comparison: select up to 4 listings for side-by-side comparison |

---

## 5.3 Booking Management

### FR-300: Create Booking
| ID | FR-300 |
|----|--------|
| **Description** | Renters shall book available listings |
| **Acceptance Criteria** | |
| AC-1 | Property booking: select check-in/check-out dates; system validates availability |
| AC-2 | Vehicle booking: select pickup/return dates and times; minimum 1 day |
| AC-3 | Price breakdown displayed: base price × nights/days + cleaning fee + service fee + taxes |
| AC-4 | Service fee: 10% of subtotal charged to renter |
| AC-5 | Double-booking prevention: dates locked using Redis distributed lock during checkout (5-minute hold) |
| AC-6 | Renter adds message to owner with booking request |
| AC-7 | Payment processed via Stripe at booking time; held in escrow until owner confirms |
| AC-8 | Booking confirmation sent via email + push + SMS |

### FR-301: Manage Booking
| ID | FR-301 |
|----|--------|
| **Description** | Owners and renters shall manage booking lifecycle |
| **Acceptance Criteria** | |
| AC-1 | Owner accepts/rejects within 48 hours; auto-reject after timeout |
| AC-2 | Cancellation policies: Flexible (full refund 24h before), Moderate (full refund 5 days before), Strict (50% refund 7 days before) |
| AC-3 | Owner cancellation: full refund to renter, penalty fee to owner, listing quality score reduced |
| AC-4 | Check-in/check-out confirmation by both parties |
| AC-5 | Booking modification: date changes require owner approval and price recalculation |
| AC-6 | Booking history accessible with filters (status, date range, listing type) |

### FR-302: Post-Booking
| ID | FR-302 |
|----|--------|
| **Description** | Post-booking actions and follow-ups |
| **Acceptance Criteria** | |
| AC-1 | Review prompt sent 24 hours after checkout |
| AC-2 | Vehicle return: condition checklist with photo comparison (pre vs post) |
| AC-3 | Damage claim: owner submits within 48 hours of return with photos and cost estimate |
| AC-4 | Security deposit: held for 7 days after checkout; released if no damage claim |
| AC-5 | Maintenance requests: renter submits during active property booking |

---

## 5.4 Payment System

### FR-400: Payment Processing
| ID | FR-400 |
|----|--------|
| **Description** | Secure payment processing with escrow and payouts |
| **Acceptance Criteria** | |
| AC-1 | Payment methods: Credit/debit card (Visa, Mastercard, Amex), SEPA direct debit (EU), bank transfer |
| AC-2 | All payments processed via Stripe; HomeFlex never stores card numbers |
| AC-3 | Escrow: funds held by Stripe until service delivery confirmed |
| AC-4 | Payout to owner: automatic after booking completion, minus platform commission (15%) |
| AC-5 | Payout schedule: configurable (instant, daily, weekly) via Stripe Connect |
| AC-6 | Refund processing: within 5-10 business days per cancellation policy |
| AC-7 | Multi-currency: prices displayed in renter's preferred currency; conversion at booking time |
| AC-8 | Invoice generation: PDF invoices for each payment with VAT/tax details |
| AC-9 | Recurring payments: monthly rent charged automatically on first of each month |
| AC-10 | Payment failure: 3 automatic retries over 7 days; booking suspended after final failure |

### FR-401: Financial Dashboard (Owners)
| ID | FR-401 |
|----|--------|
| **Description** | Owners shall view their financial overview |
| **Acceptance Criteria** | |
| AC-1 | Dashboard shows: total earnings, pending payouts, completed payouts, commission paid |
| AC-2 | Transaction history with export to CSV |
| AC-3 | Monthly/yearly earnings chart |
| AC-4 | Upcoming payout schedule |
| AC-5 | Tax documents: annual summary for tax reporting |

---

## 5.5 Communication

### FR-500: Real-Time Chat
| ID | FR-500 |
|----|--------|
| **Description** | Renters and owners shall communicate via real-time messaging |
| **Acceptance Criteria** | |
| AC-1 | Chat room auto-created when renter contacts owner about a listing |
| AC-2 | Real-time message delivery via WebSocket (STOMP/SockJS) |
| AC-3 | Message types: text, image, document (PDF for lease agreements) |
| AC-4 | Read receipts and typing indicators |
| AC-5 | Message history paginated (50 messages/page) |
| AC-6 | Push notification for new messages when app is backgrounded |
| AC-7 | Chat room linked to specific listing for context |
| AC-8 | Report abusive messages to admin |
| AC-9 | Chat available to registered users only |

### FR-501: Notifications
| ID | FR-501 |
|----|--------|
| **Description** | Multi-channel notification system |
| **Acceptance Criteria** | |
| AC-1 | Channels: in-app, push (FCM), email (SES), SMS (Twilio), WhatsApp (Twilio) |
| AC-2 | Notification categories: Booking, Payment, Message, System, Marketing |
| AC-3 | User configures preferences per category per channel |
| AC-4 | In-app notifications: bell icon with unread count badge |
| AC-5 | Critical notifications (payment failure, booking cancellation) sent on all enabled channels |
| AC-6 | Notification templates localized per user's language preference |

---

## 5.6 Administration

### FR-600: Admin Dashboard
| ID | FR-600 |
|----|--------|
| **Description** | Platform administrators manage content, users, and monitor health |
| **Acceptance Criteria** | |
| AC-1 | Dashboard overview: total users, active listings, bookings today, revenue this month, pending KYC, reported content |
| AC-2 | Listing moderation: approve/reject queue with listing preview |
| AC-3 | User management: view, suspend, ban, delete users; view activity history |
| AC-4 | KYC management: review pending verifications, approve/reject with reason |
| AC-5 | Report management: view reported listings and messages; take action (warn, remove, ban) |
| AC-6 | Dispute resolution: view disputed bookings, communicate with parties, issue refunds/payouts |
| AC-7 | Analytics: user growth, booking trends, revenue by region, listing distribution, platform health |
| AC-8 | System config: manage amenity/feature lists, commission rates, cancellation policies |
| AC-9 | Audit log: all admin actions logged with timestamp, actor, and description |

### FR-601: Support Agent Tools
| ID | FR-601 |
|----|--------|
| **Description** | Support agents resolve user issues |
| **Acceptance Criteria** | |
| AC-1 | Ticket system: users submit support requests; assigned to agents |
| AC-2 | Agent can view user profile, booking history, payment history, and chat history in one view |
| AC-3 | Agent can issue partial/full refunds with admin approval |
| AC-4 | Canned responses for common issues |
| AC-5 | Escalation to admin for disputes involving >$500 |

---

## 5.7 Reviews & Trust

### FR-700: Reviews
| ID | FR-700 |
|----|--------|
| **Description** | Two-way review system |
| **Acceptance Criteria** | |
| AC-1 | After booking completion, both renter and owner can review each other |
| AC-2 | Review fields: rating (1-5 stars), text comment, optional photos |
| AC-3 | Category ratings for properties: cleanliness, accuracy, communication, location, value |
| AC-4 | Category ratings for vehicles: cleanliness, accuracy, communication, condition, value |
| AC-5 | Reviews visible after both parties submit (or 14 days pass) |
| AC-6 | Reviews cannot be edited after submission; owner can post a public response |
| AC-7 | Aggregate rating displayed on listing and user profile |

### FR-701: Trust Score
| ID | FR-701 |
|----|--------|
| **Description** | Trust scoring for users |
| **Acceptance Criteria** | |
| AC-1 | Trust score calculated from: KYC status, review average, response rate, booking completion rate, account age |
| AC-2 | Badge levels: New, Verified, Trusted, Superhost/Top Owner |
| AC-3 | Badge displayed on profile and listings |
| AC-4 | Owners with trust score <3.0 flagged for review |

---

## 5.8 Document Management

### FR-800: Documents
| ID | FR-800 |
|----|--------|
| **Description** | Document storage and management |
| **Acceptance Criteria** | |
| AC-1 | Document types: lease agreement, insurance policy, vehicle registration, ID documents, inspection reports |
| AC-2 | Upload: PDF, JPEG, PNG up to 20MB |
| AC-3 | Documents linked to specific bookings or listings |
| AC-4 | Access control: only involved parties + admin can view |
| AC-5 | Digital lease signing: both parties sign lease document within the platform |
| AC-6 | Document expiry tracking with renewal reminders |

---

## 5.9 Maintenance Requests

### FR-900: Maintenance
| ID | FR-900 |
|----|--------|
| **Description** | Renters report maintenance issues during active property bookings |
| **Acceptance Criteria** | |
| AC-1 | Renter submits: category (plumbing, electrical, appliance, structural, other), description, photos, urgency (low/medium/high/emergency) |
| AC-2 | Owner receives notification and responds with: acknowledgment, estimated resolution date, assigned contractor info |
| AC-3 | Status tracking: SUBMITTED → ACKNOWLEDGED → IN_PROGRESS → RESOLVED → CLOSED |
| AC-4 | Emergency requests trigger SMS + push to owner immediately |
| AC-5 | Maintenance history visible to both parties |

---

# 6. Non-Functional Requirements

## 6.1 Performance

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-P1 | API response time (95th percentile) | < 200ms |
| NFR-P2 | Search query response time (95th percentile) | < 300ms |
| NFR-P3 | Page load time (first contentful paint) | < 1.5s |
| NFR-P4 | Time to interactive | < 3s |
| NFR-P5 | WebSocket message delivery latency | < 100ms |
| NFR-P6 | Image upload processing (resize + CDN) | < 5s |
| NFR-P7 | Concurrent users supported | 10,000+ |
| NFR-P8 | API throughput | 1,000 req/s per node |
| NFR-P9 | Database query time (95th percentile) | < 50ms |
| NFR-P10 | Elasticsearch query time (95th percentile) | < 100ms |

## 6.2 Availability & Reliability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-A1 | Platform uptime | 99.9% (8.76h downtime/year) |
| NFR-A2 | Recovery Time Objective (RTO) | < 15 minutes |
| NFR-A3 | Recovery Point Objective (RPO) | < 1 minute |
| NFR-A4 | Database backup frequency | Continuous (point-in-time recovery) |
| NFR-A5 | Zero-downtime deployments | Required |
| NFR-A6 | Multi-AZ deployment | Required for all data stores |
| NFR-A7 | Graceful degradation | If Elasticsearch is down, fallback to PostgreSQL search |

## 6.3 Scalability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-S1 | Horizontal scaling | Auto-scale API from 2 to 50 nodes |
| NFR-S2 | Database scaling | Read replicas for read-heavy queries |
| NFR-S3 | Cache scaling | Redis cluster mode with sharding |
| NFR-S4 | Media storage | Unlimited (S3) |
| NFR-S5 | Listing capacity | 5M+ active listings |
| NFR-S6 | User capacity | 1M+ registered users |
| NFR-S7 | Message throughput | 10K messages/second |

## 6.4 Security

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-SEC1 | Authentication | JWT + httpOnly cookies + CSRF |
| NFR-SEC2 | Encryption at rest | AES-256 for all data stores |
| NFR-SEC3 | Encryption in transit | TLS 1.3 for all connections |
| NFR-SEC4 | PCI DSS compliance | Level 1 (via Stripe) |
| NFR-SEC5 | GDPR compliance | Data portability, right to erasure, consent management |
| NFR-SEC6 | Rate limiting | Per-user and per-IP |
| NFR-SEC7 | Input validation | All user inputs sanitized (OWASP) |
| NFR-SEC8 | WAF | AWS WAF on ALB and CloudFront |
| NFR-SEC9 | Secrets management | AWS Secrets Manager (no config files) |
| NFR-SEC10 | Penetration testing | Annual third-party pentest |
| NFR-SEC11 | Dependency scanning | Automated CVE scanning in CI |

## 6.5 Accessibility

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-ACC1 | WCAG compliance | Level AA (WCAG 2.2) |
| NFR-ACC2 | Screen reader support | All interactive elements labeled |
| NFR-ACC3 | Keyboard navigation | Full app navigable without mouse |
| NFR-ACC4 | Color contrast | Minimum 4.5:1 ratio |
| NFR-ACC5 | Responsive design | Mobile-first, 320px to 4K |

## 6.6 Maintainability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-M1 | Code coverage | > 80% (unit + integration) |
| NFR-M2 | API documentation | Auto-generated OpenAPI 3.1 |
| NFR-M3 | Code style | Enforced via linters (Checkstyle, ESLint, Prettier) |
| NFR-M4 | Dependency updates | Monthly security patch cycle |
| NFR-M5 | Database migrations | Versioned via Flyway |

---

# 7. Data Model & Database Design

## 7.1 Entity Relationship Summary

### Core Entities

```
┌────────────┐       ┌──────────────┐       ┌──────────┐
│   User     │1─────*│   Listing    │1─────*│  Booking  │
│            │       │  (abstract)  │       │           │
│ id (UUID)  │       │ id (UUID)    │       │ id (UUID) │
│ email      │       │ owner_id(FK) │       │ listing_id│
│ role       │       │ title        │       │ renter_id │
│ kyc_status │       │ description  │       │ status    │
│ trust_score│       │ price        │       │ total_amt │
└────────────┘       │ status       │       │ dates     │
      │              │ vertical     │       └──────────┘
      │              └──────────────┘             │
      │                    │                      │
      │         ┌──────────┴──────────┐           │
      │         │                     │           │
      │    ┌────▼─────┐    ┌─────────▼┐    ┌────▼──────┐
      │    │ Property  │    │ Vehicle  │    │ Payment   │
      │    │           │    │          │    │           │
      │    │ bedrooms  │    │ make     │    │ stripe_id │
      │    │ bathrooms │    │ model    │    │ amount    │
      │    │ area_sqm  │    │ year     │    │ status    │
      │    │ amenities │    │ seats    │    │ type      │
      │    └──────────┘    │ features │    └───────────┘
      │                    └──────────┘
      │
      │    ┌──────────┐    ┌───────────┐   ┌──────────────┐
      ├───*│ Review   │    │ ChatRoom  │   │ Notification │
      │    │ rating   │    │ listing_id│   │ user_id      │
      │    │ comment  │    │ renter_id │   │ type         │
      │    └──────────┘    │ owner_id  │   │ channel      │
      │                    └─────┬─────┘   │ read         │
      │                          │         └──────────────┘
      │                    ┌─────▼─────┐
      │                    │ Message   │
      │                    │ content   │
      │                    │ sender_id │
      │                    │ read_at   │
      │                    └───────────┘
      │
      ├───*┌──────────────┐
      │    │KycVerification│
      │    │ document_type │
      │    │ document_url  │
      │    │ status        │
      │    │ verified_at   │
      │    └──────────────┘
      │
      └───*┌──────────────┐
           │ Document     │
           │ type         │
           │ file_url     │
           │ booking_id   │
           └──────────────┘
```

## 7.2 Key Design Decisions

### 7.2.1 Listing Abstraction (Table-per-subclass)
```sql
-- Base listing table (shared fields)
CREATE TABLE listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES users(id),
    vertical VARCHAR(20) NOT NULL, -- 'PROPERTY' or 'VEHICLE'
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) DEFAULT 'DRAFT',
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    city VARCHAR(100),
    country VARCHAR(100),
    view_count INTEGER DEFAULT 0,
    favorite_count INTEGER DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP, -- soft delete
    version INTEGER DEFAULT 0 -- optimistic locking
);

-- Property-specific fields
CREATE TABLE properties (
    listing_id UUID PRIMARY KEY REFERENCES listings(id),
    property_type VARCHAR(30),
    listing_type VARCHAR(20),
    bedrooms INTEGER,
    bathrooms INTEGER,
    area_sqm DECIMAL(8,2),
    floor_number INTEGER,
    total_floors INTEGER,
    year_built INTEGER,
    parking_spots INTEGER
);

-- Vehicle-specific fields
CREATE TABLE vehicles (
    listing_id UUID PRIMARY KEY REFERENCES listings(id),
    vehicle_type VARCHAR(30),
    make VARCHAR(50),
    model VARCHAR(50),
    year INTEGER,
    transmission VARCHAR(20),
    fuel_type VARCHAR(20),
    seats INTEGER,
    doors INTEGER,
    mileage INTEGER,
    color VARCHAR(30),
    license_plate VARCHAR(20),
    mileage_policy VARCHAR(20),
    daily_mileage_limit INTEGER,
    overage_charge_per_km DECIMAL(6,2)
);
```

### 7.2.2 Indexes Strategy
```sql
-- High-priority indexes
CREATE INDEX idx_listings_owner ON listings(owner_id);
CREATE INDEX idx_listings_status ON listings(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_listings_vertical ON listings(vertical);
CREATE INDEX idx_listings_city ON listings(city);
CREATE INDEX idx_listings_geo ON listings USING GIST (
    ST_MakePoint(longitude, latitude)
);
CREATE INDEX idx_listings_price ON listings(price);
CREATE INDEX idx_listings_created ON listings(created_at DESC);

CREATE INDEX idx_bookings_renter ON bookings(renter_id);
CREATE INDEX idx_bookings_listing ON bookings(listing_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(check_in_date, check_out_date);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE INDEX idx_messages_room ON messages(chat_room_id, created_at DESC);
CREATE INDEX idx_notifications_user ON notifications(user_id, read, created_at DESC);
```

### 7.2.3 Audit Columns (all tables)
```sql
created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
created_by  UUID REFERENCES users(id),
updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
updated_by  UUID REFERENCES users(id),
deleted_at  TIMESTAMP,  -- NULL = active, non-NULL = soft-deleted
version     INTEGER DEFAULT 0  -- optimistic locking
```

---

# 8. API Design

## 8.1 API Conventions

| Convention | Standard |
|-----------|----------|
| Base path | `/api/v1/` |
| Format | JSON (application/json) |
| Authentication | Bearer token in Authorization header |
| Pagination | `?page=0&size=20&sort=createdAt,desc` |
| Error format | `{ "timestamp", "status", "error", "message", "path", "fieldErrors[]" }` |
| Date format | ISO 8601 (`2026-03-24T14:30:00Z`) |
| ID format | UUID v4 |
| Naming | camelCase for JSON fields |
| Versioning | URI path (`/v1/`, `/v2/`) |

## 8.2 Endpoint Summary

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login with credentials |
| POST | `/api/v1/auth/oauth/{provider}` | Social login (google/apple/facebook) |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| POST | `/api/v1/auth/logout` | Invalidate session |
| POST | `/api/v1/auth/forgot-password` | Request password reset |
| POST | `/api/v1/auth/reset-password` | Reset password with token |
| POST | `/api/v1/auth/verify-email/{token}` | Verify email address |
| POST | `/api/v1/auth/verify-phone` | Verify phone via OTP |

### Listings (Polymorphic)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/listings` | Search all listings (properties + vehicles) |
| GET | `/api/v1/listings/{id}` | Get listing detail (returns property or vehicle) |
| POST | `/api/v1/listings/{id}/view` | Increment view count |
| GET | `/api/v1/listings/{id}/similar` | Get similar listings |
| POST | `/api/v1/listings/{id}/report` | Report listing |
| GET | `/api/v1/listings/comparison` | Compare listings (?ids=a,b,c,d) |

### Properties
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/properties` | Search properties with filters |
| GET | `/api/v1/properties/{id}` | Get property detail |
| POST | `/api/v1/properties` | Create property listing |
| PUT | `/api/v1/properties/{id}` | Update property |
| DELETE | `/api/v1/properties/{id}` | Soft-delete property |
| POST | `/api/v1/properties/{id}/images` | Upload images |
| DELETE | `/api/v1/properties/{id}/images/{imageId}` | Delete image |
| PUT | `/api/v1/properties/{id}/availability` | Update availability calendar |

### Vehicles
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/vehicles` | Search vehicles with filters |
| GET | `/api/v1/vehicles/{id}` | Get vehicle detail |
| POST | `/api/v1/vehicles` | Create vehicle listing |
| PUT | `/api/v1/vehicles/{id}` | Update vehicle |
| DELETE | `/api/v1/vehicles/{id}` | Soft-delete vehicle |
| POST | `/api/v1/vehicles/{id}/images` | Upload images |
| PUT | `/api/v1/vehicles/{id}/availability` | Update availability calendar |
| POST | `/api/v1/vehicles/{id}/condition` | Submit condition report |

### Bookings
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/bookings` | Create booking |
| GET | `/api/v1/bookings` | List my bookings (renter/owner) |
| GET | `/api/v1/bookings/{id}` | Get booking detail |
| PUT | `/api/v1/bookings/{id}/confirm` | Owner confirms booking |
| PUT | `/api/v1/bookings/{id}/reject` | Owner rejects booking |
| PUT | `/api/v1/bookings/{id}/cancel` | Cancel booking |
| PUT | `/api/v1/bookings/{id}/check-in` | Confirm check-in |
| PUT | `/api/v1/bookings/{id}/check-out` | Confirm check-out |
| POST | `/api/v1/bookings/{id}/dispute` | Raise dispute |
| POST | `/api/v1/bookings/{id}/maintenance` | Submit maintenance request |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/payments/intent` | Create payment intent (Stripe) |
| GET | `/api/v1/payments` | List my payments |
| GET | `/api/v1/payments/{id}` | Get payment detail |
| POST | `/api/v1/payments/{id}/refund` | Request refund |
| GET | `/api/v1/payouts` | List my payouts (owner) |
| GET | `/api/v1/payouts/summary` | Financial summary |
| POST | `/api/v1/webhooks/stripe` | Stripe webhook receiver |

### Chat
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/chat/rooms` | List my chat rooms |
| POST | `/api/v1/chat/rooms` | Create/get chat room for listing |
| GET | `/api/v1/chat/rooms/{id}/messages` | Get messages (paginated) |
| POST | `/api/v1/chat/rooms/{id}/messages` | Send message (REST fallback) |
| PUT | `/api/v1/chat/messages/{id}/read` | Mark message as read |

### WebSocket Endpoints
| Destination | Direction | Description |
|-------------|-----------|-------------|
| `/ws` | Connect | STOMP connection endpoint (SockJS) |
| `/app/chat.send` | Client → Server | Send chat message |
| `/app/chat.typing` | Client → Server | Typing indicator |
| `/topic/chat.{roomId}` | Server → Client | Receive chat messages |
| `/user/queue/notifications` | Server → Client | Personal notifications |
| `/user/queue/booking-updates` | Server → Client | Booking status changes |

### Users, Reviews, Favorites, Notifications, Documents, Admin
*(Similar RESTful patterns — full endpoint list in Appendix A)*

---

# 9. Security Architecture

## 9.1 Authentication Flow

```
┌────────┐                    ┌─────────┐                    ┌───────┐
│ Client │                    │   API   │                    │ Redis │
└───┬────┘                    └────┬────┘                    └───┬───┘
    │  POST /auth/login            │                             │
    │  {email, password}           │                             │
    │─────────────────────────────▶│                             │
    │                              │ Validate credentials        │
    │                              │ Generate JWT (15min)        │
    │                              │ Generate Refresh Token(7d)  │
    │                              │────────────────────────────▶│
    │                              │  Store refresh token        │
    │  200 OK                      │◀────────────────────────────│
    │  Body: {accessToken}         │                             │
    │  Cookie: refreshToken(httpOnly, secure, sameSite=strict)   │
    │◀─────────────────────────────│                             │
    │                              │                             │
    │  GET /api/v1/properties      │                             │
    │  Header: Authorization:      │                             │
    │    Bearer <accessToken>      │                             │
    │─────────────────────────────▶│                             │
    │                              │ Validate JWT                │
    │  200 OK {properties}         │                             │
    │◀─────────────────────────────│                             │
    │                              │                             │
    │  (15min later - token expired)│                            │
    │  POST /auth/refresh          │                             │
    │  Cookie: refreshToken        │                             │
    │─────────────────────────────▶│                             │
    │                              │────────────────────────────▶│
    │                              │  Validate refresh token     │
    │                              │  Rotate refresh token       │
    │                              │◀────────────────────────────│
    │  200 OK {newAccessToken}     │                             │
    │  Cookie: newRefreshToken     │                             │
    │◀─────────────────────────────│                             │
```

## 9.2 Authorization Model (RBAC)

| Resource | RENTER | OWNER | ADMIN | SUPPORT |
|----------|--------|-------|-------|---------|
| Create listing | - | Write own | Write any | - |
| View listing | Read public | Read own + public | Read any | Read any |
| Create booking | Write own | - | - | - |
| Manage booking | Own bookings | Own listings' bookings | Any | View only |
| Chat | Participant only | Participant only | Any | Assigned tickets |
| Payments | Own | Own | View any | View assigned |
| Admin dashboard | - | - | Full access | Limited |
| KYC management | - | - | Full access | View only |
| User management | - | - | Full access | View only |

## 9.3 Security Controls

| Control | Implementation |
|---------|---------------|
| Input validation | Jakarta Bean Validation + custom validators |
| Output encoding | Jackson auto-escaping + CSP headers |
| SQL injection | JPA parameterized queries (never string concat) |
| XSS prevention | CSP header + httpOnly cookies + input sanitization |
| CSRF | Double-submit cookie pattern (since cookies used for refresh token) |
| Rate limiting | Resilience4j + Redis (per-user + per-IP) |
| Secrets | AWS Secrets Manager (rotated quarterly) |
| Headers | Strict-Transport-Security, X-Content-Type-Options, X-Frame-Options |
| File upload | Virus scan (ClamAV), type validation, size limits |
| Dependencies | Dependabot + Snyk scanning in CI |

## 9.4 Security Hardening Requirements (Mandatory)

| ID | Requirement | Implementation Requirement | Verification |
|---|---|---|---|
| SEC-01 | Secret management | No secrets in source-controlled config. All app secrets loaded at runtime from AWS Secrets Manager with KMS encryption and 90-day rotation policy. | CI secret scan + quarterly access audit |
| SEC-02 | CORS policy | No wildcard origins with credentials. Explicit allow-list by environment (`dev`, `staging`, `prod`) and deny-by-default behavior. | Automated integration test on preflight/credentialed requests |
| SEC-03 | Token storage | Access token short TTL in memory; refresh token in `httpOnly`, `secure`, `sameSite=strict` cookie. No auth tokens in `localStorage`. | Browser security test + penetration test |
| SEC-04 | CSRF defense | Double-submit cookie or synchronizer token pattern enabled for cookie-authenticated endpoints. | Security integration tests in CI |
| SEC-05 | API abuse protection | Rate limits on public/auth/search endpoints via Redis-backed token bucket; WAF managed rules at edge. | Load test and WAF dashboards |
| SEC-06 | Auditability | Security-sensitive events (login, token refresh, role changes, KYC status, payouts) written to immutable audit log stream. | Monthly audit sample review |
| SEC-07 | Data lifecycle controls | Soft delete (`deletedAt`) for user-generated entities, retention and legal hold policy for contracts/KYC docs. | Data retention compliance checks |
| SEC-08 | Integrity/concurrency | Optimistic locking (`@Version`) on booking/payment critical entities to prevent race-condition overwrites. | Concurrent write test suite |
| SEC-09 | File upload safety | Content-type and extension validation, malware scanning, quarantine workflow on suspicious files. | E2E upload security tests |
| SEC-10 | Dependency and image risk | SBOM generation, CVE gates in CI for app dependencies and container base images. | CI policy enforcement reports |

---

# 10. Infrastructure & Deployment

## 10.1 Multi-Region Architecture

```
                         Route 53
                    (Latency-based routing)
                           │
              ┌────────────┼────────────┐
              │            │            │
        ┌─────▼─────┐┌────▼─────┐┌────▼─────┐
        │ us-east-1 ││eu-west-1 ││me-south-1│
        │ (N.America)││ (Europe) ││  (MENA)  │
        │           ││          ││          │
        │ ECS       ││ ECS      ││ ECS      │
        │ Redis     ││ Redis    ││ Redis    │
        │ RDS Read  ││ RDS Read ││ RDS Read │
        │ OpenSearch ││ OpenSearch││OpenSearch│
        └─────┬─────┘└────┬─────┘└────┬─────┘
              │            │            │
              └────────────┼────────────┘
                           │
                    ┌──────▼──────┐
                    │ Primary DB  │
                    │ us-east-1   │
                    │ (RDS Multi- │
                    │  AZ + Cross │
                    │  Region     │
                    │  Replicas)  │
                    └─────────────┘
```

### 10.1.1 AWS Region and Data Residency Strategy

- Primary write region: `us-east-1`.
- Active read regions: `eu-west-1`, `me-south-1`.
- Data residency: personally identifiable data is logically partitioned by tenant/region; region-specific legal constraints enforced by data access policy.
- Geo-routing: Route 53 latency routing with health checks; region failover to nearest healthy region.
- Disaster recovery targets: RPO <= 5 minutes, RTO <= 30 minutes for critical booking/payment APIs.

### 10.1.2 Managed Service Mapping (AWS)

| Capability | AWS Service | Responsibility |
|---|---|---|
| API compute | ECS Fargate | Stateless API and worker workloads with autoscaling |
| Relational database | Amazon RDS for PostgreSQL | Transactions, read replicas, cross-region replication |
| Cache/session/rate limit | ElastiCache Redis | Caching, distributed locks, token/session metadata |
| Async events and chat fanout | Amazon MQ (RabbitMQ engine) | Queueing, retries, dead-letter routing |
| Search and analytics index | Amazon OpenSearch Service | Full-text + faceted + geo search |
| Media/doc storage | Amazon S3 | Durable object storage for listings and KYC docs |
| Global static/media delivery | CloudFront | Edge caching and acceleration |
| Edge security | AWS WAF + Shield | Layer 7 protections and DDoS baseline |
| Secret and key management | Secrets Manager + KMS | Secret retrieval, encryption, key rotation |

## 10.2 Environment Strategy

| Environment | Purpose | Infrastructure |
|-------------|---------|---------------|
| **local** | Developer machine | Docker Compose (all services) |
| **dev** | Integration testing | Single ECS cluster, shared RDS |
| **staging** | Pre-production validation | Production-mirror, synthetic data |
| **production** | Live users | Multi-region, auto-scaling, HA |

## 10.3 CI/CD Pipeline

```
Code Push → GitHub Actions:
  1. Lint + Format check
  2. Unit tests (parallel: backend + frontend)
  3. Integration tests (Testcontainers)
  4. Security scan (Snyk + Trivy)
  5. Build Docker images
  6. Push to ECR
  7. Deploy to staging (auto)
  8. E2E tests on staging
  9. Deploy to production (manual approval)
  10. Smoke tests
  11. Monitor for 30 minutes
  12. Rollback if error rate > 1%
```

---

# 11. Scalability & Performance Strategy

## 11.1 Caching Strategy

| Data | Cache | TTL | Invalidation |
|------|-------|-----|-------------|
| Property search results | Redis | 5 min | On listing update event |
| Listing detail | Redis | 15 min | On listing update event |
| User profile | Redis | 30 min | On profile update |
| Amenity/feature lists | Redis | 24 hours | On admin change |
| Stats/analytics | Redis | 10 min | Timer-based |
| Static assets | CloudFront | 1 year | Cache-busting hash in filename |
| API responses | Client HTTP cache | Varies | ETag / Last-Modified |

## 11.2 Database Optimization

- **Connection pooling**: HikariCP with 20 max connections per node
- **Read replicas**: Route read queries to replicas via `@Transactional(readOnly=true)`
- **Partitioning**: Messages table partitioned by month; bookings by year
- **Query optimization**: All Specification queries have corresponding composite indexes
- **Batch operations**: Hibernate batch size 20 for bulk inserts
- **N+1 prevention**: `@EntityGraph` on all repository methods returning collections

## 11.3 Frontend Performance

- **Lazy loading**: All feature routes lazy-loaded
- **Virtual scrolling**: CDK Virtual Scroll for listing grids (render only visible items)
- **Image optimization**: WebP format, srcset for responsive sizes, lazy loading with `loading="lazy"`
- **Bundle budget**: < 200KB initial JavaScript (gzipped)
- **Service worker**: Cache API responses for offline browsing of recently viewed listings
- **Preloading**: `PreloadAllModules` strategy for instant route transitions

---

# 12. Monitoring & Observability

## 12.1 Metrics (Prometheus)

| Category | Metrics |
|----------|---------|
| **Business** | Bookings/hour, revenue/day, new users/day, listings/day |
| **API** | Request rate, error rate, latency (p50, p95, p99), by endpoint |
| **Database** | Query time, connection pool utilization, slow queries |
| **Cache** | Hit rate, miss rate, eviction rate, memory usage |
| **Queue** | Queue depth, consumer lag, processing time, DLQ size |
| **JVM** | Heap usage, GC pause time, thread count |
| **Infrastructure** | CPU, memory, disk I/O, network I/O |

## 12.2 Alerting Rules

| Alert | Condition | Severity | Channel |
|-------|-----------|----------|---------|
| High error rate | 5xx rate > 1% for 5 min | Critical | PagerDuty + Slack |
| API latency | p95 > 500ms for 10 min | Warning | Slack |
| Database CPU | > 80% for 15 min | Warning | Slack |
| Queue backlog | > 1000 messages for 10 min | Warning | Slack |
| Disk usage | > 85% | Warning | Email |
| Certificate expiry | < 30 days | Warning | Email |
| Payment failures | > 5% failure rate | Critical | PagerDuty + Slack |

## 12.3 Distributed Tracing

- **Correlation ID**: Every request gets a UUID correlation ID, propagated through HTTP headers, RabbitMQ message headers, and log entries
- **Trace context**: OpenTelemetry integration for end-to-end trace visibility across API → Database → Cache → Queue → Worker
- **Grafana Tempo**: Trace storage and visualization, linked from Grafana dashboards

---

# 13. Internationalization & Multi-Region

## 13.1 Language Support

| Language | Code | Region | Status |
|----------|------|--------|--------|
| English | en | Global | Primary |
| French | fr | France, North Africa | Supported |
| Arabic | ar | MENA | Planned |
| Spanish | es | Spain, Latin America | Planned |

## 13.2 Localization Scope

| Element | Strategy |
|---------|----------|
| UI strings | ngx-translate JSON files per language |
| Dates | `date-fns` with locale-aware formatting |
| Currency | Display in user's preferred currency; stored in listing's native currency |
| Addresses | Region-specific format (postal code before/after city) |
| Phone numbers | International format with country code |
| RTL support | Arabic requires right-to-left layout (CSS `direction: rtl`) |
| Legal content | Region-specific terms of service, privacy policy |
| Email templates | Localized per user's language preference |
| Push notifications | Localized message templates |

## 13.3 Multi-Currency

- Prices stored in the listing's native currency (owner sets this)
- Display converted to renter's preferred currency using exchange rates
- Exchange rates fetched daily from a reliable API (e.g., Open Exchange Rates)
- Booking is charged in the listing's native currency (Stripe handles conversion)
- Exchange rate at booking time is locked and stored with the booking

---

# 14. Third-Party Integrations

| Service | Provider | Purpose | Fallback |
|---------|----------|---------|----------|
| Payment processing | Stripe Connect | Marketplace payments, escrow, payouts | None (critical path) |
| KYC verification | Stripe Identity | Document + selfie verification | Manual admin review |
| Push notifications | Firebase Cloud Messaging | iOS + Android push | In-app notifications |
| Email | AWS SES | Transactional email | Queue and retry |
| SMS | Twilio | OTP, booking alerts | Email fallback |
| WhatsApp | Twilio | Rich notifications (MENA) | SMS fallback |
| Geocoding | OpenStreetMap Nominatim | Address → lat/lng | Google Geocoding API |
| Maps | Leaflet + OSM tiles | Interactive maps | Static map images |
| OAuth | Google, Apple, Facebook | Social login | Email/password login |
| File storage | AWS S3 | Media storage | None (critical path) |
| CDN | AWS CloudFront | Global content delivery | Direct S3 access |
| Monitoring | Prometheus + Grafana | Metrics and dashboards | AWS CloudWatch |
| Logging | ELK Stack | Centralized logs | CloudWatch Logs |
| CI/CD | GitHub Actions | Build and deploy | Manual deployment |
| Container registry | AWS ECR | Docker image storage | DockerHub |
| DNS | AWS Route 53 | Latency-based routing | CloudFlare |
| WAF | AWS WAF | API protection | Nginx rate limiting |
| Secrets | AWS Secrets Manager | Credentials management | Environment variables |

---

# 15. Mobile Strategy

## 15.1 Platform Support

| Platform | Minimum Version | Build Tool |
|----------|----------------|-----------|
| iOS | 16.0+ | Xcode + Capacitor |
| Android | API 24 (Android 7.0)+ | Android Studio + Capacitor |
| Web | Modern browsers (Chrome 111+, Safari 16.4+, Firefox 128+) | Angular CLI |

## 15.2 Native Features

| Feature | Capacitor Plugin | Usage |
|---------|-----------------|-------|
| Camera | @capacitor/camera | Profile photos, listing images, condition reports |
| Geolocation | @capacitor/geolocation | Map search, nearby listings |
| Push notifications | @capacitor/push-notifications | Real-time alerts |
| Biometrics | @capacitor/biometrics | Fingerprint/FaceID login |
| Share | @capacitor/share | Share listings to social media |
| App updates | @capawesome/capacitor-app-update | In-app update prompts |
| File system | @capacitor/filesystem | Document downloads |
| Status bar | @capacitor/status-bar | Native appearance |

## 15.3 Offline Strategy

| Feature | Offline Behavior |
|---------|-----------------|
| Listing browsing | Recently viewed listings cached locally |
| Search | Last search results available offline |
| Chat | Messages queued and sent when reconnected |
| Bookings | View existing bookings; new bookings require connectivity |
| Media | Uploaded images queued until connectivity restored |

---

# 16. Testing Strategy

## 16.1 Testing Pyramid

| Level | Tool | Coverage Target | What to Test |
|-------|------|----------------|-------------|
| **Unit** | JUnit 5 + Mockito (backend), Jasmine (frontend) | 80%+ | Business logic, validators, mappers |
| **Integration** | Testcontainers (PostgreSQL, Redis, RabbitMQ) | Critical paths | Repository queries, service interactions |
| **API** | REST Assured | All endpoints | Request/response contracts, auth, validation |
| **Component** | Angular Testing Library | All components | User interactions, rendering |
| **E2E** | Playwright | Critical user flows | Registration → listing → booking → payment |
| **Performance** | k6 | Key endpoints | Load testing at 2x expected traffic |
| **Security** | OWASP ZAP + Snyk | All endpoints | Vulnerability scanning |
| **Accessibility** | axe-core + Lighthouse | All pages | WCAG 2.2 AA compliance |

## 16.2 Test Environment

- **Testcontainers**: Spin up real PostgreSQL, Redis, RabbitMQ, and Elasticsearch in Docker for integration tests. No mocks for infrastructure.
- **Stripe test mode**: All payment tests use Stripe's test API keys with test card numbers.
- **Factory pattern**: Test data factories (not fixtures) for generating realistic test data.

---

# 17. Release & Rollout Strategy

## 17.1 Release Process

| Phase | Duration | Activities |
|-------|----------|-----------|
| **Feature development** | Varies | Feature branches, PR reviews, CI checks |
| **Code freeze** | 2 days | Only bug fixes merged to release branch |
| **Staging validation** | 2 days | E2E tests, manual QA, performance tests |
| **Canary release** | 1 day | 5% traffic to new version; monitor error rates |
| **Progressive rollout** | 2 days | 25% → 50% → 100% traffic |
| **Post-release monitoring** | 7 days | Watch for regressions, collect user feedback |

## 17.2 Feature Flags

- New features deployed behind feature flags (LaunchDarkly or AWS AppConfig)
- Enables A/B testing, gradual rollout, and instant kill switch
- Vehicle rental vertical launched behind feature flag for initial beta

## 17.3 Rollback Strategy

- **Automatic**: If 5xx error rate exceeds 2% within 30 minutes of deployment, automatically revert to previous container image
- **Manual**: One-click rollback via ECS service update to previous task definition
- **Database**: Flyway migrations are forward-only; rollback scripts prepared but applied manually

## 17.4 Implementation Phasing Roadmap

| Phase | Timeline | Primary Outcomes |
|---|---|---|
| **Phase 0: Hardening Baseline** | Weeks 1-4 | Remove hardcoded secrets, enforce strict CORS, production env parity, unify WebSocket connection ownership, fix auth token storage model |
| **Phase 1: Platform Stabilization** | Weeks 5-10 | Introduce centralized state management, booking overlap validation, optimistic locking, audit trail, and critical DB indexing |
| **Phase 2: Scale Foundations** | Weeks 11-18 | Deploy Redis + RabbitMQ + OpenSearch managed services, enable resilient patterns (retry/circuit breaker/rate limit), complete observability dashboards |
| **Phase 3: Marketplace Expansion** | Weeks 19-28 | Launch vehicles vertical, Stripe-based payouts/collections, owner KYC workflows, and multi-region traffic rollout |
| **Phase 4: Operational Maturity** | Weeks 29-36 | SLO-driven operations, chaos drills, cost optimization, compliance hardening, and post-launch tuning |

---

# 18. Risk Analysis

| # | Risk | Probability | Impact | Mitigation |
|---|------|-------------|--------|-----------|
| R1 | Payment processing outage (Stripe) | Low | Critical | Queue payments for retry; show user-friendly error; monitor Stripe status page |
| R2 | Database corruption/loss | Very Low | Critical | Multi-AZ RDS, point-in-time recovery, daily S3 backups, tested restore procedures |
| R3 | DDoS attack | Medium | High | AWS Shield + WAF + CloudFront + rate limiting |
| R4 | Data breach | Low | Critical | Encryption at rest/transit, minimal PII storage, annual pentest, bug bounty program |
| R5 | Fraudulent listings | High | Medium | KYC verification, admin moderation queue, ML-based fraud detection (v3.0) |
| R6 | Scalability bottleneck | Medium | High | Load testing at 2x capacity, auto-scaling, horizontal architecture |
| R7 | Third-party API deprecation | Low | Medium | Adapter pattern isolates external services; swap providers without core changes |
| R8 | Regulatory compliance (GDPR/PCI) | Medium | High | Data minimization, consent management, Stripe handles PCI, legal review quarterly |
| R9 | Key personnel loss | Medium | Medium | Comprehensive documentation, pair programming, knowledge sharing sessions |
| R10 | Vehicle rental legal complexity | High | Medium | Region-specific legal review, insurance requirements, compliance checklist per market |

---

# 19. Appendices

## Appendix A: Complete API Endpoint List

*(Detailed OpenAPI spec to be auto-generated from code via SpringDoc)*

## Appendix B: Database Migration Strategy

- **Tool**: Flyway
- **Convention**: `V{version}__{description}.sql` (e.g., `V2.0.0__add_vehicles_table.sql`)
- **Rule**: Migrations are forward-only; never edit a released migration
- **Rollback**: Separate `U{version}__{description}.sql` scripts prepared but applied manually

## Appendix C: Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection string | `jdbc:postgresql://host:5432/homeflex` |
| `REDIS_URL` | Redis connection string | `redis://host:6379` |
| `RABBITMQ_URL` | RabbitMQ connection string | `amqp://host:5672` |
| `ELASTICSEARCH_URL` | Elasticsearch URL | `https://host:9200` |
| `STRIPE_SECRET_KEY` | Stripe API key | `sk_live_...` |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret | `whsec_...` |
| `TWILIO_ACCOUNT_SID` | Twilio account ID | `AC...` |
| `TWILIO_AUTH_TOKEN` | Twilio auth token | `...` |
| `AWS_ACCESS_KEY_ID` | AWS credentials | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | AWS credentials | `...` |
| `S3_BUCKET_NAME` | S3 bucket for media | `homeflex-media-prod` |
| `CLOUDFRONT_DOMAIN` | CDN domain | `cdn.homeflex.com` |
| `JWT_SECRET` | JWT signing key (256-bit) | `(from Secrets Manager)` |
| `FIREBASE_CREDENTIALS` | FCM service account JSON | `(from Secrets Manager)` |
| `SES_FROM_EMAIL` | Sender email address | `noreply@homeflex.com` |

## Appendix D: Glossary of Domain Terms

| Term | Definition |
|------|-----------|
| **Listing** | An asset (property or vehicle) posted for rental |
| **Vertical** | A category of rental assets (real estate, vehicles) |
| **Escrow** | Payment held by the platform until service is delivered |
| **Payout** | Transfer of funds from HomeFlex to asset owner |
| **Commission** | Platform fee (15% of booking amount) |

## Appendix E: Current-to-Target Migration Notes

- `rental-backend/build.gradle` is the source of truth for backend runtime/tooling (Gradle + Java 21 + Spring Boot 4).
- `rental-app-frontend/package.json` is the source of truth for frontend runtime/tooling (Angular 21 + Ionic 8 + Tailwind 4).
- Existing migration documentation may contain historical versions; architectural implementation MUST follow this SRS and repository manifests above.
| **Trust Score** | Composite reputation score based on reviews, verification, and behavior |
| **Superhost** | Owner with trust score > 4.5 and 10+ completed bookings |
| **Cancellation Policy** | Rules governing refund amounts based on cancellation timing |
| **Condition Report** | Photo + text documentation of vehicle state before/after rental |
| **Maintenance Request** | Renter-submitted issue during an active property booking |

---

*End of Document*

**Next Steps:**
1. Architecture review with team leads
2. Finalize technology procurement (AWS accounts, Stripe Connect, Twilio)
3. Set up CI/CD pipeline and infrastructure-as-code (Terraform)
4. Begin Sprint 1: User management + Auth + KYC
