# Módulo `seguridad`

Contexto acotado responsable de la autenticación y de la decisión de acceso del sistema. Valida los JWT de uno o varios emisores configurables, consulta al proveedor de autorización (PEP) en cada solicitud protegida y protege los recursos con lista negra Redis y rate limiting. Ningún controlador, caso de uso ni dominio conoce al proveedor de identidad ni al de autorización. La gestión de usuarios vive en el contexto `usuarios`.

---

## Función

| Responsabilidad | Mecanismo |
|---|---|
| Autenticación de solicitudes | Resource server multi-emisor: JWT validado por JWKS, `iss` y `aud` de cada emisor configurado (`security.authentication.issuers`) |
| Decisión de acceso | `DecisionAccesoOutputPort` por solicitud, con adaptador por emisor: `pep` (PEP de Security Baseline), `role-table` (tabla de equivalencias del emisor legado) o `none` (solo desarrollo). El `role-table` toma los realm roles del token y comprueba en Redis, con un script Lua, si alguno otorga el client role que la tabla exige al endpoint; el índice client role → realm roles lo refresca un `@Scheduled` desde la Admin API del proveedor (feature `mapeoroles`, `KEYCLOAK_CLIENT_UUID` obligatoria). El emisor legado ya no emite `resource_access` |
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
    ├── autenticacion/command/primaryadapter/web/    bearer/ProveedorBearer, sesion/ProveedorSesionPdp
    ├── config/autenticacion/                        AutenticacionProperties, DecodificadoresPorEmisor, AutenticacionPorEmisorConfig,
    │                                                IdentidadAutenticada, ProveedorAutenticacion = CanalAutenticacion + PoliticaCanal
    │                                                + PresentadorCredencial, ProveedoresAutenticacion
    ├── config/security/                             SeguridadConfig, RutasPublicas, ValidadorConfiguracionSeguridad,
    │                                                NoAutenticadoEntryPoint, AccesoDenegadoHandler, AudienciaValidator
    └── filter/                                      AutorizacionAccesoFilter (filtro único), LimitadorSolicitudesFilter
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
- `AutorizacionAccesoFilter` es el único filtro de acceso: elige el `ProveedorAutenticacion` (Bearer o cookie de sesión, `X-Proveedor-Identidad` fuerza uno), autentica, aplica la lista negra, registra el sujeto en la traza y pregunta, en cada solicitud protegida, si el usuario puede ejecutar el método sobre el recurso (plantilla de ruta con prefijo, p. ej. `/api/fichas-perfil/{fichaPerfilId}/items`). 2xx permite, 401 y 403 se propagan, y cualquier otro resultado o fallo de transporte responde **503** (falla cerrado). No se cachea ninguna decisión ni viaja ningún permiso en el token.
- Rutas públicas y exentas del PEP: `security.authorization.public-paths` y `authenticated-paths`.
- Un endpoint nuevo se registra como recurso en el proveedor de autorización y, mientras exista el emisor legado, con una fila en `autorizacion-equivalencias.yml` (`CompletitudEquivalenciasAutorizacionTest` lo exige; escribe `build/reports/autorizacion/recursos-pdp.csv`).
- En producción el arranque falla si el modo es `none`, si `security.enabled=false`, si falta cualquier dato obligatorio del PEP o si se permite HTTP inseguro.
- **Canal de sesión (cookie del BFF): id de usuario por correo.** El `subject` del PDP no es el `usuarios.id`; tras la decisión del PEP, `ResolverIdentidadSesion` resuelve el id consultando a `usuarios` (`POST /api/usuarios/por-correo`, body `{correo}`) con una **credencial de servicio** (`client_credentials` del cliente `arquisoft-seguridad-usuarios`, realm role `servicio-identidad`), nunca con la cookie del llamante (recursaría). Un correo sin usuario vigente responde 403; `usuarios` caído, 5xx o credencial rechazada responde 503. Los aciertos se cachean en Redis (`SESSION_USUARIOS_CACHE_TTL`, `PT5M`) con la huella SHA-256 del correo normalizado; con Redis caído se consulta directo. **Exige `AUTH_LEGACY_ENABLED=true`** donde `SESSION_AUTH_ENABLED=true`: el token de servicio es del realm `arquisoft` y solo lo valida el emisor `legado` (el arranque lo comprueba). La consulta envía `X-Proveedor-Identidad: {SESSION_USUARIOS_PROVEEDOR}` (default `legado`) para que su token de servicio se valide con el emisor `legado` y se autorice con su tabla de roles; `principal` tiene otro issuer, así que sin el encabezado el `iss` del token ya lo asigna a `legado`. Deuda: el Bearer del emisor `principal` tiene el mismo desajuste de id y no se resuelve aquí.
- Sesión stateless — ningún estado HTTP del lado del servidor.

**Selector de emisor:** `X-Proveedor-Identidad` también acepta el id de un emisor habilitado (`legado`, `principal`): el Bearer se autentica y se autoriza como ese emisor; su `iss` debe coincidir con el del emisor y su audiencia con la de ese emisor, si no 401. Cualquier cliente puede elegir cualquier emisor habilitado (el token debe pasar firma, `iss` y audiencia; la decisión de acceso sigue siendo del PDP o la tabla). Regla: los emisores habilitados con el mismo `issuer` deben declarar la misma `blacklist`, `audiences` y `authorization`; si no, el arranque falla con `seguridad.infraestructura.autorizacion.error.emisores-politica-distinta`. Así el encabezado nunca cambia un control, solo la lectura de los `claims`.

**Variables de entorno:** `AUTH_ISSUER_URI`, `AUTH_JWKS_URI`, `AUTH_AUDIENCES`, `AUTH_AUTHORIZATION`, `AUTH_CLAIM_ROLES`, `AUTH_LEGACY_ENABLED`, `PEP_URL`, `PEP_APPLICATION_NAME`, `PEP_ENVIRONMENT`, `PEP_REGISTRATION_TOKEN`, `PEP_ALLOW_INSECURE_HTTP`, `PEP_CONNECT_TIMEOUT`, `PEP_READ_TIMEOUT`, `PEP_MAX_RETRIES`, `PEP_RETRY_BACKOFF`, `AUTHORIZATION_PATH_MODE`, `AUTHORIZATION_PATH_PREFIX`, `DIRECT_LOGIN_PROVIDER`, `IDENTITY_PROVISIONING_PROVIDER`, `SECURITY_ENABLED`, `SESSION_USUARIOS_CLIENT_SECRET`, `SESSION_USUARIOS_URL`, `SESSION_USUARIOS_TOKEN_URL`, `SESSION_USUARIOS_CLIENT_ID`, `SESSION_USUARIOS_CACHE_TTL`, `SESSION_USUARIOS_POR_CORREO_PATH`, `SESSION_USUARIOS_PROVEEDOR`. Ver `.env.example`.

### Lista negra de tokens (logout)
- Al cerrar sesión, el JTI se guarda en Redis con TTL igual al tiempo de vida restante.
- `AplicadorListaNegra` (paso del filtro único, tras autenticar y antes de la traza y la decisión) consulta Redis solo para emisores con `blacklist: true`; ante una caída de Redis acepta el token hasta que expire (fail-open acotado).

### Rate limiting
- Implementado con Bucket4j + Redis (distribuido, apto para múltiples instancias).
- Bucket global: 100 req/min por IP (dev) / 60 req/min (prod).
- Bucket login (`/auth/login`): 5 req/min por IP (dev) / 3 req/min (prod).
- Retorna `429 Too Many Requests` con cabecera `X-Rate-Limit-Retry-After-Seconds`.
