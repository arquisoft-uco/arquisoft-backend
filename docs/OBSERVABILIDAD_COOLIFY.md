# Observabilidad — Coolify

> Pipeline: backend (stdout JSON) → Docker socket → Grafana Alloy → Loki local → Grafana.
> Todo el stack de monitoreo corre en el mismo host Coolify.

---

## Archivos de configuración

| Archivo | Propósito |
|---|---|
| [`infra/coolify/docker-compose.yml`](../infra/coolify/docker-compose.yml) | Stack completo: Loki, Prometheus, Alloy, Grafana con integración Traefik |
| [`infra/coolify/config.alloy`](../infra/coolify/config.alloy) | Pipeline Alloy: Docker socket → filtro al backend → Loki + métricas de sistema → Prometheus |

---

## Despliegue

### Pre-requisitos

- Backend desplegado en Coolify con `SPRING_PROFILES_ACTIVE=prod`.
- Variable de entorno `GRAFANA_PASSWORD` configurada en el recurso de Coolify.
- Red `coolify` ya creada por Coolify (se genera al desplegar la primera app).

### 1. Copiar `config.alloy` al servidor (SSH — una sola vez)

Alloy necesita el archivo en una ruta fija del host antes del primer deploy. El `docker-compose.yml` lo monta desde `/opt/observability/config.alloy`.

```bash
ssh root@<SERVER_IP> 'mkdir -p /opt/observability'
scp infra/coolify/config.alloy root@<SERVER_IP>:/opt/observability/config.alloy
```

Para actualizarlo sin tocar el stack:

```bash
scp infra/coolify/config.alloy root@<SERVER_IP>:/opt/observability/config.alloy
ssh root@<SERVER_IP> 'docker restart alloy'
```

### 2. Desplegar el stack desde Coolify UI

En Coolify → **New Resource → Docker Compose** → apuntar al repositorio y seleccionar `infra/coolify/docker-compose.yml` como compose file.

> Coolify ejecuta el compose desde su propio directorio de trabajo, por eso el `config.alloy` usa ruta absoluta (`/opt/observability/config.alloy`) y no ruta relativa.

### 3. Verificar

```bash
ssh root@<SERVER_IP> 'docker logs alloy --tail 20 2>&1'
```

**Salida esperada:**
```
level=info msg="finished transferring logs" component=tailer container=docker/...
```

> **`group_add: ["999"]` en Alloy:** añade el GID del grupo `docker` del host al contenedor, permitiéndole leer el socket sin elevar privilegios a root. Si el deploy falla con `permission denied on /var/run/docker.sock`, verificar el GID real con `stat -c '%g' /var/run/docker.sock` en el servidor y ajustar el valor.

> **Volúmenes externos:** `observability_loki-data`, `observability_prometheus-data` y `observability_grafana-data` — Coolify los crea al aprovisionar el recurso si no existen.

---

## Stack

| Servicio | Imagen | Puerto interno | Expuesto al exterior |
|---|---|---|---|
| Loki | `grafana/loki:2.9.0` | `3100` | Sí — `loki.arquisoft.top` (usado por devs locales) |
| Prometheus | `prom/prometheus:latest` | `9090` | No |
| Alloy | `grafana/alloy:latest` | `12345` (health) | No |
| Grafana | `grafana/grafana:latest` | `3000` | Sí — `grafana.arquisoft.top` (Traefik) |

Todos comparten la red `coolify` (externa, gestionada por Coolify).

---

## Datasources en Grafana

| Datasource | URL interna |
|---|---|
| Loki | `http://loki:3100` |
| Prometheus | `http://prometheus:9090` |

Configurar en Grafana: **Connections → Data sources → Add → Loki / Prometheus**.

---

## Labels disponibles en Loki

| Label | Origen | Valores típicos |
|---|---|---|
| `container` | `discovery.relabel` → `__meta_docker_container_name` | `arquisoft-backend` |
| `level` | JSON → `stage.labels` | `INFO`, `WARN`, `ERROR` |
| `job` | `external_labels` | `backend-java-coolify` |

---

## Queries en Grafana (LogQL)

```logql
# Base
{container="arquisoft-backend", job="backend-java-coolify"}

# Por nivel
{container="arquisoft-backend", job="backend-java-coolify", level="ERROR"}

# Búsqueda en texto
{container="arquisoft-backend", job="backend-java-coolify"} |= "Exception"

# Trazabilidad
{container="arquisoft-backend"} | json | traceId="<uuid>"
{container="arquisoft-backend"} | json | userId="<uuid>"

# Métricas
rate({container="arquisoft-backend", level="ERROR"}[1m])
count_over_time({container="arquisoft-backend", level="WARN"}[1h])
```

---

## DNS requeridos

| FQDN | Servicio |
|---|---|
| `grafana.arquisoft.top` | Panel de visualización (Traefik → Grafana:3000) |
| `loki.arquisoft.top` | Recepción de logs desde los entornos locales de los devs |

> `loki.arquisoft.top` es el endpoint al que apunta `infra/local/config.alloy`. Es el único punto de entrada externo para logs; Prometheus y Alloy no tienen exposición pública.

---

## Mantenimiento

```bash
# Logs de Alloy en tiempo real
ssh root@<SERVER_IP> 'docker logs alloy -f 2>&1'

# Reiniciar Alloy (ej. tras actualizar config.alloy)
ssh root@<SERVER_IP> 'docker restart alloy'

# Actualizar config.alloy en el servidor
scp infra/coolify/config.alloy root@<SERVER_IP>:/opt/observability/config.alloy
ssh root@<SERVER_IP> 'docker restart alloy'
```

> El `docker-compose.yml` se gestiona desde Coolify UI (redeploy desde el repo). No se actualiza via SSH.
