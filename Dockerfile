# syntax=docker/dockerfile:1
# Origen del jar:
#   compilado    (default) → se compila aquí desde el código. Lo usa docker-compose en local.
#   precompilado           → se copia ./app.jar del contexto. Lo usa docker-build.yml, que
#                            compila en el runner con la caché de Gradle: dentro de BuildKit, sin
#                            caché, la resolución de dependencias fallaba de forma intermitente
#                            ("Could not find spring-core…" contra Maven Central y el Plugin Portal).
# BuildKit solo construye la etapa elegida.
ARG JAR_ORIGEN=compilado

# ==================== STAGE 1a: JAR COMPILADO AQUÍ ====================
# --platform=$BUILDPLATFORM: el jar es bytecode agnóstico de arquitectura, así que
# esta etapa siempre corre nativa en el runner (evita compilar bajo emulación QEMU
# cuando el target es arm64, que sería órdenes de magnitud más lento).
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk-alpine AS jar-compilado
WORKDIR /build

# Copiar todo el proyecto (.dockerignore excluye secretos, .git, build/, *.bat, etc.)
COPY . .

# chmod + build en un solo RUN para minimizar capas.
# find excluye el artefacto *-plain.jar que Spring Boot también genera,
# evitando que el COPY del stage 2 falle por múltiples coincidencias.
# Los secretos solo existen durante este RUN: el token de GitHub Packages no queda en ninguna capa.
RUN --mount=type=secret,id=github_packages_user,env=GITHUB_PACKAGES_USER \
    --mount=type=secret,id=github_packages_token,env=GITHUB_PACKAGES_TOKEN \
    sed -i 's/\r$//' gradlew && chmod +x gradlew && \
    ./gradlew build -x test --no-daemon && \
    find build/libs -maxdepth 1 -name "*.jar" ! -name "*-plain.jar" \
         -exec cp {} app.jar \;

# ==================== STAGE 1b: JAR PRECOMPILADO (CI) ====================
FROM scratch AS jar-precompilado
COPY app.jar /build/app.jar

FROM jar-${JAR_ORIGEN} AS jar

# ==================== STAGE 2: RUNTIME ====================
# JRE-only: reduce superficie de ataque al eliminar el compilador y herramientas de build
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Crear grupo y usuario de sistema sin contraseña ni shell interactivo (OWASP A05)
# /app/logs      → perfil dev/base (application.yml: logs/arquisoft.log)
# /var/log/arquisoft → perfil prod  (application-prod.yml: /var/log/arquisoft/arquisoft.log)
RUN addgroup -S arquisoft && \
        adduser -S -u 1000 -G arquisoft arquisoft && \
        mkdir -p /app/logs /var/log/arquisoft && \
        chown arquisoft:arquisoft /app/logs /var/log/arquisoft
# --chown evita un RUN chown separado (menos capas, sin ejecutar como root tras el COPY)
COPY --chown=arquisoft:arquisoft --from=jar /build/app.jar app.jar

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
