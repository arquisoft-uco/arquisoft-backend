# Configurar el cliente de servicio del canal de sesión

Guía para que `seguridad` consulte a `usuarios` con un token de servicio y resuelva el id de aplicación
del usuario de una sesión (cookie del BFF de Security Baseline). La llamada
`POST /usuarios/por-correo` sale por `shared:web-client` con un Bearer obtenido por
`client_credentials`; nunca reenvía la cookie del usuario, porque la llamada pasa por el mismo filtro y
recursaría.

## 1. Por qué hace falta

Con `SESSION_AUTH_ENABLED=true`, `ValidadorConfiguracionSeguridad` aborta el arranque si falta
cualquiera de estas dos cosas:

| Falta | Motivo |
|---|---|
| `SESSION_USUARIOS_CLIENT_SECRET` | El cliente de servicio pide su token con `client_credentials` y necesita su secreto |
| `AUTH_LEGACY_ENABLED=true` | El token de servicio es del realm `arquisoft`, no del Security Baseline. Solo el emisor `legado` valida tokens de ese realm |

Mensaje de arranque típico:

```
Configuración de seguridad inválida: falta security.authentication.session.usuarios.client-secret;
el canal de sesion exige el emisor legado habilitado (AUTH_LEGACY_ENABLED=true): el token de servicio
con el que consulta a usuarios es del realm arquisoft
```

## 2. Qué se crea en Keycloak (realm `arquisoft`)

| # | Elemento | Valor |
|---|---|---|
| 1 | Client role en `arquisoft-api` | `usuarios:usuario-por-correo:view` |
| 2 | Realm role compuesto | `servicio-identidad`, con ese client role como único permiso |
| 3 | Cliente | `arquisoft-seguridad-usuarios`, confidencial, solo *Service accounts roles* |
| 4 | Mapper de audiencia del cliente | Audience `arquisoft-api` en access token e introspección |
| 5 | Rol de la cuenta de servicio | `servicio-identidad` |
| 6 | Secreto | Pestaña *Credentials* del cliente |

La definición de referencia está en `keycloak/realm-arquisoft.json`. El JSON no trae el secreto:
Keycloak lo genera al crear o importar el cliente.

> Se modifica un Keycloak compartido. Hazlo en el realm correcto y valida el resultado con el
> apartado 4 antes de arrancar el backend.

### Paso 1: client role en `arquisoft-api`

1. **Clients → `arquisoft-api` → pestaña Roles → Create role.**
2. **Role name:** `usuarios:usuario-por-correo:view`.
3. **Save.**

### Paso 2: realm role `servicio-identidad`

1. **Realm roles → Create role.**
2. **Role name:** `servicio-identidad`.
3. **Description:** `Rol de servicio: solo resolver el id de un usuario por su correo`. **Save.**
4. En ese rol, pestaña **Associated roles → Assign role**.
5. Cambia el filtro a **Filter by clients**, busca `usuarios:usuario-por-correo:view` y márcalo.
6. **Assign.** El rol queda compuesto y su único permiso es ese client role.

### Paso 3: crear el cliente

1. **Clients → Create client.**
2. **General settings:**
   - Client type: `OpenID Connect`.
   - Client ID: `arquisoft-seguridad-usuarios`.
   - Description: `Cuenta de servicio del contexto seguridad para resolver el id de usuario por correo`.
   - **Next.**
3. **Capability config:**
   - Client authentication: **ON**.
   - Authorization: OFF.
   - Authentication flow: desmarca *Standard flow* y *Direct access grants*; marca
     *Service accounts roles*.
   - **Next.**
4. **Login settings:** vacío. **Save.**

### Paso 4: mapper de audiencia

1. En el cliente: **Client scopes → `arquisoft-seguridad-usuarios-dedicated` → Configure a new
   mapper** (o *Add mapper → By configuration*).
2. Tipo **Audience**.
3. **Name:** `audience-arquisoft-api`.
4. **Included Client Audience:** `arquisoft-api`.
5. **Add to ID token:** OFF. **Add to access token:** ON. **Add to lightweight access token:** OFF.
   **Add to token introspection:** ON.
6. **Save.** Sin esta audiencia el backend rechaza el token, porque `AUTH_AUDIENCES` exige
   `arquisoft-api`.

### Paso 5: rol de la cuenta de servicio

1. En el cliente: pestaña **Service accounts roles → Assign role**.
2. Filtro **Filter by realm roles** → `servicio-identidad` → **Assign.**

### Paso 6: copiar el secreto

1. Pestaña **Credentials**, con *Client Authenticator* en `Client Id and Secret`.
2. Copia el **Client secret**.

## 3. Variables del backend

En el `.env` local (nunca versionado) y en el `ENV_FILE` del despliegue:

```
SESSION_AUTH_ENABLED=true
SESSION_PDP_URL=http://localhost:18080
SESSION_ALLOW_INSECURE_HTTP=true
CORS_ALLOW_CREDENTIALS=true

AUTH_LEGACY_ENABLED=true
SESSION_USUARIOS_CLIENT_SECRET=<secreto del paso 6>
```

Opcionales, con su valor por defecto (están comentadas en `.env.example`):

| Variable | Por defecto | Uso |
|---|---|---|
| `SESSION_USUARIOS_URL` | `http://localhost:8080` | URL base del servicio `usuarios` |
| `SESSION_USUARIOS_PROVEEDOR` | `legado` | Valor de `X-Proveedor-Identidad` en la llamada a `usuarios`: el emisor que autentica y autoriza el token de servicio |
| `SESSION_USUARIOS_TOKEN_URL` | derivada del emisor | Endpoint de token del realm `arquisoft` |
| `SESSION_USUARIOS_CLIENT_ID` | `arquisoft-seguridad-usuarios` | Cliente de servicio |
| `SESSION_USUARIOS_ALLOW_INSECURE_HTTP` | `false` | Permite `http` en local |
| `SESSION_USUARIOS_CONNECT_TIMEOUT` / `SESSION_USUARIOS_READ_TIMEOUT` | `1s` / `3s` | Tiempos del cliente |
| `SESSION_USUARIOS_CACHE_TTL` | `PT5M` | TTL de la caché Redis correo → id |
| `SESSION_USUARIOS_POR_CORREO_PATH` | `/api/usuarios/por-correo` | Ruta de la consulta |

`AUTH_LEGACY_ENABLED=true` también exige `KEYCLOAK_CLIENT_UUID`, que el emisor `legado` usa para leer
de la Admin API qué realm roles otorgan cada client role.

### Por qué la llamada envía `X-Proveedor-Identidad`

El token de servicio es del realm `arquisoft`, cuyo issuer es el del emisor `legado` (tabla de roles);
el emisor `principal` (PEP) tiene otro issuer (el realm `security-baseline`). La llamada a
`/usuarios/por-correo` envía `X-Proveedor-Identidad: legado` (`SESSION_USUARIOS_PROVEEDOR`) para fijar
ese emisor de forma explícita: el token se autentica y se autoriza como `legado`, y su decisión de
acceso va a la tabla de roles, no al PEP, que no conoce ese token. El `iss` y la audiencia del token
deben coincidir con ese emisor, o la respuesta es 401.

Si se configura `AUTH_ISSUER_URI` igual al issuer de `legado`, los emisores habilitados con el mismo
`issuer` deben declarar la misma `blacklist`, `audiences` y `authorization`; si no, el arranque falla
con `seguridad.infraestructura.autorizacion.error.emisores-politica-distinta`.

## 4. Verificar el token antes de arrancar

`read -rs` no deja el secreto en el historial ni en pantalla:

```bash
read -rs S; curl -s -X POST https://auth.arquisoft.top/realms/arquisoft/protocol/openid-connect/token \
  -d grant_type=client_credentials -d client_id=arquisoft-seguridad-usuarios -d client_secret="$S" \
| python3 -c 'import sys,json,base64; t=json.load(sys.stdin)["access_token"]; p=t.split(".")[1]; p+="="*(-len(p)%4); d=json.loads(base64.urlsafe_b64decode(p)); print("aud:",d.get("aud")); print("roles:",d.get("realm_access"))'
```

Resultado esperado:

- `aud` contiene `arquisoft-api`. Si falta, revisa el paso 4.
- `roles` incluye `servicio-identidad`. Si falta, revisa el paso 5.

## 5. Después de configurar

1. Reinicia el backend. El error de arranque del apartado 1 desaparece.
2. El emisor `legado` autoriza por el índice de roles de Redis, que refresca
   `ActualizarMapeoRolesScheduler` desde la Admin API. El nuevo mapeo (client role →
   `servicio-identidad`) puede tardar hasta el siguiente refresco. Si la primera llamada a
   `/usuarios/por-correo` responde 403, espera ese ciclo antes de sospechar de la configuración.
3. Carga el catálogo de mensajes en Redis (`catalogo/cargar.sh`) si cambió `seguridad.properties`.

## 6. Diagnóstico

| Síntoma | Causa probable |
|---|---|
| El backend no arranca con el mensaje del apartado 1 | Falta `AUTH_LEGACY_ENABLED=true` o `SESSION_USUARIOS_CLIENT_SECRET` |
| 503 al abrir una sesión con cookie | El PDP, Keycloak o `usuarios` no responden, o el token de servicio no se obtiene |
| 403 con cookie válida y PEP que permite | El correo de la sesión no existe, o está dado de baja, en `usuarios` |
| 403 en `/usuarios/por-correo` con el token de servicio | Falta el paso 2 o el 5, o el índice de roles aún no se refrescó |
| 401 en `/usuarios/por-correo` con el token de servicio | Falta la audiencia (paso 4) o `AUTH_LEGACY_ENABLED` no está activo |
| 503 `USUARIOS_NO_DISPONIBLE` con el log `estado-inesperado` 401 al pedir el id por correo | `SESSION_USUARIOS_PROVEEDOR` apunta a `principal`: el `iss` del token de servicio (realm `arquisoft`) no coincide con el de ese emisor y `usuarios` responde 401 incluso tras el reintento; debe ser `legado` |
| El cliente reintenta una vez y falla | El token de servicio caducó o fue revocado; el cliente invalida el token y reintenta una sola vez |

## Referencias

- `CLAUDE.md` → *Consultas síncronas entre contextos* (excepción documentada de HT-016-AJ3).
- `seguridad/README.md` y `.env.example` (bloque `SESSION_USUARIOS_*`).
- `keycloak/realm-arquisoft.json` (cliente, rol compuesto y client role de referencia).
