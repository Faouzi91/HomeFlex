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
                    |  (Angular 21)|        | (Flutter 3.8)|
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
                                  |  |  SPRING BOOT 4  |
                                  |  | (backend:8080)  |
                                  |  +--+-+-+-+-+--+---+
                                  |     | | | | |  |
                       +----------+     | | | | |  +----------+
                       |                | | | | |             |
              +--------v-----+   +------v-+ | +-v------+  +--v---------+
              | PostgreSQL 16|   | Redis 7| | |RabbitMQ|  |Elastic 9   |
              | (db:5432)    |   | (:6379)| | |(:5672) |  |search      |
              |              |   |        | | |        |  |(:9200)     |
              | - users      |   | cache  | | | outbox |  | property   |
              | - properties |   | rate   | | | relay  |  | full-text  |
              | - bookings   |   | limits | | | events |  | + geo      |
              | - insurance  |   | Redlock| | |        |  | search     |
              +--------------+   +--------+ | +--------+  +------------+
                                            |
                                  +---------v---------+
                                  |  External APIs    |
                                  |                   |
                                  |  - Stripe (Identity/Connect)
                                  |  - Firebase (FCM) |
                                  |  - Twilio (SMS/WhatsApp/OTP)
                                  |  - AWS S3 (Storage)
                                  +-------------------+
```

## Technology Stack

| Layer              | Technology                                                           |
| ------------------ | -------------------------------------------------------------------- |
| **Backend**        | Java 21, Spring Boot 4, Spring Security, Hibernate Envers (Auditing) |
| **Persistence**    | PostgreSQL 18 (Flyway migrations), Redis 8 (Caching/Redlock)         |
| **Search**         | Elasticsearch 9.1 (Property Discovery)                               |
| **Messaging**      | RabbitMQ 4 (Transactional Outbox Pattern)                            |
| **Frontend**       | Angular 21, NgRx Signal Store, Tailwind CSS 4, Leaflet Maps          |
| **Mobile**         | Flutter (Android/iOS — separate repo, not in Docker Compose)         |
| **Infrastructure** | Docker Compose, AWS (ECS Fargate, RDS, S3), Terraform                |
| **Monitoring**     | ELK Stack 9.1 (Logstash, Kibana), Prometheus 3.5, Grafana 11.6       |

## Core Architectural Patterns

### 1. Transactional Outbox Relay

Ensures atomic consistency between database updates and external systems (Elasticsearch, RabbitMQ).

- **Step 1:** Service saves entity + `OutboxEvent` in a single DB transaction.
- **Step 2:** `OutboxRelayService` polls events using `FOR UPDATE SKIP LOCKED`.
- **Step 3:** Events published to RabbitMQ; consumers update Elasticsearch or send notifications.

### 2. Distributed Locking (Redlock)

Prevents double-booking and race conditions in concurrent environments using **Redisson**.

- Locks are acquired based on `propertyId` + `dates` during the booking creation/modification flow.

### 3. App-Level PII Encryption

Sensitive user data (names, phones) is encrypted using AES-256 before being persisted to the database via JPA `AttributeConverter`.

### 4. Distributed Caching

Layered Redis caching for frequently accessed property details (30m TTL) and search results (5m TTL) via Spring Cache.

## Global Multi-Region Strategy (Conceptual)

```
      [ Route53 Latency-Based Routing ]
               /               \
      [ EU-WEST-1 ]         [ US-EAST-1 ]
      (Primary Region)      (Failover Region)
             |                     |
      [ ECS Fargate ] <---> [ ECS Fargate ]
             |                     |
      [ RDS Global ] <---Replication---> [ RDS Global ]
        (Write)               (Read Replica)
```

## Monitoring & Observability

### Logging (ELK Stack)

1.  **Backend:** Ships JSON logs via TCP to Logstash.
2.  **Logstash:** Filters and routes logs to Elasticsearch.
3.  **Kibana:** Visualizes logs, traces, and system errors in real-time.

### Metrics (Prometheus & Grafana)

- **SLO Alerts:** Defined for < 500ms latency and < 1% error rate.
- **JVM Health:** Real-time monitoring of heap, GC, and thread states.
- **Business Metrics:** Counters for successful bookings, payments, and user growth.

## Module Innovations

| Module                | Implementation                                                                         |
| --------------------- | -------------------------------------------------------------------------------------- |
| **Blockchain Leases** | Asynchronous recording of signed lease hashes on-chain (Simulated Ethereum/Polygon).   |
| **AI Pricing**        | Data-driven pricing suggestions based on location demand and seasonal trends.          |
| **Insurance**         | Integrated marketplace for Tenant/Landlord protection plans during booking.            |
| **Disputes**          | Multi-party evidence system (photo/PDF) with administrative mediation.                 |
| **GDPR**              | Endpoints for automated data portability (Export) and Erasure (Right to be Forgotten). |
