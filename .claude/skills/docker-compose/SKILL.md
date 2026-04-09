---
name: docker-compose
description: >
  Generate Docker Compose configurations for Spring Boot + Angular + PostgreSQL + supporting
  services (MinIO, Redis, Prometheus, Grafana). Trigger this skill whenever the user asks to
  create or update a docker-compose file, add a service to Docker, dockerize the app,
  set up a dev environment, add monitoring, or configure containers for local development.
  Always apply this — never freehand docker-compose generation.
---

# Docker Compose Generation

## Stack Assumptions

- Spring Boot backend · Angular frontend (Nginx)
- PostgreSQL · MinIO · Redis (optional) · Prometheus + Grafana (observability)
- Dev-first: hot-reload friendly, named volumes, exposed ports
- Secrets via `.env` file (never hardcoded in compose)

---

## Base docker-compose.yml

```yaml
# docker-compose.yml
version: '3.9'

services:
  # ── PostgreSQL ─────────────────────────────────────────────────────────────
  db:
    image: postgres:18-alpine
    container_name: <project>-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - '5432:5432'
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docker/postgres/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ['CMD-SHELL', 'pg_isready -U ${DB_USER} -d ${DB_NAME}']
      interval: 10s
      timeout: 5s
      retries: 5

  # ── MinIO (S3-compatible object storage) ──────────────────────────────────
  minio:
    image: minio/minio:latest
    container_name: <project>-minio
    restart: unless-stopped
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY}
    ports:
      - '9000:9000' # API
      - '9001:9001' # Console UI
    volumes:
      - minio_data:/data
    healthcheck:
      test: ['CMD', 'curl', '-f', 'http://localhost:9000/minio/health/live']
      interval: 30s
      timeout: 10s
      retries: 3

  # ── Spring Boot Backend ────────────────────────────────────────────────────
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: <project>-backend
    restart: unless-stopped
    depends_on:
      db:
        condition: service_healthy
      minio:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_FLYWAY_ENABLED: 'true'
      MINIO_ENDPOINT: http://minio:9000
      MINIO_ACCESS_KEY: ${MINIO_ACCESS_KEY}
      MINIO_SECRET_KEY: ${MINIO_SECRET_KEY}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - '8080:8080'
    volumes:
      - ./backend/logs:/app/logs

  # ── Angular Frontend (Nginx) ───────────────────────────────────────────────
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: <project>-frontend
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - '80:80'
    volumes:
      - ./docker/nginx/nginx.conf:/etc/nginx/conf.d/default.conf:ro

volumes:
  postgres_data:
  minio_data:
```

---

## docker-compose.monitoring.yml (Observability overlay)

```yaml
# docker-compose.monitoring.yml
version: '3.9'

services:
  # ── Prometheus ─────────────────────────────────────────────────────────────
  prometheus:
    image: prom/prometheus:latest
    container_name: <project>-prometheus
    restart: unless-stopped
    ports:
      - '9090:9090'
    volumes:
      - ./docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.retention.time=15d'

  # ── Grafana ────────────────────────────────────────────────────────────────
  grafana:
    image: grafana/grafana:latest
    container_name: <project>-grafana
    restart: unless-stopped
    depends_on:
      - prometheus
    ports:
      - '3000:3000'
    environment:
      GF_SECURITY_ADMIN_USER: ${GRAFANA_USER:-admin}
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
      - ./docker/grafana/provisioning:/etc/grafana/provisioning:ro

volumes:
  prometheus_data:
  grafana_data:
```

Usage: `docker compose -f docker-compose.yml -f docker-compose.monitoring.yml up`

---

## .env Template

```dotenv
# .env (committed as .env.example, gitignored as .env)

# Database
DB_NAME=<project>_db
DB_USER=<project>_user
DB_PASSWORD=changeme_db

# MinIO
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=changeme_minio

# Security
JWT_SECRET=changeme_jwt_secret_min_32_chars

# Grafana
GRAFANA_PASSWORD=changeme_grafana
```

---

## Dockerfile Templates

### Backend (Spring Boot)

```dockerfile
# backend/Dockerfile

# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
```

### Frontend (Angular + Nginx)

```dockerfile
# frontend/Dockerfile

# Build stage
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --quiet

COPY . .
RUN npm run build -- --configuration production

# Runtime stage
FROM nginx:alpine
COPY --from=builder /app/dist/<app-name>/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### Nginx Config (API proxy)

```nginx
# docker/nginx/nginx.conf

server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    # Angular — HTML5 routing fallback
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy API calls to backend
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Prometheus Config

```yaml
# docker/prometheus/prometheus.yml

global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['backend:8080']
```

---

## Common Commands

```bash
# Start core stack
docker compose up -d

# Start with monitoring
docker compose -f docker-compose.yml -f docker-compose.monitoring.yml up -d

# Rebuild a service
docker compose up -d --build backend

# View logs
docker compose logs -f backend

# Reset DB (destroys data)
docker compose down -v && docker compose up -d

# Execute into container
docker compose exec db psql -U ${DB_USER} -d ${DB_NAME}
```

---

## Checklist Before Outputting

- [ ] All secrets in `.env` — never hardcoded
- [ ] `healthcheck` on DB and MinIO
- [ ] Backend `depends_on` with `condition: service_healthy`
- [ ] Named volumes declared at bottom
- [ ] Nginx config proxies `/api/` to backend
- [ ] Multi-stage Dockerfiles (build + runtime)
- [ ] Non-root user in backend Dockerfile
- [ ] `.env.example` committed, `.env` gitignored
- [ ] Prometheus config targets `backend:8080/actuator/prometheus`
