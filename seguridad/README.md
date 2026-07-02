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
│           ├── aggregate/
│           │   ├── TokenAggregate.java          Aggregate para validar un JWT (solo campo valor)
│           │   └── SesionAggregate.java         Aggregate de sesión activa; guarda invariantes del logout (jti no vacío, TTL > 0)
│           ├── model/
│           │   ├── CredencialesSesion.java       Value object: resultado de autenticar/refrescar
│           │   └── IdentidadToken.java           Value object: identidad extraída de un JWT validado
│           ├── port/out/
│           │   ├── AuthenticationOutputPort.java  Contrato con el proveedor de identidad (Keycloak)
│           │   ├── TokenValidationOutputPort.java Contrato para validar y extraer info de un JWT
│           │   ├── TokenBlacklistOutputPort.java  Contrato para blacklist de tokens revocados
│           │   └── CurrentUserOutputPort.java     Contrato para leer el usuario autenticado del contexto
│           └── exception/
│               └── AuthenticationException.java   Excepción base de autenticación del dominio
│
├── application/
│   └── src/main/java/.../seguridad/application/
│       └── auth/
│           └── command/
│               ├── AuthenticateUserUseCase.java   Caso de uso: autenticar con email y contraseña
│               ├── LogoutUseCase.java             Caso de uso: invalidar token en blacklist Redis
│               ├── RefreshTokenUseCase.java        Caso de uso: obtener nuevo access token
│               ├── ValidateTokenUseCase.java       Caso de uso: validar JWT y extraer identidad
│               ├── model/
│               │   ├── AuthenticateUserCommand.java  Datos de entrada del login
│               │   └── TokenSesionCommand.java       Record plano: jti y TTL calculado por el adaptador; invariantes en SesionAggregate
│               └── port/in/
│                   ├── AuthenticateUserInputPort.java
│                   ├── LogoutInputPort.java
│                   ├── RefreshTokenInputPort.java
│                   └── ValidateTokenInputPort.java
│
└── infrastructure/
    └── src/main/java/.../seguridad/infrastructure/
        ├── auth/
        │   └── command/
        │       ├── adapter/in/web/
        │       │   ├── AuthCommandInputAdapter.java   Endpoints REST /auth/*
        │       │   └── dto/
        │       │       ├── LoginRequestDTO.java
        │       │       ├── LoginResponseDTO.java
        │       │       ├── LogoutResponseDTO.java
        │       │       ├── RefreshTokenRequestDTO.java
        │       │       └── ValidateTokenResponseDTO.java
        │       └── adapter/out/
        │           ├── keycloak/
        │           │   └── KeycloakAuthOutputAdapter.java   Integración Keycloak vía RestTemplate
        │           ├── redis/
        │           │   └── RedisTokenBlacklistOutputAdapter.java  Blacklist JTI en Redis
        │           ├── jwt/
        │           │   └── JwtTokenOutputAdapter.java       Decodifica JWT con JwtDecoder de Spring
        │           └── security/
        │               └── CurrentUserOutputAdapter.java    Lee usuario del SecurityContextHolder
        ├── config/
        │   ├── security/      SecurityConfig, handlers de 401/403
        │   ├── keycloak/      KeycloakRoleExtractor + KeycloakJwtConverterConfig — mapeo de resource_access.roles a authorities
        │   ├── ratelimit/     Bucket4j + Redis distribuido
        │   ├── cors/          CorsConfig
        │   ├── http/          RestTemplate para Keycloak
        │   └── scheduling/    @EnableScheduling
        ├── exception/
        │   ├── CredencialesInvalidasException.java
        │   ├── TokenInvalidoException.java
        │   └── ProveedorIdentidadNoDisponibleException.java
        ├── filter/
        │   ├── AuditFilter.java         Log de cada request (METHOD, URI, STATUS, duración, IP)
        │   ├── JwtBlacklistFilter.java  Bloquea tokens revocados; fail-closed ante Redis caído
        │   └── RateLimitingFilter.java  429 por IP; bucket global y bucket login (más estricto)
        └── web/
            └── SeguridadGlobalExceptionHandler.java  Mapeo de excepciones a HTTP 401/503
```

---

## Endpoints expuestos

| Método | Ruta | Auth requerida | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | No | Autentica con email + contraseña; retorna access token y refresh token |
| `POST` | `/auth/refresh` | No | Obtiene nuevo access token usando un refresh token válido |
| `POST` | `/auth/logout` | Sí (Bearer) | Invalida el JWT actual en la blacklist de Redis |
| `POST` | `/auth/validate` | No | Valida un JWT y retorna la identidad extraída; útil para validaciones entre servicios |

La creación de usuarios (`POST /usuarios`) vive en el contexto `usuarios`, no en `seguridad`.

---

## Mecanismos de seguridad

### JWT / Keycloak
- Tokens validados contra el JWK Set de Keycloak (configurado en `SecurityConfig`).
- Autorización basada en permisos finos del claim `resource_access.{KEYCLOAK_CLIENT_ID}.roles` (formato `contexto:recurso:accion`, ej. `usuarios:usuario:create`, `fichas:ficha-perfil:view`), extraídos por `KeycloakRoleExtractor` y mapeados 1:1 a `GrantedAuthority` sin prefijo `ROLE_` en `KeycloakJwtConverterConfig`. Los roles de `realm_access.roles` ya no se usan para autorización — cada contexto protege sus endpoints con `@PreAuthorize("hasAuthority('...')")` sobre estos permisos finos.
- Sesión stateless — ningún estado HTTP del lado del servidor.

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
- Orígenes permitidos configurables vía `CORS_ALLOWED_ORIGINS` (por defecto: `localhost:3000`, `4200`, `5173`).

### Auditoría
- `AuditFilter` registra cada request: METHOD, URI, STATUS, duración (ms), IP y userId (del JWT si presente).
- Excluye rutas de Swagger, actuator y docs.
