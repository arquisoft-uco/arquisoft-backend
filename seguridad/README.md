# Módulo `seguridad`

Contexto acotado responsable de la autenticación y de la decisión de acceso del sistema. Valida los JWT de uno o varios emisores configurables, consulta al proveedor de autorización (PEP) en cada solicitud protegida y protege los recursos con lista negra Redis y rate limiting. Ningún controlador, caso de uso ni dominio conoce al proveedor de identidad ni al de autorización. La gestión de usuarios vive en el contexto `usuarios`.

---

## Función

| Responsabilidad | Mecanismo |
|---|---|
| Autenticación de solicitudes | Resource server multi-emisor: JWT validado por JWKS, `iss` y `aud` de cada emisor configurado (`security.authentication.issuers`) |
| Decisión de acceso | `DecisionAccesoOutputPort` por solicitud, con adaptador por emisor: `pep` (PEP de Security Baseline), `role-table` (tabla de equivalencias del emisor legado) o `none` (solo desarrollo) |
| Identidad del actor | `IdentidadActual` (`shared:web`) resuelta desde el JWT con los claims configurados por emisor |
| Login directo (deprecado) | Proveedor `security.direct-login.provider` (`keycloak` o `none`) |
| Cierre de sesión | Lista negra de JTI en Redis con TTL automático, solo para emisores con `blacklist: true` |
| Protección de endpoints | Rate limiting por IP, CORS, auditoría vía `shared:tracing` |

---

## Estructura relevante

```
seguridad/
├── domain/.../autorizacion/AccesoDomain.java        Invariantes de una solicitud de acceso
├── application/.../autorizacion/command/            AutorizarAcceso (Command, Mapper, Interactor, UseCase),
│                                                    DecisionAccesoOutputPort, DecisionAcceso (sellada), DecisionAccesoResult
└── infrastructure/.../
    ├── autorizacion/command/
    │   ├── primaryadapter/web/                      ResolvedorPlantillaRuta (recurso = prefijo + plantilla MVC), AutorizarAccesoRequestMapper
    │   └── secondaryadapter/                        pep/, equivalencias/ (role-table), none/, DecisionAccesoPorEmisorOutputAdapter
    ├── auth/command/secondaryadapter/               keycloak/ (login directo), jwt/, redis/ (lista negra), security/ (IdentidadActualSeguridadAdapter)
    ├── config/autenticacion/                        AutenticacionProperties, DecodificadoresPorEmisor, AutenticacionPorEmisorConfig
    ├── config/security/                             SeguridadConfig, RutasPublicas, ValidadorConfiguracionSeguridad,
    │                                                NoAutenticadoEntryPoint, AccesoDenegadoHandler, AudienciaValidator
    └── filter/                                      AutorizacionAccesoFilter, JwtListaNegraFilter, IdentidadTrazaFilter, LimitadorSolicitudesFilter
```

---

## Endpoints expuestos (deprecados)

Existen solo mientras dure la transición y se retiran con `security.direct-login.provider=none`.

| Método | Ruta | Auth requerida | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | No | Autentica con email + contraseña (ROPC, desaconsejado para navegadores) |
| `POST` | `/auth/refresh` | No | Nuevo access token con un refresh token válido |
| `POST` | `/auth/logout` | Sí (Bearer, sin consulta al PEP) | Invalida el JWT actual en la lista negra de Redis |
| `POST` | `/auth/validate` | No | Valida un JWT y retorna la identidad extraída |

---

## Mecanismos de seguridad

### Autenticación y autorización
- Cada emisor se declara en `security.authentication.issuers[*]` (`id`, `issuer`, `jwks-uri`, `audiences`, `authorization`, `blacklist`, `claims`). Un token de un emisor ausente, deshabilitado, con audiencia ajena o expirado se rechaza con **401**.
- `AutorizacionAccesoFilter` pregunta, en cada solicitud protegida, si el usuario puede ejecutar el método sobre el recurso (plantilla de ruta con prefijo, p. ej. `/api/fichas-perfil/{fichaPerfilId}/items`). 2xx permite, 401 y 403 se propagan, y cualquier otro resultado o fallo de transporte responde **503** (falla cerrado). No se cachea ninguna decisión ni viaja ningún permiso en el token.
- Rutas públicas y exentas del PEP: `security.authorization.public-paths` y `authenticated-paths`.
- Un endpoint nuevo se registra como recurso en el proveedor de autorización y, mientras exista el emisor legado, con una fila en `autorizacion-equivalencias.yml` (`CompletitudEquivalenciasAutorizacionTest` lo exige; escribe `build/reports/autorizacion/recursos-pdp.csv`).
- En producción el arranque falla si el modo es `none`, si `security.enabled=false`, si falta cualquier dato obligatorio del PEP o si se permite HTTP inseguro.
- Sesión stateless — ningún estado HTTP del lado del servidor.

**Variables de entorno:** `AUTH_ISSUER_URI`, `AUTH_JWKS_URI`, `AUTH_AUDIENCES`, `AUTH_AUTHORIZATION`, `AUTH_CLAIM_ROLES`, `AUTH_LEGACY_ENABLED`, `PEP_URL`, `PEP_APPLICATION_NAME`, `PEP_ENVIRONMENT`, `PEP_REGISTRATION_TOKEN`, `PEP_ALLOW_INSECURE_HTTP`, `PEP_CONNECT_TIMEOUT`, `PEP_READ_TIMEOUT`, `PEP_MAX_RETRIES`, `PEP_RETRY_BACKOFF`, `AUTHORIZATION_PATH_MODE`, `AUTHORIZATION_PATH_PREFIX`, `DIRECT_LOGIN_PROVIDER`, `IDENTITY_PROVISIONING_PROVIDER`, `SECURITY_ENABLED`. Ver `.env.example`.

### Lista negra de tokens (logout)
- Al cerrar sesión, el JTI se guarda en Redis con TTL igual al tiempo de vida restante.
- `JwtListaNegraFilter` consulta Redis solo para emisores con `blacklist: true`; ante una caída de Redis acepta el token hasta que expire (fail-open acotado).

### Rate limiting
- Implementado con Bucket4j + Redis (distribuido, apto para múltiples instancias).
- Bucket global: 100 req/min por IP (dev) / 60 req/min (prod).
- Bucket login (`/auth/login`): 5 req/min por IP (dev) / 3 req/min (prod).
- Retorna `429 Too Many Requests` con cabecera `X-Rate-Limit-Retry-After-Seconds`.
