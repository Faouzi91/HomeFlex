# Build Stage — full JDK for compilation
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and config first (layer caching — dependencies rarely change)
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Download Gradle distribution via wrapper jar (avoids broken gradlew script on Windows)
RUN java -jar gradle/wrapper/gradle-wrapper.jar dependencies --no-daemon || true

# Copy source and build
COPY src ./src
RUN java -jar gradle/wrapper/gradle-wrapper.jar bootJar --no-daemon -x test

# Runtime Stage — JRE-slim for smaller image (~200MB vs ~600MB)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/build/libs/rental-backend.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
  CMD wget -q -O - http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
