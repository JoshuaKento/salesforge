# Multi-stage build for Spring Boot application
FROM eclipse-temurin:21-jdk-alpine as builder

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradle gradle
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts ./
COPY core/build.gradle.kts core/
COPY infra/build.gradle.kts infra/
COPY security/build.gradle.kts security/
COPY web/build.gradle.kts web/

# Copy source code
COPY core/src core/src
COPY infra/src infra/src
COPY security/src security/src
COPY web/src web/src

# Make gradlew executable and build
RUN chmod +x gradlew
# Create Docker-specific gradle.properties
RUN echo "org.gradle.jvmargs=-Xmx1024m -XX:MaxMetaspaceSize=256m" > gradle.properties
RUN echo "org.gradle.java.installations.auto-detect=true" >> gradle.properties
RUN ./gradlew :web:build -x test --no-daemon

# Extract layers for better caching (now only bootJar will exist)
RUN java -Djarmode=layertools -jar web/build/libs/*.jar extract

# Final stage
FROM eclipse-temurin:21-jre-alpine

# Install necessary packages and create non-root user
RUN apk add --no-cache \
        curl \
        ca-certificates && \
    addgroup -S appuser && \
    adduser -S appuser -G appuser

# Set working directory
WORKDIR /app

# Copy layers from builder stage
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check (uses PORT environment variable if available)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application with PORT support
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} org.springframework.boot.loader.launch.JarLauncher"]

# Labels for better container management
LABEL org.opencontainers.image.title="SalesForge API" \
      org.opencontainers.image.description="Sales Force Automation REST API built with Spring Boot" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.source="https://github.com/JoshuaKento/salesforge" \
      org.opencontainers.image.vendor="SalesForge" \
      org.opencontainers.image.authors="joshua@salesforge.com"