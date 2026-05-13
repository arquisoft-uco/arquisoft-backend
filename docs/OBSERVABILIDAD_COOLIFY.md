# Observabilidad en Coolify — Setup

## Pre-requisitos
- Acceso SSH al servidor: `ssh root@<SERVER_IP>`
- App desplegada en Coolify con `SPRING_PROFILES_ACTIVE` configurado
- DNS `grafana.<dominio>` apuntando a `<SERVER_IP>`

---

## 1. Directorios en el servidor

```bash
ssh root@<SERVER_IP> 'mkdir -p /opt/arquisoft/logs && chown 1000:1000 /opt/arquisoft/logs && chmod 755 /opt/arquisoft/logs && mkdir -p /opt/observability/alloy'
```

## 2. Subir config.alloy

```bash
scp infra/local/config.alloy root@<SERVER_IP>:/opt/observability/alloy/config.alloy
```

> El `config.alloy` del servidor debe tener `__path__ = "/opt/arquisoft/logs/*.log"` y `url = "http://loki:3100/loki/api/v1/push"`.

## 3. Subir docker-compose del stack

```bash
scp infra/coolify/docker-compose.yml root@<SERVER_IP>:/opt/observability/docker-compose.yml
```

## 4. Bind mount en Coolify UI

En la app del backend → **Storages** → **Directory Mount**:

| Source (host) | Destination (container) |
|---|---|
| `/opt/arquisoft/logs` | `/app/logs` |

Hacer **Redeploy** y verificar:

```bash
ssh root@<SERVER_IP> 'ls -la /opt/arquisoft/logs/'
```

> Debe aparecer `arquisoft.log`.

## 5. Levantar el stack

```bash
ssh root@<SERVER_IP> 'cd /opt/observability && docker compose up -d && docker compose ps'
```

## 6. Verificar

```bash
ssh root@<SERVER_IP> 'docker logs alloy --tail 10 2>&1'
```

> Debe aparecer `start tailing file ... arquisoft.log` sin errores de Loki ni Prometheus.

## 7. Grafana — datasource Loki

1. Abrir `https://grafana.<dominio>` → login `admin` / `$GRAFANA_PASSWORD`
2. **Connections → Data sources → Add → Loki**
3. URL: `http://loki:3100` → **Save & test**

---

## Comandos de mantenimiento

```bash
# Ver logs en tiempo real
ssh root@<SERVER_IP> 'docker logs alloy -f 2>&1'

# Reiniciar stack completo
ssh root@<SERVER_IP> 'cd /opt/observability && docker compose restart'

# Verificar archivo de log activo
ssh root@<SERVER_IP> 'tail -f /opt/arquisoft/logs/arquisoft.log'
```
