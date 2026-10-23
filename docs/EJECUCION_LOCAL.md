# Ejecución local con Gradle

## Prerrequisitos

- Java 21 instalado
- Archivo `.env` en la raíz del proyecto (copiar desde `.env.example`)

## Pasos

**1. Dar permisos al wrapper (solo la primera vez)**

```bash
chmod +x gradlew
```

**2. Levantar infraestructura**

```bash
docker-compose up postgres rabbitmq redis keycloak
```

El script `init-db.sql` crea automáticamente todas las bases de datos al iniciar el contenedor de Postgres. Cada contexto tiene su propia BD — no existe una BD centralizada para el Outbox Pattern.

**3. Cargar el catálogo de mensajes en Redis**

```bash
REDIS_HOST=localhost REDIS_PORT=6379 REDIS_PASSWORD=default123 sh catalogo/cargar.sh
```

No es opcional. La aplicación valida al arrancar que **todas** las claves que declara existan en Redis, y si falta una sola no levanta (ADR-013 v1.1). El script es idempotente: re-ejecutarlo no tiene efecto secundario.

Si haces un `FLUSHDB` manual sobre esa instancia, **recarga el catálogo antes de reiniciar el backend** o no arrancará. Por la misma razón el script nunca hace `FLUSHDB` él mismo: esa base comparte espacio con los buckets de rate limit y los tokens invalidados de `seguridad`.

Con `docker-compose up` (todo el stack) este paso no hace falta: el servicio `catalogo-loader` lo ejecuta y el backend espera a que termine con éxito.

**4. Correr el proyecto**

```bash
./gradlew bootRun
```

Spring Boot carga automáticamente el `.env` al arrancar. El perfil activo se define dentro del archivo con `spring.profiles.active`.

La aplicación queda disponible en `http://localhost:8080/api`.

## Variables de entorno requeridas

Copia `.env.example` a `.env`. Las variables clave son:

| Variable | Descripción |
|---|---|
| `DB_SEGURIDAD_URL` | JDBC URL de la BD del contexto seguridad |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | Conexión al broker |
| `KEYCLOAK_URL` / `KEYCLOAK_REALM` | Solo el emisor legado, el login directo y el aprovisionamiento de `usuarios` |
| `KEYCLOAK_CLIENT_ID`, `KEYCLOAK_CLIENT_SECRET`, `KEYCLOAK_CLIENT_UUID` | Cuenta de servicio con la que el emisor `role-table` lee de la Admin API qué realm roles otorgan cada client role. Sin `KEYCLOAK_CLIENT_UUID` el arranque aborta si el emisor legado está habilitado en `role-table` (también debe ir en el `ENV_FILE` del CD) |
| `ROLE_EXPANSION_*` | Opcionales: `REFRESH_INTERVAL` (PT5M), `TTL` (PT15M), `CHECK_INTERVAL` (PT30S), `LOCK_LEASE` (PT60S), `CONNECT_TIMEOUT` (2s), `READ_TIMEOUT` (3s) del índice en Redis |
| `AUTH_ISSUER_URI`, `AUTH_AUDIENCES` | Emisor principal (Security Baseline). En dev: `http://keycloak.localhost:19090/realms/security-baseline` |
| `PEP_URL`, `PEP_APPLICATION_NAME`, `PEP_ENVIRONMENT`, `PEP_REGISTRATION_TOKEN` | PEP de Security Baseline (laboratorio: PEP `http://localhost:18081`, PDP `18080`, Keycloak `19090`). `PEP_ALLOW_INSECURE_HTTP=true` por defecto en dev |
| `AUTH_LEGACY_ENABLED` | `true` por defecto en dev; `false` apaga el emisor Keycloak del realm `arquisoft` |
| `DIRECT_LOGIN_PROVIDER=none` | Retira los cuatro endpoints `/auth/*` (deprecados) |
| `IDENTITY_PROVISIONING_PROVIDER=none` | Los endpoints de `usuarios` que aprovisionan identidad responden 503 |
| `SECURITY_ENABLED=false` | Desinstala el filtro de autorizacion (solo local; el arranque en `prod` lo rechaza) |
