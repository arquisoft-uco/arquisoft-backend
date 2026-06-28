# Despliegue continuo (CD) por SSH

Guía del flujo de **CI/CD** que despliega el backend en un servidor remoto vía
SSH, usando la imagen Docker publicada en GitHub Container Registry (GHCR).

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
                              1. escribe /opt/arquisoft/.env  (desde secreto ENV_FILE)
                              2. espera y hace `docker pull` de :sha-<short>
                              3. reemplaza el contenedor  (docker run --env-file)
                              4. verifica /api/actuator/health
```

**Solo se despliega el backend.** PostgreSQL, RabbitMQ, Redis, Keycloak y MinIO
deben existir ya en el servidor (gestionados aparte); el contenedor del backend
se conecta a ellos según las URLs/hosts definidos en el `.env`.

**Se despliega por el tag inmutable `sha-<short>`** (no por `develop`): el pull
se reintenta hasta que ese tag exista, lo que serializa "build terminó → deploy"
y evita desplegar una imagen vieja por una condición de carrera.

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
| `SSH_HOST` | ✅ | IP o dominio del servidor (`203.0.113.10` o `api.midominio.com`) |
| `SSH_USER` | ✅ | Usuario SSH (`deploy`) |
| `SSH_PORT` | ✅ | Puerto SSH (`22`) |
| `SSH_PRIVATE_KEY` | ✅ | Clave **privada** OpenSSH completa (multilínea, con cabecera/pie `-----BEGIN/END ...-----`) |
| `ENV_FILE` | ✅ | Contenido **completo** del `.env` de runtime (pegar tal cual, ver §3) |
| `GHCR_USERNAME` | ⛔ opcional | Solo si el paquete GHCR es **privado**: tu usuario de GitHub |
| `GHCR_TOKEN` | ⛔ opcional | Solo si es **privado**: un PAT con scope `read:packages` |

> La imagen se publica **pública** por defecto (ver `docker-build.yml`). Mientras
> el paquete sea público, **no** necesitas `GHCR_USERNAME` ni `GHCR_TOKEN`.

---

## 3. Preparar el secreto `ENV_FILE`

Es el `.env` que la app usa **dentro del contenedor**. Parte de `.env.example` y
ajusta los valores para el servidor:

- Usa los **hosts reales** de tu infraestructura. Si Postgres/RabbitMQ/Redis/etc.
  corren en el **mismo** servidor, puedes usar `host.docker.internal` (el deploy
  agrega `--add-host=host.docker.internal:host-gateway`) o `localhost`/IP del host.
- Pon `SPRING_PROFILES_ACTIVE=prod` para producción.
- Define `CORS_ALLOWED_ORIGINS` con los dominios reales de frontend/backend.
- Rellena credenciales reales (DB, Keycloak `KEYCLOAK_CLIENT_SECRET`, MinIO, Redis).

Luego, en el secreto `ENV_FILE`, **pega el archivo entero** (líneas `CLAVE=valor`),
por ejemplo:

```dotenv
SPRING_PROFILES_ACTIVE=prod
DB_USUARIOS_URL=jdbc:postgresql://host.docker.internal:5432/usuarios
DB_USUARIOS_USERNAME=arquisoft_user
DB_USUARIOS_PASSWORD=la-clave-real
# ... resto de variables ...
```

El workflow lo escribe en `/opt/arquisoft/.env` (permisos `600`) y lo inyecta con
`docker run --env-file`. **Para rotar configuración:** edita el secreto `ENV_FILE`
y vuelve a desplegar (push a `develop` o disparo manual).

---

## 4. Preparar el servidor (una sola vez)

1. **Instalar Docker** (Ubuntu/Debian):
   ```bash
   curl -fsSL https://get.docker.com | sh
   ```
2. **Crear el usuario de despliegue** y darle acceso a Docker:
   ```bash
   sudo adduser --disabled-password --gecos "" deploy
   sudo usermod -aG docker deploy
   ```
3. **Generar el par de claves SSH** para el deploy (en tu máquina local):
   ```bash
   ssh-keygen -t ed25519 -C "github-actions-deploy" -f ./deploy_key -N ""
   ```
   - Copia la **pública** al servidor:
     ```bash
     ssh-copy-id -i ./deploy_key.pub deploy@SERVIDOR
     # o manualmente: añade deploy_key.pub a /home/deploy/.ssh/authorized_keys
     ```
   - Copia la **privada** completa (`cat ./deploy_key`) al secreto `SSH_PRIVATE_KEY`.
4. **Verificar conexión** desde tu máquina:
   ```bash
   ssh -i ./deploy_key -p 22 deploy@SERVIDOR "docker --version"
   ```
5. Asegúrate de que el directorio `/opt/arquisoft` sea escribible por `deploy`
   (el workflow intenta `sudo mkdir` y cae a `mkdir`):
   ```bash
   sudo mkdir -p /opt/arquisoft && sudo chown deploy:deploy /opt/arquisoft
   ```
6. El puerto `8080` debe estar accesible (firewall / reverse proxy).

---

## 5. Cómo apuntar el despliegue a OTRO servidor

El workflow no tiene el servidor "hardcodeado": todo viene de secretos. Para
cambiar de servidor (o crear uno nuevo):

1. Prepara el **nuevo servidor** siguiendo §4 (Docker + usuario `deploy` + clave SSH).
2. En `Settings → Secrets and variables → Actions`, **actualiza** los valores:
   - `SSH_HOST` → IP/dominio del nuevo servidor.
   - `SSH_USER`, `SSH_PORT` → si cambian.
   - `SSH_PRIVATE_KEY` → la clave privada cuya pública pusiste en el nuevo servidor.
   - `ENV_FILE` → si cambian hosts/credenciales de la infraestructura del nuevo servidor.
3. Vuelve a desplegar: push a `develop`, o **Actions → CD Deploy (SSH) → Run workflow**.

No hay que tocar el YAML para cambiar de servidor — solo los secretos.

> Si quieres **varios entornos** (staging y prod), la forma recomendada es usar
> *GitHub Environments* (`Settings → Environments`): crea `staging` y `production`,
> define los mismos secretos en cada uno, añade `environment: <nombre>` al job y
> condiciona el entorno según la rama. Es una extensión natural de este flujo.

---

## 6. Probar primero en una rama feature

`workflow_dispatch` y `workflow_run` **solo se ejecutan desde la rama por defecto**
(`main`). Por eso, para validar el despliegue **desde la rama feature antes de
fusionar**, `deploy.yml` trae una nota temporal para activarlo por `push`:

1. En [`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml), reemplaza
   temporalmente la línea de ramas del `push` por la versión que incluye la feature:
   ```yaml
   on:
     push:
       branches: [develop, feature/cd-deploy-ssh]   # ← TEMPORAL para probar
   ```
2. Configura los secretos (§2, §3) y prepara el servidor (§4).
3. Haz push de la rama feature. Observa en **Actions** que corren, en orden:
   `CI Backend`, `Docker Build & Push` y `CD Deploy (SSH)`.
4. Verifica en el servidor:
   ```bash
   docker ps | grep arquisoft-backend
   curl -fsS http://localhost:8080/api/actuator/health
   ```
5. **Antes de fusionar a `develop`**, revierte la línea de ramas a:
   ```yaml
   on:
     push:
       branches: [develop]
   ```

> ⚠️ Probar desde la feature despliega al **mismo servidor** configurado en los
> secretos. Si no quieres tocar producción durante la prueba, apunta `SSH_HOST`/
> `ENV_FILE` a un servidor de pruebas mientras validas.

---

## 7. Operación y troubleshooting

```bash
# Estado y logs del backend
docker ps
docker logs --tail 100 -f arquisoft-backend

# Ver qué imagen está corriendo
docker inspect --format '{{.Config.Image}}' arquisoft-backend

# Reiniciar / detener
docker restart arquisoft-backend
docker rm -f arquisoft-backend
```

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| `ssh: handshake failed` / timeout | `SSH_HOST/PORT/KEY` mal, o firewall | Verifica §4.4 y que la pública esté en `authorized_keys` |
| `denied / unauthorized` al `docker pull` | Paquete GHCR privado sin credenciales | Define `GHCR_USERNAME` + `GHCR_TOKEN`, o haz público el paquete |
| `La imagen ... no se publicó a tiempo` | `docker-build.yml` falló o tardó | Revisa ese workflow; el deploy reintenta 30×10s (~5 min) |
| Health check falla | DB/Keycloak/RabbitMQ inalcanzables desde el contenedor | Revisa hosts/credenciales en `ENV_FILE`; mira `docker logs` |
| App arranca pero no responde fuera | Puerto 8080 cerrado | Abre firewall / configura reverse proxy |

---

## Referencias

- [docker-build.yml](../.github/workflows/docker-build.yml) — publicación de la imagen
- [.env.example](../.env.example) — plantilla de variables de runtime
- [DESPLIEGUE_ALLOY_COOLIFY.md](DESPLIEGUE_ALLOY_COOLIFY.md) — despliegue alternativo con Coolify
- [GUIA_DOCKERFILE.md](GUIA_DOCKERFILE.md) — detalles de la imagen
