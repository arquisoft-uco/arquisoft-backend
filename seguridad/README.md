# Módulo `seguridad`

Contexto acotado responsable de la autenticación, autorización y gestión de usuarios del sistema. Delega la emisión de tokens a Keycloak y protege los recursos mediante JWT, blacklist Redis y rate limiting.

---

## Función

| Responsabilidad | Mecanismo |
|---|---|
| Autenticación de usuarios | Keycloak (Resource Owner Password Credentials) |
| Refresco de tokens | Keycloak refresh_token flow |
| Cierre de sesión | Blacklist JTI en Redis con TTL automático |
| Validación de tokens entre servicios | JwtDecoder + JWK Set de Keycloak |
| Creación de usuarios | Agrega dominio + evento `UsuarioCreadoEvent` vía RabbitMQ |
| Protección de endpoints | JWT Resource Server, rate limiting por IP, CORS, AuditFilter |

---

## Estructura de carpetas

```
seguridad/
├── domain/
│   └── src/main/java/.../seguridad/domain/
│       ├── auth/
│       │   ├── aggregate/
│       │   │   └── TokenAggregate.java          Aggregate para validar un JWT (solo campo valor)
│       │   ├── model/
│       │   │   ├── CredencialesSesion.java       Value object: resultado de autenticar/refrescar
│       │   │   └── IdentidadToken.java           Value object: identidad extraída de un JWT validado
│       │   ├── port/out/
│       │   │   ├── AuthenticationOutputPort.java  Contrato con el proveedor de identidad (Keycloak)
│       │   │   ├── TokenValidationOutputPort.java Contrato para validar y extraer info de un JWT
│       │   │   ├── TokenBlacklistOutputPort.java  Contrato para blacklist de tokens revocados
│       │   │   └── CurrentUserOutputPort.java     Contrato para leer el usuario autenticado del contexto
│       │   └── exception/
│       │       └── AuthenticationException.java   Excepción base de autenticación del dominio
│       └── usuario/
│           ├── aggregate/
│           │   └── UsuarioAggregate.java          Aggregate root del usuario; emite UsuarioCreadoEvent
│           ├── model/
│           │   └── UsuarioRole.java               Enum con los 8 roles del sistema
│           ├── event/
│           │   └── UsuarioCreadoEvent.java        Evento de dominio publicado al crear un usuario
│           └── port/out/
│               └── UsuarioOutputPort.java         Contrato de persistencia de usuarios
│
├── application/
│   └── src/main/java/.../seguridad/application/
│       ├── auth/
│       │   └── command/
│       │       ├── AuthenticateUserUseCase.java   Caso de uso: autenticar con email y contraseña
│       │       ├── port/in/
│       │       │   └── AuthenticateUserInputPort.java
│       │       ├── model/
│       │       │   └── AuthenticateUserCommand.java  Datos de entrada del login
│       │       ├── LogoutUseCase.java             Caso de uso: invalidar token en blacklist Redis
│       │       ├── port/in/
│       │       │   └── LogoutInputPort.java
│       │       ├── model/
│       │       │   └── TokenSesionCommand.java    Comando con jti y TTL; validaciones de dominio
│       │       ├── RefreshTokenUseCase.java       Caso de uso: obtener nuevo access token
│       │       ├── port/in/
│       │       │   └── RefreshTokenInputPort.java
│       │       ├── ValidateTokenUseCase.java      Caso de uso: validar JWT y extraer identidad
│       │       └── port/in/
│       │           └── ValidateTokenInputPort.java
│       └── usuario/
│           └── command/
│               ├── CrearUsuarioUseCase.java       Caso de uso: crear usuario y publicar evento
│               ├── port/in/
│               │   └── CrearUsuarioInputPort.java
│               └── model/
│                   └── CrearUsuarioCommand.java
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
        ├── usuario/
        │   └── command/
        │       ├── adapter/in/web/
        │       │   ├── UsuarioCommandInputAdapter.java   Endpoint POST /usuarios
        │       │   └── dto/
        │       │       ├── CrearUsuarioRequestDTO.java
        │       │       └── CrearUsuarioResponseDTO.java
        │       └── adapter/out/persistence/
        │           └── UsuarioCommandOutputAdapter.java
        ├── config/
        │   ├── security/      SecurityConfig, handlers de 401/403
        │   ├── keycloak/      Extracción y mapeo de roles JWT
        │   ├── ratelimit/     Bucket4j + Redis distribuido
        │   ├── datasource/    DataSource, JPA, Flyway del contexto
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
| `POST` | `/usuarios` | Sí — `usuarios:usuario:create` | Crea un usuario en el sistema y publica `UsuarioCreadoEvent` |

---

## Mecanismos de seguridad

### JWT / Keycloak
- Tokens validados contra el JWK Set de Keycloak (configurado en `SecurityConfig`).
- Roles extraídos exclusivamente de `realm_access.roles` (ADR-003).
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
