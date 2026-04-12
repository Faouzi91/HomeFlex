# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy gradle wrapper first
COPY gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

# Copy config and source
COPY build.gradle settings.gradle ./
COPY src ./src

# Build the JAR (skipping tests for speed and reliability)
RUN ./gradlew bootJar --no-daemon -x test

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/build/libs/rental-backend.jar app.jar

EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
  CMD wget -q -O - http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
