# Runtime-only image — JAR is pre-built locally via: ./gradlew bootJar -x test
FROM eclipse-temurin:21-jre
WORKDIR /app

# Non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

COPY build/libs/rental-backend.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
  CMD wget -q -O - http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
