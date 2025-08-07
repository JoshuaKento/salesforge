# Multi-stage build for Spring Boot application
FROM openjdk:21-jre-slim as builder

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY web/build/libs/*.jar app.jar

# Extract layers for better caching
RUN java -Djarmode=layertools -jar app.jar extract

# Final stage
FROM openjdk:21-jre-slim

# Install necessary packages and create non-root user
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        ca-certificates && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r appuser && \
    useradd -r -g appuser appuser

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

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/auth/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

# Labels for better container management
LABEL org.opencontainers.image.title="SalesForge API" \
      org.opencontainers.image.description="Sales Force Automation REST API built with Spring Boot" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.source="https://github.com/JoshuaKento/salesforge" \
      org.opencontainers.image.vendor="SalesForge" \
      org.opencontainers.image.authors="joshua@salesforge.com"