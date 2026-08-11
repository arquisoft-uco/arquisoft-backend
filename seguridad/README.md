# Módulo `seguridad`

Contexto acotado responsable de la autenticación y autorización del sistema. Delega la emisión de tokens a Keycloak y protege los recursos mediante JWT, blacklist Redis y rate limiting. La creación y gestión de usuarios vive en el contexto `usuarios` — `seguridad` es puramente command-side sobre autenticación (no tiene agregado de usuario propio).

---

## Función

| Responsabilidad | Mecanismo |
|---|---|
| Autenticación de usuarios | Keycloak (Resource Owner Password Credentials) |
| Refresco de tokens | Keycloak refresh_token flow |
| Cierre de sesión | Blacklist JTI en Redis con TTL automático |
| Validación de tokens entre servicios | JwtDecoder + JWK Set de Keycloak |
| Autorización de endpoints | Permisos finos (`resource_access.{clientId}.roles`) vía JWT Resource Server |
| Protección de endpoints | Rate limiting por IP, CORS, AuditFilter |

---

## Estructura de carpetas

```
seguridad/
├── domain/
│   └── src/main/java/.../seguridad/domain/
│       └── auth/
│           ├── SesionDomain.java                 Aggregate root de sesión activa; invariantes del logout (jti no vacío, TTL > 0)
│           ├── TokenDomain.java                   Aggregate root para validar un JWT (solo campo valor)
│           ├── model/
│           │   ├── CredencialesSesion.java        Value object: resultado de autenticar/refrescar
│           │   └── IdentidadToken.java            Value object: identidad extraída de un JWT validado
│           ├── secondaryport/
│           │   ├── AutenticacionOutputPort.java   Contrato con el proveedor de identidad (Keycloak)
│           │   ├── ValidacionTokenOutputPort.java Contrato para validar y extraer info de un JWT
│           │   ├── TokenInvalidadoOutputPort.java Contrato para blacklist de tokens revocados
│           │   └── UsuarioActualOutputPort.java   Contrato para leer el usuario autenticado del contexto
│           └── exception/
│               └── AuthenticationException.java   Excepción base de autenticación del dominio
│
├── application/
│   └── src/main/java/.../seguridad/application/
│       └── auth/
│           └── command/
│               ├── primaryport/
│               │   ├── interactor/
│               │   │   ├── AutenticarUsuarioInteractor.java   Entry point: autenticar con email y contraseña
│               │   │   ├── CerrarSesionInteractor.java         Entry point: invalidar token en blacklist Redis
│               │   │   ├── RefrescarTokenInteractor.java       Entry point: obtener nuevo access token
│               │   │   ├── ValidarTokenInteractor.java         Entry point: validar JWT y extraer identidad
│               │   │   └── impl/                                *InteractorImpl.java — sin @Transactional (sin DataSource propio)
│               │   └── model/
│               │       ├── AutenticarUsuarioCommand.java       Datos de entrada del login
│               │       └── TokenSesionCommand.java             Record plano: jti y TTL calculado por el adaptador; invariantes en SesionDomain
│               ├── usecase/                                    Colaborador interno, no nested bajo primaryport
│               │   ├── AutenticarUsuarioUseCase.java
│               │   ├── CerrarSesionUseCase.java
│               │   ├── RefrescarTokenUseCase.java
│               │   ├── ValidarTokenUseCase.java
│               │   └── impl/                                    *UseCaseImpl.java
│               └── result/
│                   ├── AutenticacionResult.java
│                   ├── RefrescoTokenResult.java
│                   └── ValidacionTokenResult.java
│
└── infrastructure/
    └── src/main/java/.../seguridad/infrastructure/
        ├── auth/
        │   └── command/
        │       ├── primaryadapter/web/
        │       │   ├── AutenticacionCommandController.java   Endpoints REST /auth/*
        │       │   └── dto/
        │       │       ├── LoginRequestDTO.java               Tiene su propio toCommand()
        │       │       ├── LoginResponseDTO.java
        │       │       ├── LogoutResponseDTO.java
        │       │       ├── RefreshTokenRequestDTO.java
        │       │       └── ValidateTokenResponseDTO.java
        │       └── secondaryadapter/
        │           ├── keycloak/
        │           │   └── KeycloakAuthOutputAdapter.java   Integración Keycloak vía RestTemplate
        │           ├── redis/
        │           │   └── RedisTokenBlacklistOutputAdapter.java  Blacklist JTI en Redis
        │           ├── jwt/
        │           │   └── JwtTokenOutputAdapter.java       Decodifica JWT con JwtDecoder de Spring
        │           └── security/
        │               └── UsuarioActualOutputAdapter.java  Lee usuario del SecurityContextHolder
        ├── config/
        │   ├── security/      SeguridadConfig, SecurityAuthenticationEntryPoint/SecurityAccessDeniedHandler (401/403), AudienceValidator
        │   ├── keycloak/      KeycloakRolExtractor + KeycloakJwtConverterConfig — mapeo de resource_access.roles a authorities
        │   ├── ratelimit/     LimiteSolicitudesConfig/Properties + BucketResolver/RedisBucketResolver (Bucket4j + Redis distribuido)
        │   ├── cors/          CorsConfig
        │   ├── http/          RestTemplateConfig para Keycloak
        │   └── scheduling/    SchedulingConfig (@EnableScheduling)
        ├── exception/
        │   ├── CredencialesInvalidasException.java
        │   ├── TokenInvalidoException.java
        │   └── ProveedorIdentidadNoDisponibleException.java
        ├── filter/
        │   ├── AuditFilter.java              Log de cada request (METHOD, URI, STATUS, duración, IP)
        │   ├── JwtBlacklistFilter.java        Bloquea tokens revocados; fail-closed ante Redis caído
        │   └── LimitadorSolicitudesFilter.java 429 por IP; bucket global y bucket login (más estricto)
        └── web/
            └── SeguridadGlobalExceptionHandler.java  Mapeo de excepciones a HTTP 401/503
```

---

## Endpoints expuestos

| Método | Ruta | Auth requerida | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | No | **⚠️ ROPC — desaconsejado para navegadores** (OAuth 2.1 / RFC 9700). Autentica con email + contraseña. La SPA debe usar Authorization Code + PKCE contra Keycloak; este endpoint queda solo para clientes internos de confianza |
| `POST` | `/auth/refresh` | No | Obtiene nuevo access token usando un refresh token válido |
| `POST` | `/auth/logout` | Sí (Bearer) | Invalida el JWT actual en la blacklist de Redis (por su `jti`, con TTL = vida restante del token) |
| `POST` | `/auth/validate` | No | Valida un JWT y retorna la identidad extraída; útil para validaciones entre servicios |

La creación de usuarios (`POST /usuarios`) vive en el contexto `usuarios`, no en `seguridad`.

---

## Mecanismos de seguridad

### JWT / Keycloak
- Tokens validados en `SeguridadConfig` contra el **issuer** de Keycloak (`{KEYCLOAK_URL}/realms/{KEYCLOAK_REALM}`), que autodescubre el JWKS vía OIDC. Se valida **firma + expiración (nbf/exp) + `iss` + `aud`** (RFC 9700 / OAuth 2.1). Un token con issuer o audience incorrectos se rechaza con **401 `invalid_token`**.
- Autorización basada en permisos finos del claim `resource_access.{KEYCLOAK_CLIENT_ID}.roles` (formato `contexto:recurso:accion`, ej. `usuarios:usuario:create`, `fichas:ficha-perfil:view`), extraídos por `KeycloakRolExtractor` y mapeados 1:1 a `GrantedAuthority` sin prefijo `ROLE_` en `KeycloakJwtConverterConfig`. Los roles de `realm_access.roles` ya no se usan para autorización — cada contexto protege sus endpoints con `@PreAuthorize("hasAuthority('...')")` sobre estos permisos finos.
- Sesión stateless — ningún estado HTTP del lado del servidor.

#### Contrato de claims del access token

La SPA `react-app` autentica con **Authorization Code + PKCE** (public client) y envía el access token como `Authorization: Bearer <token>`. Para que ese token pase la validación y la autorización, Keycloak DEBE emitirlo con:

| Claim | Contenido esperado | Lo produce | Si falta → |
|---|---|---|---|
| `aud` | contiene `arquisoft-api` | **audience resolve mapper** (default) cuando el token porta client roles de `arquisoft-api` | **401** `invalid_token` (validación en `SeguridadConfig`) |
| `resource_access.arquisoft-api.roles` | permisos finos (`contexto:recurso:accion`) | **Full Scope Allowed** o **Client Roles mapper** en `react-app` + realm roles compuestos | **403** en `@PreAuthorize` (autentica pero sin authorities) |
| `realm_access.roles` | roles de negocio (`estudiante`, `asesor`, `coordinador`, …) | default de Keycloak | el backend NO lo usa; solo gating de UI del frontend |

> `arquisoft-api` es el client confidencial del backend (resource server) y `react-app` es el public client de la SPA. El valor esperado en `aud` es configurable con `KEYCLOAK_EXPECTED_AUDIENCE` y el client cuyos roles se leen con `KEYCLOAK_CLIENT_ID`.

#### Configuración requerida en el realm `arquisoft` (Keycloak)

1. **Client roles** en `arquisoft-api`: los permisos finos (`fichas:ficha-perfil:view`, `usuarios:usuario:create`, …).
2. **Realm roles compuestos** de negocio (`estudiante`, `asesor`, `coordinador`, …) que **agrupan** (composite) los client roles anteriores de `arquisoft-api`.
3. En el public client `react-app`:
   - **Full Scope Allowed = ON** (o un **Client Roles mapper** apuntando a `arquisoft-api`) para que los client roles concedidos aparezcan en `resource_access.arquisoft-api.roles`. Con esto, el `audience resolve mapper` por defecto añade `arquisoft-api` al claim `aud` automáticamente (no se requiere un Audience mapper explícito).

**Verificación end-to-end:** decodificar un access token real (jwt.io o `POST /auth/validate`) y confirmar que trae `aud` = `arquisoft-api`, `resource_access.arquisoft-api.roles` con los permisos, `realm_access.roles` con los roles de negocio, y `jti` (necesario para la blacklist de logout).

**Variables de entorno relevantes:** `KEYCLOAK_URL`, `KEYCLOAK_REALM`, `KEYCLOAK_CLIENT_ID` (`arquisoft-api`), `KEYCLOAK_EXPECTED_AUDIENCE` (default `arquisoft-api`).

### Blacklist de tokens (logout)
- Al cerrar sesión, el JTI del token se guarda en Redis con TTL igual al tiempo de vida restante.
- `JwtBlacklistFilter` verifica el JTI en cada request autenticado.
- Comportamiento **fail-closed**: si Redis no responde → 503 (niega el acceso).

### Rate limiting
- Implementado con Bucket4j + Redis (distribuido, apto para múltiples instancias).
- Bucket global: 100 req/min por IP (dev) / 60 req/min (prod).
- Bucket login (`/auth/login`): 5 req/min por IP.
- Retorna `429 Too Many Requests` con cabecera `X-Rate-Limit-Retry-After-Seconds`.

### CORS
- Orígenes permitidos configurables vía `CORS_ALLOWED_ORIGINS` (por defecto: `localhost:3000`, `4200`, `5173`) — lista exacta, sin comodines.
- La SPA autentica con **Bearer token (no cookies)**: `allow-credentials=false` (default) y `allowed-headers=Authorization,Content-Type` (sin `*`). Configurables vía `CORS_ALLOW_CREDENTIALS` y `CORS_ALLOWED_HEADERS`. En producción, incluir el origen real de la SPA en `CORS_ALLOWED_ORIGINS`.

### Auditoría
- `AuditFilter` registra cada request: METHOD, URI, STATUS, duración (ms), IP y userId (del JWT si presente).
- Excluye rutas de Swagger, actuator y docs.
