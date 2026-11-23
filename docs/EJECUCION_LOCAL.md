# Ejecución local con Gradle

## Prerrequisitos

- Java 25 instalado
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
| `KEYCLOAK_CLIENT_ID`, `KEYCLOAK_CLIENT_SECRET`, `KEYCLOAK_CLIENT_UUID` | Cuenta de servicio con la que el emisor `role-table` lee de la Admin API qué realm roles otorgan cada client role. Sin `KEYCLOAK_CLIENT_UUID` el arranque aborta si el emisor legado está habilitado en `role-table` (también debe estar en Doppler, `arquisoft-backend/test`, para el CD) |
| `ROLE_EXPANSION_*` | Opcionales: `REFRESH_INTERVAL` (PT5M), `TTL` (PT15M), `CHECK_INTERVAL` (PT30S), `LOCK_LEASE` (PT60S), `CONNECT_TIMEOUT` (2s), `READ_TIMEOUT` (3s) del índice en Redis |
| `AUTH_ISSUER_URI`, `AUTH_AUDIENCES` | Emisor principal (Security Baseline). En dev: `http://keycloak.localhost:19090/realms/security-baseline` |
| `PEP_URL`, `PEP_APPLICATION_NAME`, `PEP_ENVIRONMENT`, `PEP_REGISTRATION_TOKEN` | PEP de Security Baseline (laboratorio: PEP `http://localhost:18081`, PDP `18080`, Keycloak `19090`). `PEP_ALLOW_INSECURE_HTTP=true` por defecto en dev |
| `SESSION_AUTH_ENABLED`, `SESSION_PDP_URL` | Canal de la cookie de sesión del BFF (`SECURITY_BASELINE_SESSION`) como proveedor `sesion-pdp`, junto a Bearer. Apagado por defecto; en dev se enciende con `SESSION_AUTH_ENABLED=true`, `SESSION_PDP_URL=http://localhost:18080` y `CORS_ALLOW_CREDENTIALS=true`. Opcionales: `SESSION_PROVIDER_ID`, `SESSION_COOKIE_NAME`, `SESSION_CONNECT_TIMEOUT` (1s), `SESSION_READ_TIMEOUT` (3s), `SESSION_CACHE_TTL` (PT60S; `PT0S` apaga la caché de identidad), `SESSION_CACHE_MAX_SIZE` (10000), `SESSION_ALLOW_INSECURE_HTTP` (`true` por defecto solo en dev) |
| `AUTH_LEGACY_ENABLED` | `true` por defecto en dev; `false` apaga el emisor Keycloak del realm `arquisoft` |
| `DIRECT_LOGIN_PROVIDER=none` | Retira los cuatro endpoints `/auth/*` (deprecados) |
| `IDENTITY_PROVISIONING_PROVIDER=none` | Los endpoints de `usuarios` que aprovisionan identidad responden 503 |
| `SECURITY_ENABLED=false` | Desinstala el filtro de autorizacion (solo local; el arranque en `prod` lo rechaza) |

## Catálogo de Parámetros (opcional)

El backend puede leer del Catálogo de Parámetros de la organización los valores de la aplicación y el ambiente asociados a su identidad de servicio. Está **apagado por defecto** (`CATALOGO_PARAMETROS_ENABLED=false`) y mientras lo esté no hace falta nada de esto. Encendido, la carga inicial es síncrona: **el backend no arranca** si el Catálogo o Keycloak están caídos, la configuración es inválida o se rechaza la credencial. Para desarrollar sin Catálogo, déjalo en `false`.

Consumo M2M (Catálogo ≥ 0.1.17; el mecanismo anterior de tokens propios, `/tokens/{id}/parametros`, ya no existe): el backend pide un token `client_credentials` a Keycloak con su identidad de servicio, lo reutiliza hasta 30 s antes de que venza, y consulta `GET /catalogo-parametros/api/v1/parametros/consulta/m2m` con `Authorization: Bearer`. No envía aplicación ni ambiente: el Catálogo los resuelve desde la asociación del `client_id`. Un 401 del Catálogo descarta el token y el siguiente refresco pide otro.

Variables (en `.env`): `CATALOGO_PARAMETROS_BASE_URL`, `CATALOGO_PARAMETROS_TOKEN_URI` (endpoint de tokens del realm, p. ej. `http://localhost:19090/realms/security-baseline/protocol/openid-connect/token`), `CATALOGO_PARAMETROS_CLIENT_ID`, `CATALOGO_PARAMETROS_CLIENT_SECRET`, `CATALOGO_PARAMETROS_REFRESH_INTERVAL` (`PT5M`), y opcionales `CATALOGO_PARAMETROS_CONNECT_TIMEOUT` (`1s`), `CATALOGO_PARAMETROS_READ_TIMEOUT` (`3s`), `CATALOGO_PARAMETROS_ALLOW_INSECURE_HTTP` (`true` por defecto solo en dev; cubre la URL del Catálogo y la de tokens). No confundir con `CATALOGO_FUENTE`, que es del catálogo de mensajes.

La identidad necesita tres cosas, o el Catálogo responde 403: (1) existir en Security como identidad de servicio; (2) un rol técnico con el recurso `GET /catalogo-parametros/api/v1/parametros/consulta/m2m`; (3) estar registrada en el Catálogo y asociada a la aplicación y el ambiente cuyos parámetros debe leer.

Parámetro consumido hoy: `RATE_LIMIT_SOLICITUDES_POR_MINUTO` (entero >= 1, en texto, p. ej. `50`) fija la cuota global de solicitudes por minuto y por IP del limitador. Si falta, está inactivo, el módulo está apagado o el valor no es un entero >= 1, se usa `RATE_LIMIT_REQUESTS_PER_MINUTE` (propiedad `security.rate-limit.requests-per-minute`) y, con un valor inválido, se loguea un `warn` por valor distinto. Un cambio en el Catálogo se aplica tras el siguiente refresco (`CATALOGO_PARAMETROS_REFRESH_INTERVAL`). La cuota de login no se toca. Prueba: con `RATE_LIMIT_ENABLED=true` y el parámetro en `5`, una ráfaga de 8 peticiones a una ruta no técnica bajo `/api` (no `/api/actuator/health`, exenta) devuelve `X-Rate-Limit-Remaining` 4..0 y `429` desde la sexta; al cambiarlo a `8` y esperar un refresco, la ráfaga usa el bucket nuevo (clave `arquisoft:ratelimit:global:{cuota}:{ip}`). Con un valor `abc` o sin parámetro vuelve a la propiedad.

Para probarlo contra un Catálogo local:

- **Choque de puerto 8081.** El backend del Catálogo publica `8081:8080` y este repo mapea su Keycloak en `8081:8080`. No toques nuestro Keycloak (es el `iss` de los tokens): en el lado del Catálogo crea un `docker-compose.override.yml` con `ports: !override ["8083:8080"]` en el servicio `app` (Compose ≥ 2.24; sin `!override` el puerto se concatena y el 8081 sigue ocupado) y apunta `CATALOGO_PARAMETROS_BASE_URL=http://localhost:8083/catalogo-parametros/api/v1`. No uses `8082`: desde la 0.1.17 lo ocupa el administrador del Pulsar propio del Catálogo (`CATALOGO_PULSAR_ADMIN_PORT`). Si nuestro Keycloak no está levantado, el Catálogo puede quedarse en `8081`.
- Las identidades, roles y parámetros se crean en las consolas: Security en `http://localhost:5173` y el Catálogo en `http://localhost:4201`, ambas sobre Security Baseline (PDP/PEP/Keycloak en 18080/18081/19090) con perfil administrativo.
- En el Catálogo, el estado de la relación parámetro–ambiente debe llamarse exactamente `activo` (sin distinguir mayúsculas). Con otro nombre, por ejemplo `activo_dev`, la relación se considera no vigente y el parámetro no llega.
- Prueba manual del contrato: pide el token con `curl -d grant_type=client_credentials -d client_id=... -d client_secret=... <TOKEN_URI>` y consulta `/parametros/consulta/m2m` con ese Bearer; debe traer el parámetro con `metadatos[].metadato.valor`.

## Canal de la cookie de sesión del BFF (Security Baseline 0.1.6)

Con `SESSION_AUTH_ENABLED=true` el backend acepta la cookie de sesión además de Bearer. Por cada petición con cookie consulta primero al PEP (reenviando solo esa cookie) y, solo si permite, obtiene la identidad con `GET {SESSION_PDP_URL}/api/v1/session` (caché en memoria de la identidad, nunca de decisiones). El arranque falla si falta la URL, si `CORS_ALLOW_CREDENTIALS` no es `true`, si los orígenes no son explícitos o si `CORS_ALLOWED_HEADERS` no incluye `X-XSRF-TOKEN` y `X-Proveedor-Identidad`.

- **Selector:** el encabezado opcional `X-Proveedor-Identidad` elige el proveedor (`sesion-pdp`, `bearer` o el id de un emisor habilitado como `legado` o `principal`, que autentica y autoriza el Bearer como ese emisor; los emisores habilitados con el mismo `issuer` deben declarar la misma `blacklist`, `audiences` y `authorization`, si no el arranque falla con `seguridad.infraestructura.autorizacion.error.emisores-politica-distinta`). Sin él, gana Bearer y, sin Bearer, la cookie. Un valor desconocido, deshabilitado o `none` responde 401.
- **CSRF:** una escritura (cualquier método salvo GET, HEAD, OPTIONS y TRACE) por cookie exige `X-XSRF-TOKEN` no vacío y un `Origin` (o el origen de `Referer`) dentro de `CORS_ALLOWED_ORIGINS`; si no, 403.
- `/auth/logout` con solo cookie responde 401.
- `accessTokenLifespan` = 300 s: la sesión BFF se corta cada ~5 minutos y PEP/PDP responden 401; este backend no la renueva (lo resuelve el borde o el front).
- La cookie es `SameSite=Lax`: PDP, frontend y API deben compartir dominio registrable.
- La CSRF (`XSRF-TOKEN` / `X-XSRF-TOKEN`) solo la aplica el BFF a sus rutas; ni el PEP ni su starter la exigen a la API, por eso la cierra este backend.
- El PEP en modo proxy no reenvía Bearer ni cookies: no es alternativa.
