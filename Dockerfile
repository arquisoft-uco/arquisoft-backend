# ==================== STAGE 1: BUILD ====================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# Copiar todo el proyecto (.dockerignore excluye secretos, .git, build/, *.bat, etc.)
COPY . .

# chmod + build en un solo RUN para minimizar capas.
# find excluye el artefacto *-plain.jar que Spring Boot también genera,
# evitando que el COPY del stage 2 falle por múltiples coincidencias.
RUN chmod +x gradlew && \
    ./gradlew build -x test --no-daemon && \
    find build/libs -maxdepth 1 -name "*.jar" ! -name "*-plain.jar" \
         -exec cp {} app.jar \;

# ==================== STAGE 2: RUNTIME ====================
# JRE-only: reduce superficie de ataque al eliminar el compilador y herramientas de build
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear grupo y usuario de sistema sin contraseña ni shell interactivo (OWASP A05)
RUN addgroup -S arquisoft && \
        adduser -S -u 1000 -G arquisoft arquisoft && \
        mkdir -p /app/logs && chown arquisoft:arquisoft /app/logs
# --chown evita un RUN chown separado (menos capas, sin ejecutar como root tras el COPY)
COPY --chown=arquisoft:arquisoft --from=builder /build/app.jar app.jar

# Ejecutar como usuario no-root (OWASP A05: Security Misconfiguration)
USER arquisoft

EXPOSE 8080

# start-period=60s: tiempo real de arranque de Spring Boot con múltiples DBs y Flyway
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
