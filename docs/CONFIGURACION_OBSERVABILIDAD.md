# Configuración de Observabilidad — Arquisoft Backend

> Alineado con **ADR-005** (PLG Stack: Prometheus + Loki + Grafana).
> Última actualización: refleja la implementación real del módulo `shared:logger`.

# Configuración de Observabilidad — Arquisoft Backend

> Alineado con **ADR-005** (PLG Stack: Prometheus + Loki + Grafana).
> Última actualización: refleja la implementación real del módulo `shared:logger`.

---

## Stack tecnológico (PLG + A)

| Componente | Rol | Versión |
|---|---|---|
| **logstash-logback-encoder** | Serializa eventos de log como JSON en el proceso Java | `8.0` |
| **micrometer-tracing-bridge-brave** | Inyecta `traceId` + `spanId` en MDC automáticamente | Gestionada por Spring Boot BOM |
| **Grafana Alloy** | Agente único: lee el archivo de log, extrae labels, envía a Loki | `latest` |
| **Loki** | Almacén de logs indexado por labels | `https://loki.arquisoft.top` |
| **Grafana** | Visualización y alertas | `https://grafana.arquisoft.top` |
| **Prometheus** | Métricas (CPU, RAM, latencia, pool BD) | via Actuator `/actuator/prometheus` |

---

## Arquitectura del pipeline de logs

```
Spring Boot (logstash-logback-encoder)
    │  JSON por línea → logs/arquisoft.log
    ▼
Grafana Alloy (config.alloy / docker-compose.alloy-local.yml)
    │  Extrae level + env como stream labels
    │  Descarta DEBUG/TRACE
    ▼
Loki (https://loki.arquisoft.top/loki/api/v1/push)
    ▼
Grafana Explore / Dashboards
```

**Regla de diseño:** el backend nunca conoce a Loki. Alloy hace la recolección. El backend solo escribe al archivo — Alloy transforma y envía.

---

## Módulo `shared:logger`

Ubicación: `shared/logger/build.gradle`

Dependencias que aporta a cualquier módulo que lo importe:

```gradle
implementation "org.springframework.boot:spring-boot-starter-logging"
implementation "net.logstash.logback:logstash-logback-encoder:${logstashVersion}"
implementation "io.micrometer:micrometer-tracing-bridge-brave"
```

El módulo raíz lo importa en `build.gradle`:

```gradle
implementation project(':shared:logger')
```

**Uso en código:** solo `@Slf4j` (Lombok). No existe bean `AppLogger` ni puerto `DomainLogger`. La capa de dominio no tiene logging.

---

## Configuración de Logback (`src/main/resources/logback-spring.xml`)

Estándar unificado para **todos los perfiles**:

| Appender | Destino | Formato | Propósito |
|---|---|---|---|
| `CONSOLE` | stdout | Texto + colores ANSI | Lectura humana en terminal |
| `JSON_FILE` | `logs/arquisoft.log` | JSON (`LogstashEncoder`) | Alloy → Loki |
| `ASYNC_CONSOLE` | wraps `CONSOLE` | — | Desacopla Virtual Threads del I/O |
| `ASYNC_FILE` | wraps `JSON_FILE` | — | Desacopla Virtual Threads del I/O |

**Campos incluidos en cada línea JSON:**

```json
{
  "@timestamp": "2026-05-11T20:39:01.471-05:00",
  "message": "Started ArquisoftApplication in 32s",
  "logger_name": "com.arquisoft.ArquisoftApplication",
  "level": "INFO",
  "app": "arquisoft-backend",
  "env": "prod",
  "traceId": "abc123",
  "spanId": "def456",
  "requestId": "uuid-xxx",
  "userId": "uuid-yyy"
}
```

`traceId` y `spanId` los inyecta `micrometer-tracing-bridge-brave` en el MDC automáticamente por request. `requestId` y `userId` los inyecta el `RequestCorrelationFilter`.

### Diferencias entre perfiles (solo comportamiento, no estructura)

| Parámetro | `dev` | `prod` |
|---|---|---|
| `JSON_FILE` retención | 7 días / 500 MB | 30 días / 2 GB |
| `AsyncAppender.queueSize` | 2048 | 4096 |
| `AsyncAppender.discardingThreshold` | `0` (nunca descarta) | `20` (descarta INFO/DEBUG bajo presión de cola) |
| Root level | `INFO` | `WARN` |

> **Por qué `discardingThreshold=20` en prod:** cuando la cola del `AsyncAppender` supera el 80 % de capacidad, Logback descarta eventos `INFO` y `DEBUG` para no bloquear Virtual Threads. `WARN` y `ERROR` nunca se descartan. Trade-off deliberado: throughput > completitud de `INFO` en picos.

### Regla crítica: no declarar `logging.file.name` en YAML

Si Spring Boot ve `logging.file.name` en el YAML, inyecta `LOG_FILE` y puede activar un segundo `FileAppender` interno que escribe **texto plano** en el mismo archivo — corrompiendo el JSON que espera Alloy. Los appenders los gestiona exclusivamente `logback-spring.xml`.

---

## Niveles de log por perfil (`application-{perfil}.yml`)

### `application-dev.yml`

```yaml
logging:
  level:
    com.arquisoft: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### `application-prod.yml`

```yaml
logging:
  level:
    root: WARN
    com.arquisoft: INFO        # eventos de negocio — siempre valor en Loki
    org.springframework.security: WARN
    org.springframework.web: WARN
    org.springframework.amqp: WARN
    org.springframework.data: WARN
    com.zaxxer.hikari: WARN
    org.flywaydb: WARN
    org.apache.tomcat: WARN
    com.rabbitmq: WARN
    io.micrometer: WARN
    org.hibernate: WARN
    org.hibernate.SQL: OFF     # demasiado verboso, usa traceId para debug
    org.hibernate.type: OFF
    springdoc: OFF             # deshabilitado en prod
```

**Criterio de selección de nivel:** si el log no ayuda a detectar, diagnosticar o resolver un incidente en producción, no va a Loki. `INFO` es el contrato público de la aplicación.

---

## Grafana Alloy local (`config.alloy` + `docker-compose.alloy-local.yml`)

### Arrancar Alloy local

```bash
docker compose -f docker-compose.alloy-local.yml up -d
```

### Pipeline configurado en `config.alloy`

```hcl
// 1. Descubrimiento del archivo de log
local.file_match "spring_boot_logs" {
    path_targets = [
        { "__path__" = "/logs/*.log", "app" = "arquisoft-api" },
    ]
}

// 2. Pipeline: extrae labels y descarta ruido
loki.process "extract_labels" {
    forward_to = [loki.write.remote_loki.receiver]

    stage.json {
        expressions = {
            level     = "level",
            env       = "env",
            message   = "message",
            logger    = "logger_name",
            traceId   = "traceId",
            requestId = "requestId",
            userId    = "userId",
        }
    }

    // Baja cardinalidad → stream labels (usables en {})
    stage.labels {
        values = { level = "", env = "" }
    }

    // Descartar DEBUG/TRACE antes de enviar a Loki
    stage.drop {
        expression          = "\"level\":\"(?:DEBUG|TRACE)\""
        drop_counter_reason = "debug_trace_noise"
    }
}

// 3. Lectura → pipeline
loki.source.file "backend_logs" {
    targets    = local.file_match.spring_boot_logs.targets
    forward_to = [loki.process.extract_labels.receiver]
}

// 4. Envío a Loki remoto
loki.write "remote_loki" {
    endpoint {
        url = "https://loki.arquisoft.top/loki/api/v1/push"
    }
    external_labels = {
        origin = "local-brayan",
        job    = "backend-java-local",
    }
}
```

### Labels disponibles en Loki

| Label | Origen | Cardinalidad | Valores |
|---|---|---|---|
| `app` | `path_targets` | Baja | `arquisoft-api` |
| `env` | JSON → `stage.labels` | Baja | `dev`, `prod` |
| `level` | JSON → `stage.labels` | Baja | `INFO`, `WARN`, `ERROR` |
| `origin` | `external_labels` | Baja | `local-brayan`, otros devs |
| `job` | `external_labels` | Baja | `backend-java-local` |

> **Regla de cardinalidad Loki:** solo campos con pocos valores distintos van como stream labels en `{}`. `traceId`, `requestId` y `userId` son alta cardinalidad — se consultan con `| json` en Grafana.

---

## Queries en Grafana (LogQL)

```logql
# Todos los logs de tu instancia local
{app="arquisoft-api", origin="local-brayan"}

# Solo errores de prod
{app="arquisoft-api", origin="local-brayan", env="prod", level="ERROR"}

# Buscar texto en el mensaje
{app="arquisoft-api", level="ERROR"} |= "JWT"

# Trazar un request completo (alta cardinalidad → | json)
{app="arquisoft-api"} | json | traceId="abc123def456"

# Logs de un usuario específico
{app="arquisoft-api", env="prod"} | json | userId="uuid-xxx"
```

---

## Despliegue en producción (Coolify)

### Backend

- Perfil activo: `prod`
- Logs: consola (stdout) + `logs/arquisoft.log` (JSON)
- Alloy en Coolify lee el archivo del contenedor y envía a Loki interno

### Stack de monitoreo (`docker-compose` en Coolify)

```yaml
services:
  loki:
    image: grafana/loki:2.9.0
    networks: [coolify]
    volumes: [loki-data:/loki]

  prometheus:
    image: prom/prometheus:latest
    command:
      - --config.file=/etc/prometheus/prometheus.yml
      - --storage.tsdb.retention.time=15d
    networks: [coolify]
    volumes: [prometheus-data:/prometheus]

  alloy:
    image: grafana/alloy:latest
    volumes:
      - /var/lib/docker/containers:/var/lib/docker/containers:ro
      - /var/run/docker.sock:/var/run/docker.sock:ro
      - ./alloy/config.alloy:/etc/alloy/config.alloy
    networks: [coolify]

  grafana:
    image: grafana/grafana:latest
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}
    networks: [coolify]
    volumes: [grafana-data:/var/lib/grafana]

networks:
  coolify:
    external: true
```

### `config.alloy` en Coolify (lee stdout de Docker)

```hcl
discovery.docker "containers" {
    host = "unix:///var/run/docker.sock"
}

loki.source.docker "docker_logs" {
    host       = "unix:///var/run/docker.sock"
    targets    = discovery.docker.containers.targets
    forward_to = [loki.relabel.add_labels.receiver]
}

loki.relabel "add_labels" {
    forward_to = [loki.write.local_loki.receiver]
    rule {
        source_labels = ["__meta_docker_container_name"]
        target_label  = "container"
    }
}

loki.write "local_loki" {
    endpoint {
        url = "http://loki:3100/loki/api/v1/push"
    }
}

prometheus.exporter.unix "node_stats" { }
prometheus.scrape "scrape_node" {
    targets    = prometheus.exporter.unix.node_stats.targets
    forward_to = [prometheus.remote_write.local_prometheus.receiver]
}
prometheus.remote_write "local_prometheus" {
    endpoint {
        url = "http://prometheus:9090/api/v1/write"
    }
}
```

### Data Sources en Grafana

| Data Source | URL interna |
|---|---|
| Loki | `http://loki:3100` |
| Prometheus | `http://prometheus:9090` |

### Dominios requeridos (DNS A → IP servidor UCO)

| FQDN | Uso |
|---|---|
| `grafana.arquisoft.top` | Panel de visualización |
| `loki.arquisoft.top` | Recepción de logs desde instancias locales |

---

## Verificación de conexión local → Loki remoto

```bash
curl -s -o /dev/null -w "%{http_code}" \
  -H "Content-Type: application/json" \
  -XPOST "https://loki.arquisoft.top/loki/api/v1/push" \
  --data-raw "{\"streams\": [{\"stream\": {\"job\": \"test-local\"}, \"values\": [[\"$(date +%s%N)\", \"Prueba desde PC\"]]}]}"
# Respuesta esperada: 204
```