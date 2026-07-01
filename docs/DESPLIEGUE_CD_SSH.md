# Despliegue continuo (CD) por SSH

Guía del flujo de **CI/CD** que despliega el backend en un servidor remoto vía
SSH, usando la imagen Docker publicada en GitHub Container Registry (GHCR) y
exponiéndolo por **Traefik** en `api.<dominio>`.

- **CI** → [`.github/workflows/ci.yml`](../.github/workflows/ci.yml): lint, tests y build en cada push/PR.
- **Build de imagen** → [`.github/workflows/docker-build.yml`](../.github/workflows/docker-build.yml): construye y publica `ghcr.io/<repo>` en cada push.
- **CD (despliegue)** → [`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml): se conecta por SSH al servidor y levanta el contenedor.

---

## 1. Cómo funciona

```
push a develop
   │
   ├─► ci.yml            (lint + tests + build)
   │
   ├─► docker-build.yml  (publica ghcr.io/<repo>:develop  y  :sha-<short>)
   │
   └─► deploy.yml ──SSH──► servidor:
                              1. escribe $HOME/arquisoft/.env  (desde secreto ENV_FILE)
                              2. espera y hace `docker pull` de :sha-<short>
                              3. reemplaza el contenedor, unido a la red arquisoft-network
                              4. le pone labels de Traefik → expone api.<BASE_DOMAIN> (443/TLS)
                              5. verifica /api/actuator/health (en 127.0.0.1:8080)
```

**Solo se despliega el backend.** PostgreSQL, RabbitMQ, Redis, Keycloak y MinIO
viven **aparte** (otro stack) en la red Docker **`arquisoft-network`**. El backend
se une a esa red y los alcanza por **nombre de contenedor** (ver §3).

**Se despliega por el tag inmutable `sha-<short>`** (no por `develop`): el pull
se reintenta hasta que ese tag exista, lo que serializa "build terminó → deploy"
y evita desplegar una imagen vieja por una condición de carrera.

**Exposición:** el puerto `8080` se publica **solo en loopback** (`127.0.0.1:8080`)
para el health check local; el tráfico externo entra por **Traefik** (HTTPS/443,
`letsencrypt`) hacia el host `api.<BASE_DOMAIN>`.

### Disparadores

| Evento | Qué despliega |
|--------|---------------|
| `push` a `develop` | la imagen `sha-<short>` de ese commit |
| `workflow_dispatch` (manual, pestaña **Actions**) | el tag indicado en el input `image_tag`, o el `sha-<short>` del commit si se deja vacío |

---

## 2. Secretos requeridos en GitHub

`Settings → Secrets and variables → Actions → New repository secret`

| Secreto | Obligatorio | Ejemplo / Descripción |
|---------|:----------:|------------------------|
| `SSH_HOST` | ✅ | Dominio (recomendado) o IP del servidor. Si la IP es **dinámica**, usa un dominio (p. ej. DuckDNS) que apunte al servidor. |
| `SSH_USER` | ✅ | Usuario **del SO** en el servidor (el de `ssh USUARIO@host`), en el grupo `docker`. |
| `SSH_PORT` | ✅ | Puerto SSH (`22`). Si hay NAT, el puerto **redirigido** en el router. |
| `SSH_PRIVATE_KEY` | ✅ | Clave **privada** OpenSSH completa (multilínea, con cabecera/pie `-----BEGIN/END ...-----`, sin passphrase). |
| `ENV_FILE` | ✅ | Contenido **completo** del `.env` de runtime (pegar tal cual, ver §3). |
| `BASE_DOMAIN` | ✅ | Dominio **base** (p. ej. `midominio.com`). El backend se expone en `api.<BASE_DOMAIN>`; el subdominio `api` es fijo en el workflow. |
| `DOCKER_NETWORK` | ⛔ opcional | Red Docker de la infra. Default: `arquisoft-network`. |
| `GHCR_USERNAME` | ⛔ opcional | Solo si el paquete GHCR es **privado**: tu usuario de GitHub. |
| `GHCR_TOKEN` | ⛔ opcional | Solo si es **privado**: un PAT con scope `read:packages`. |

> La imagen se publica **pública** por defecto (ver `docker-build.yml`). Mientras
> el paquete sea público, **no** necesitas `GHCR_USERNAME` ni `GHCR_TOKEN`.

---

## 3. Preparar el secreto `ENV_FILE`

Es el `.env` que la app usa **dentro del contenedor**. Parte de `.env.example` y
ajusta los valores para el servidor.

> ⚠️ **Regla de oro (dentro de un contenedor `localhost` es el propio
> contenedor, no el host).** Como los servicios de infra corren como contenedores
> en `arquisoft-network`, el backend los alcanza por **nombre de contenedor** — no
> por `localhost` ni `host.docker.internal`.

### Hosts a usar

| Servicio | Valor en el `.env` | Nota |
|---|---|---|
| PostgreSQL (7 BDs) | `arquisoft-postgres:5432` | nombre de contenedor + puerto interno |
| RabbitMQ | `arquisoft-rabbitmq` (`RABBITMQ_HOST`) | puerto interno `5672` |
| Redis | `arquisoft-redis` (`REDIS_HOST`) | puerto interno `6379` |
| Keycloak | `https://auth.<BASE_DOMAIN>` (`KEYCLOAK_URL`) | **URL pública** (el issuer del JWT debe coincidir) |
| MinIO | `https://s3.<BASE_DOMAIN>` (`MINIO_ENDPOINT`) | **URL pública** (necesario para presigned URLs) |

Además:
- `SPRING_PROFILES_ACTIVE=prod` para producción.
- `CORS_ALLOWED_ORIGINS` con los dominios reales de frontend/backend.
- Credenciales reales de cada servicio (deben coincidir con las del stack de infra).

Ejemplo (fragmento):

```dotenv
SPRING_PROFILES_ACTIVE=prod
DB_USUARIOS_URL=jdbc:postgresql://arquisoft-postgres:5432/usuarios
DB_USUARIOS_USERNAME=arquisoft_user
DB_USUARIOS_PASSWORD=la-clave-real
# ...las otras 6 BDs igual, cambiando solo el nombre de la base...
RABBITMQ_HOST=arquisoft-rabbitmq
REDIS_HOST=arquisoft-redis
KEYCLOAK_URL=https://auth.midominio.com
MINIO_ENDPOINT=https://s3.midominio.com
```

> Nombres de variable: la app usa `DB_USUARIOS_*`, `DB_FICHAS_*`, `DB_PROYECTOS_*`,
> `DB_ARTEFACTOS_*`, `DB_REPO_ARTEFACTOS_*`, `DB_ENTREGABLES_*`, `DB_EVALUACIONES_*`
> (una BD por contexto). Respeta esos nombres exactos.

El workflow escribe el archivo en `$HOME/arquisoft/.env` (permisos `600`) y lo
inyecta con `docker run --env-file`. **Para rotar configuración:** edita el secreto
`ENV_FILE` y vuelve a desplegar (push a `develop` o disparo manual).

---

## 4. Preparar el servidor (una sola vez)

1. **Instalar Docker** (Ubuntu/Debian):
   ```bash
   curl -fsSL https://get.docker.com | sh
   ```
2. **Usuario de despliegue** con acceso a Docker (o usa tu usuario habitual):
   ```bash
   sudo usermod -aG docker "$USER"   # requiere reabrir sesión
   ```
3. **Clave SSH** (si el servidor es la máquina destino, se puede generar ahí mismo):
   ```bash
   ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/deploy_key -N ""
   cat ~/.ssh/deploy_key.pub >> ~/.ssh/authorized_keys
   chmod 600 ~/.ssh/authorized_keys
   ```
   Copia la **privada** (`cat ~/.ssh/deploy_key`) al secreto `SSH_PRIVATE_KEY` y luego
   bórrala del servidor (`shred -u ~/.ssh/deploy_key`).
4. **Red Docker de la infra** debe existir y contener postgres/redis/rabbitmq/keycloak/minio:
   ```bash
   docker network ls | grep arquisoft-network
   ```
   (El backend se une a ella; no la crea.)
5. **Traefik** debe estar corriendo en `arquisoft-network`, con entrypoint `websecure`
   y un certResolver `letsencrypt` (el backend solo aporta sus labels).
6. **Conectividad entrante** (si el server está detrás de NAT doméstico):
   - Redirige el puerto SSH en el router hacia la IP interna del servidor.
   - Si la IP pública es **dinámica**, usa un dominio (DuckDNS u otro) como `SSH_HOST`
     para no depender de la IP.
   - Deben estar abiertos/redirigidos también `80` y `443` (para Traefik y ACME).
7. **Verificar conexión** (desde fuera de la LAN, p. ej. datos móviles):
   ```bash
   ssh -i deploy_key -p <PUERTO> <USUARIO>@<DOMINIO> "docker ps"
   ```

> El workflow crea `$HOME/arquisoft` automáticamente (`mkdir -p`), sin `sudo`.

---

## 5. Cómo apuntar el despliegue a OTRO servidor / dominio

El workflow no tiene nada "hardcodeado" del entorno: todo viene de secretos.

1. Prepara el **nuevo servidor** siguiendo §4 (Docker + usuario + clave SSH + red + Traefik).
2. En `Settings → Secrets and variables → Actions`, **actualiza**:
   - `SSH_HOST`, `SSH_USER`, `SSH_PORT`, `SSH_PRIVATE_KEY` → del nuevo servidor.
   - `BASE_DOMAIN` → el nuevo dominio base (el backend quedará en `api.<nuevo>`).
   - `ENV_FILE` → hosts/credenciales de la infra del nuevo servidor.
   - `DOCKER_NETWORK` → solo si la red no se llama `arquisoft-network`.
3. Vuelve a desplegar: push a `develop`, o **Actions → CD Deploy (SSH) → Run workflow**.

No hay que tocar el YAML para cambiar de servidor o dominio — solo los secretos.

> Para **varios entornos** (staging y prod), usa *GitHub Environments*
> (`Settings → Environments`): crea `staging`/`production`, define los mismos
> secretos en cada uno, añade `environment: <nombre>` al job y condiciona por rama.

---

## 6. Probar primero en una rama feature

`workflow_dispatch` y `workflow_run` **solo se ejecutan desde la rama por defecto**.
Por eso, para validar el despliegue **desde una rama feature antes de fusionar**,
se activa temporalmente el `push` de esa rama en `deploy.yml`:

1. En [`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml), agrega
   temporalmente tu rama al trigger:
   ```yaml
   on:
     push:
       branches: [develop, feature/mi-rama]   # ← TEMPORAL para probar
   ```
2. Configura los secretos (§2, §3) y prepara el servidor (§4).
3. Haz push. En **Actions** deberían correr `CI Backend`, `Docker Build & Push` y `CD Deploy (SSH)`.
4. Verifica en el servidor:
   ```bash
   docker ps | grep arquisoft-backend
   curl -fsS http://127.0.0.1:8080/api/actuator/health
   ```
5. **Antes de fusionar**, revierte el trigger a `branches: [develop]`.

> ⚠️ Probar desde la feature despliega al **mismo servidor** de los secretos. Si no
> quieres tocar producción, apunta `SSH_HOST`/`ENV_FILE`/`BASE_DOMAIN` a un entorno
> de pruebas mientras validas.

---

## 7. Operación y troubleshooting

```bash
# Estado y logs del backend
docker ps
docker logs --tail 100 -f arquisoft-backend

# Imagen en ejecución y red
docker inspect --format '{{.Config.Image}}' arquisoft-backend
docker inspect --format '{{range $n,$_ := .NetworkSettings.Networks}}{{$n}} {{end}}' arquisoft-backend

# Health local y a través de Traefik
curl -fsS http://127.0.0.1:8080/api/actuator/health
curl -fsS https://api.<BASE_DOMAIN>/api/actuator/health
```

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| `dial tcp <ip>:*** i/o timeout` en el paso SSH | Puerto SSH no alcanzable desde internet (NAT sin port-forward, firewall) | Redirige el puerto en el router; abre firewall; §4.6 |
| `dial tcp: lookup *** ... i/o timeout` | `SSH_HOST` es un hostname que no resuelve, o IP dinámica cambió | Usa un dominio válido y limpio (sin esquema ni puerto) en `SSH_HOST` |
| `mkdir: cannot create directory ... Permission denied` | Ruta fuera del `$HOME` del usuario | Ya se usa `$HOME/arquisoft`; revisa que `SSH_USER` sea el correcto |
| `Connection to localhost:5432 refused` | Un `DB_*_URL`/host apunta a `localhost` | Usa el **nombre de contenedor** (`arquisoft-postgres`, etc.), §3 |
| `password authentication failed` | Credenciales del `ENV_FILE` no coinciden con la infra | Usa las credenciales reales del stack de infra |
| Falta `DB_USUARIOS_*` y va a `localhost` | Nombre de variable incorrecto en `ENV_FILE` | Respeta los nombres exactos por contexto (§3) |
| `La imagen ... no se publicó a tiempo` | `docker-build.yml` falló o tardó | Revisa ese workflow; el deploy reintenta 30×10s (~5 min) |
| `denied / unauthorized` al `docker pull` | Paquete GHCR privado sin credenciales | Define `GHCR_USERNAME` + `GHCR_TOKEN`, o haz público el paquete |
| Health check falla | Alguna dependencia inalcanzable desde el contenedor | Revisa hosts/credenciales en `ENV_FILE`; mira `docker logs` |
| HTTPS público sirve `TRAEFIK DEFAULT CERT` | Let's Encrypt no emitió el cert (con DuckDNS, timeout de CAA intermitente) | Reintenta (`docker restart` de Traefik) o usa DNS-01 challenge |

---

## Referencias

- [docker-build.yml](../.github/workflows/docker-build.yml) — publicación de la imagen
- [.env.example](../.env.example) — plantilla de variables de runtime
- [DESPLIEGUE_ALLOY_COOLIFY.md](DESPLIEGUE_ALLOY_COOLIFY.md) — despliegue alternativo con Coolify
- [GUIA_DOCKERFILE.md](GUIA_DOCKERFILE.md) — detalles de la imagen
