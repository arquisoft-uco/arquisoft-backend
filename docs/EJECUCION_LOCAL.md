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
| `SESSION_AUTH_ENABLED`, `SESSION_PDP_URL` | Canal de la cookie de sesión del BFF (`SECURITY_BASELINE_SESSION`) como proveedor `sesion-pdp`, junto a Bearer. Apagado por defecto; en dev se enciende con `SESSION_AUTH_ENABLED=true`, `SESSION_PDP_URL=http://localhost:18080` y `CORS_ALLOW_CREDENTIALS=true`. Opcionales: `SESSION_PROVIDER_ID`, `SESSION_COOKIE_NAME`, `SESSION_CONNECT_TIMEOUT` (1s), `SESSION_READ_TIMEOUT` (3s), `SESSION_CACHE_TTL` (PT60S; `PT0S` apaga la caché de identidad), `SESSION_CACHE_MAX_SIZE` (10000), `SESSION_ALLOW_INSECURE_HTTP` (`true` por defecto solo en dev) |
| `AUTH_LEGACY_ENABLED` | `true` por defecto en dev; `false` apaga el emisor Keycloak del realm `arquisoft` |
| `DIRECT_LOGIN_PROVIDER=none` | Retira los cuatro endpoints `/auth/*` (deprecados) |
| `IDENTITY_PROVISIONING_PROVIDER=none` | Los endpoints de `usuarios` que aprovisionan identidad responden 503 |
| `SECURITY_ENABLED=false` | Desinstala el filtro de autorizacion (solo local; el arranque en `prod` lo rechaza) |

## Canal de la cookie de sesión del BFF (Security Baseline 0.1.6)

Con `SESSION_AUTH_ENABLED=true` el backend acepta la cookie de sesión además de Bearer. Por cada petición con cookie consulta primero al PEP (reenviando solo esa cookie) y, solo si permite, obtiene la identidad con `GET {SESSION_PDP_URL}/api/v1/session` (caché en memoria de la identidad, nunca de decisiones). El arranque falla si falta la URL, si `CORS_ALLOW_CREDENTIALS` no es `true`, si los orígenes no son explícitos o si `CORS_ALLOWED_HEADERS` no incluye `X-XSRF-TOKEN` y `X-Proveedor-Identidad`.

- **Selector:** el encabezado opcional `X-Proveedor-Identidad` elige el proveedor (`sesion-pdp`, `bearer` o el id de un emisor habilitado como `legado` o `principal`, que autentica y autoriza el Bearer como ese emisor; los emisores habilitados con el mismo `issuer` deben declarar la misma `blacklist`, `audiences` y `authorization`, si no el arranque falla con `seguridad.infraestructura.autorizacion.error.emisores-politica-distinta`). Sin él, gana Bearer y, sin Bearer, la cookie. Un valor desconocido, deshabilitado o `none` responde 401.
- **CSRF:** una escritura (cualquier método salvo GET, HEAD, OPTIONS y TRACE) por cookie exige `X-XSRF-TOKEN` no vacío y un `Origin` (o el origen de `Referer`) dentro de `CORS_ALLOWED_ORIGINS`; si no, 403.
- `/auth/logout` con solo cookie responde 401.
- `accessTokenLifespan` = 300 s: la sesión BFF se corta cada ~5 minutos y PEP/PDP responden 401; este backend no la renueva (lo resuelve el borde o el front).
- La cookie es `SameSite=Lax`: PDP, frontend y API deben compartir dominio registrable.
- La CSRF (`XSRF-TOKEN` / `X-XSRF-TOKEN`) solo la aplica el BFF a sus rutas; ni el PEP ni su starter la exigen a la API, por eso la cierra este backend.
- El PEP en modo proxy no reenvía Bearer ni cookies: no es alternativa.
