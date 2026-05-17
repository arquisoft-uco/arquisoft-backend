# Observabilidad — Entorno Local

> Pipeline: backend (stdout JSON) → Docker socket → Grafana Alloy → Loki remoto (`loki.arquisoft.top`) → Grafana.

---

## Archivos de configuración

| Archivo | Propósito |
|---|---|
| [`infra/local/config.alloy`](../infra/local/config.alloy) | Pipeline Alloy: descubrimiento de contenedores, filtro al backend, extracción de labels, envío a Loki |
| [`infra/local/docker-compose.yml`](../infra/local/docker-compose.yml) | Levanta Alloy con acceso al Docker socket del host |

---

## Pre-requisitos

**1. Backend corriendo como contenedor Docker con perfil `prod`:**

```bash
docker ps --filter name=arquisoft-backend
```

El archivo `.env` en la raíz define `SPRING_PROFILES_ACTIVE=prod`, que `docker-compose.yml` hereda automáticamente. Con perfil `prod` el backend emite JSON (`StructuredLogEncoder`); con `dev` emite texto ANSI y Alloy no parsea los campos.

**2. Usuario en el grupo `docker`** (necesario para leer el socket):

```bash
ls -la /var/run/docker.sock                   # srw-rw---- → grupo docker requerido
sudo usermod -aG docker $USER                  # requiere re-login
```

---

## Personalizar el label `origin`

El label `origin` en `external_labels` de `config.alloy` identifica al desarrollador en Loki. **Cada desarrollador debe cambiarlo al suyo** para no mezclar logs con los de otros miembros del equipo.

```river
// infra/local/config.alloy — external_labels
external_labels = { origin = "local-tuNombre", job = "backend-java-local" }
```

---

## Levantar Alloy

```bash
# 1. Verificar si hay contenedor anterior en estado Exited
docker ps -a --filter name=alloy-local
docker rm alloy-local                          # si aparece en Exited

# 2. Levantar
docker compose -f infra/local/docker-compose.yml up -d

# 3. Verificar funcionamiento
docker logs alloy-local 2>&1 | tail -20
```

**Salida esperada (éxito):**
```
level=info msg="finished transferring logs" component=tailer container=docker/...
```

> **Error transitorio al arrancar:** `entry too far behind` — Loki rechaza timestamps del buffer Docker acumulados previo al arranque de Alloy. Se auto-resuelve; los logs nuevos fluyen correctamente.

---

## Labels disponibles en Loki

| Label | Origen | Valores típicos |
|---|---|---|
| `container` | `discovery.relabel` → `__meta_docker_container_name` | `arquisoft-backend` |
| `level` | JSON → `stage.labels` | `INFO`, `WARN`, `ERROR` |
| `origin` | `external_labels` en `config.alloy` | `local-tuNombre` |
| `job` | `external_labels` | `backend-java-local` |

`traceId` y `userId` son alta cardinalidad — se consultan con `| json`, no como stream labels.

> **Nota sobre `__meta_docker_*`:** estos metadatos solo están disponibles en `discovery.relabel` referenciado via `relabel_rules` en `loki.source.docker`. No están disponibles en `loki.relabel` usado como `forward_to`. Ver comentarios en `config.alloy`.

---

## Campos JSON emitidos por el backend

El backend usa `StructuredLogEncoder` (Spring Boot 4.x nativo, perfil `prod`). Campos relevantes para `| json` en Grafana:

| Campo | Quién lo inyecta | Cuándo está presente |
|---|---|---|
| `level`, `message`, `logger_name` | Logback | Siempre |
| `traceId` | `TraceIdFilter` (orden -300) | Por request (ausente en startup) |
| `userId` | `AuditFilter` (LOWEST_PRECEDENCE) | Por request autenticado |
| `httpMethod`, `httpUri`, `httpStatus`, `durationMs`, `clientIp` | `AuditFilter` | Solo en evento `AUDIT` |

Rutas excluidas del evento `AUDIT`: `/api/actuator/`, `/api/swagger-ui`, `/api/v3/api-docs`, `/api/swagger-resources`.

---

## Queries en Grafana (LogQL)

Usar siempre `{container="arquisoft-backend"}` — **no** solo `{origin="..."}` para evitar mezclar con logs internos de Alloy/Traefik.

```logql
# Base — perfil prod emite WARN/ERROR por defecto (com.arquisoft: INFO)
{container="arquisoft-backend", origin="local-tuNombre"}

# Por nivel
{container="arquisoft-backend", origin="local-tuNombre", level="ERROR"}
{container="arquisoft-backend", origin="local-tuNombre", level="WARN"}

# Búsqueda en texto
{container="arquisoft-backend", origin="local-tuNombre"} |= "NullPointerException"

# Trazabilidad — alta cardinalidad: usar | json
{container="arquisoft-backend"} | json | traceId="<uuid>"
{container="arquisoft-backend", origin="local-tuNombre"} | json | userId="<uuid>"

# Formato legible
{container="arquisoft-backend", origin="local-tuNombre"} | json | line_format "[{{.level}}] {{.logger_name}} — {{.message}}"

# Métricas (paneles Grafana)
rate({container="arquisoft-backend", level="ERROR"}[1m])
count_over_time({container="arquisoft-backend", level="WARN"}[1h])
```

---

## Troubleshooting

| Síntoma | Causa | Solución |
|---|---|---|
| `container name "/alloy-local" is already in use` | Contenedor en estado `Exited` | `docker rm alloy-local` y relanzar |
| `permission denied on docker socket` | Usuario fuera del grupo `docker` | `sudo usermod -aG docker $USER` + re-login |
| `entry too far behind` al arrancar | Timestamps viejos del buffer Docker | Error transitorio — se auto-resuelve |
| `error reading config` | Sintaxis inválida en `config.alloy` | `docker logs alloy-local 2>&1 \| head -30` |
| Sin label `level` en Loki | Backend no corre con perfil `prod` | Verificar `SPRING_PROFILES_ACTIVE=prod` en `.env` |

---

## Verificación directa del endpoint Loki

```bash
curl -s -o /dev/null -w "%{http_code}" \
  -H "Content-Type: application/json" \
  -XPOST "https://loki.arquisoft.top/loki/api/v1/push" \
  --data-raw "{\"streams\":[{\"stream\":{\"job\":\"test-local\"},\"values\":[[\"$(date +%s%N)\",\"Prueba desde PC\"]]}]}"
# Respuesta esperada: 204
```
