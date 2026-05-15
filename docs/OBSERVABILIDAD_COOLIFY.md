# Observabilidad — Coolify

> Arquitectura two-server: Alloy corre en el mismo servidor que el backend (Server 1) y envía
> logs y métricas al stack de observabilidad en un servidor dedicado (Server 2).

```
Server 1 (backend)                     Server 2 (observabilidad)
┌────────────────────────────┐          ┌──────────────────────────────┐
│  backend (Spring Boot)     │          │  Loki  ←─── Grafana          │
│       ↓ stdout JSON        │          │   ↑              ↑           │
│  Alloy (docker socket)  ───┼─3100────▶│  push         Prometheus     │
│       ↓ node_exporter   ───┼─9090────▶│  push           ↑           │
└────────────────────────────┘          └──────────────────────────────┘
```

---

## Archivos de configuración

| Archivo | Propósito |
|---|---|
| [`infra/coolify/docker-compose.yml`](../infra/coolify/docker-compose.yml) | Stack de observabilidad (Server 2): Loki, Prometheus, Grafana |
| [`infra/coolify/docker-compose-alloy.yml`](../infra/coolify/docker-compose-alloy.yml) | Stack de captura (Server 1): Alloy con acceso al Docker socket |
| [`infra/coolify/config.alloy`](../infra/coolify/config.alloy) | Pipeline Alloy: Docker socket → filtro backend → Loki + métricas → Prometheus |

---

## Despliegue

### Pre-requisitos

- Backend desplegado en Server 1 con `SPRING_PROFILES_ACTIVE=prod`.
- Variable de entorno `GRAFANA_PASSWORD` configurada en el recurso de Coolify (Server 2).
- Red `coolify` ya creada en ambos servidores (se genera al desplegar la primera app).
- IP privada de Server 2 disponible para configurar `LOKI_URL` y `PROMETHEUS_URL`.

---

### Stack observabilidad — Server 2

#### 1. Desplegar desde Coolify UI

En Coolify → **New Resource → Docker Compose** → apuntar al repositorio y seleccionar
`infra/coolify/docker-compose.yml` como compose file.

Coolify gestiona el HTTPS y el dominio de Grafana desde su panel. No se requiere
configuración manual de Traefik.

#### 2. Verificar

```bash
ssh root@<SERVER2_IP> 'docker ps --format "table {{.Names}}\t{{.Status}}"'
# Esperado: loki (healthy), prometheus (healthy), grafana (healthy)
```

---

### Stack Alloy — Server 1

#### 1. Copiar `config.alloy` al servidor (SSH — una sola vez)

Alloy lee la configuración desde una ruta fija del host. Debe copiarse antes del
primer deploy y cada vez que el archivo cambie.

```bash
ssh root@<SERVER1_IP> 'mkdir -p /opt/alloy'
scp infra/coolify/config.alloy root@<SERVER1_IP>:/opt/alloy/config.alloy
```

Para actualizar sin redeployar el stack:

```bash
scp infra/coolify/config.alloy root@<SERVER1_IP>:/opt/alloy/config.alloy
ssh root@<SERVER1_IP> 'docker restart alloy'
```

#### 2. Configurar variables de entorno en Coolify (Server 1)

El `docker-compose-alloy.yml` requiere dos variables que apuntan al servidor de
observabilidad. Configurarlas en el recurso de Coolify de Server 1, o directamente
en el compose file antes del deploy:

| Variable | Valor |
|---|---|
| `LOKI_URL` | `http://<SERVER2_PRIVATE_IP>:3100/loki/api/v1/push` |
| `PROMETHEUS_URL` | `http://<SERVER2_PRIVATE_IP>:9090/api/v1/write` |

> **Firewall:** el puerto 3100 de Server 2 debe aceptar conexiones desde la IP privada
> de Server 1. El puerto 9090 aplica igual si se quiere push de métricas cross-server.

#### 3. Desplegar desde Coolify UI

En Coolify (Server 1) → **New Resource → Docker Compose** → seleccionar
`infra/coolify/docker-compose-alloy.yml` como compose file.

> **`group_add: ["999"]`:** añade el GID del grupo `docker` del host al contenedor,
> permitiéndole leer el socket sin elevar privilegios a root. Si el deploy falla con
> `permission denied on /var/run/docker.sock`, verificar el GID real con
> `stat -c '%g' /var/run/docker.sock` en Server 1 y ajustar el valor en el compose.

#### 4. Verificar

```bash
ssh root@<SERVER1_IP> 'docker logs alloy --tail 20 2>&1'
```

**Salida esperada:**
```
level=info msg="finished transferring logs" component=tailer container=docker/...
```

---

## Stack

### Server 2 — observabilidad

| Servicio | Imagen | Puerto interno | Expuesto |
|---|---|---|---|
| Loki | `grafana/loki:3.3.2` | `3100` | Sí — red privada entre servidores (firewall) |
| Prometheus | `prom/prometheus:v3.1.0` | `9090` | No (exponer si se requiere push cross-server) |
| Grafana | `grafana/grafana:11.5.0` | `3000` | Sí — gestionado por Coolify (HTTPS) |

Todos comparten la red `coolify` (externa, gestionada por Coolify) en Server 2.

### Server 1 — captura

| Servicio | Imagen | Puerto | Expuesto |
|---|---|---|---|
| Alloy | `grafana/alloy:v1.5.1` | `12345` (health) | No |

---

## Datasources en Grafana

Configurar en Grafana (Server 2): **Connections → Data sources → Add → Loki / Prometheus**.

| Datasource | URL interna (misma red Docker en Server 2) |
|---|---|
| Loki | `http://loki:3100` |
| Prometheus | `http://prometheus:9090` |

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
| `grafana.arquisoft.top` | Panel de visualización (gestionado por Coolify) |
| `loki.arquisoft.top` | Recepción de logs desde entornos locales de los devs |

> `loki.arquisoft.top` es el endpoint al que apunta `infra/local/config.alloy`.
> Prometheus y Alloy no tienen exposición pública.

---

## Mantenimiento

```bash
# ── Server 1 (Alloy) ──────────────────────────────────────────

# Logs en tiempo real
ssh root@<SERVER1_IP> 'docker logs alloy -f 2>&1'

# Reiniciar tras actualizar config.alloy
scp infra/coolify/config.alloy root@<SERVER1_IP>:/opt/alloy/config.alloy
ssh root@<SERVER1_IP> 'docker restart alloy'

# ── Server 2 (observabilidad) ─────────────────────────────────

# Estado de los servicios
ssh root@<SERVER2_IP> 'docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Image}}"'

# Logs de Loki
ssh root@<SERVER2_IP> 'docker logs loki --tail 30 2>&1'
```

> Los stacks se gestionan desde Coolify UI (redeploy desde el repo). No se actualizan via SSH.
