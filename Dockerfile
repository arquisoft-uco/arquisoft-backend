# ==================== STAGE 1: BUILD ====================
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build

# Copy gradle files
COPY gradle ./gradle
COPY gradlew gradlew.bat gradle.properties ./

# Copy source code
COPY . .

# Build the application
RUN ./gradlew build -x test --no-daemon

# ==================== STAGE 2: RUNTIME ====================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 arquisoft && \
    adduser -D -u 1000 -G arquisoft arquisoft

# Copy JAR from builder
COPY --from=builder /build/build/libs/*.jar app.jar

# Change ownership
RUN chown -R arquisoft:arquisoft /app

# Switch to non-root user
USER arquisoft

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget -q --spider http://localhost:8080/api/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
