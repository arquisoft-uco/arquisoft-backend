# Configuración de Observabilidad — Arquisoft Backend

> Alineado con **ADR-005** (PLG Stack: Prometheus + Loki + Grafana).
> Última actualización: logging Docker-native — stdout capturado por Alloy vía Docker socket.

---

## Stack tecnológico (PLG + A)

| Componente | Rol | Versión/Módulo |
|---|---|---|
| **StructuredLogEncoder** | Serializa eventos de log como JSON en stdout (perfil `prod`) | Spring Boot 4.x nativo |
| **AuditFilter** | Inyecta `userId` en MDC; emite evento `AUDIT` al finalizar cada request | `seguridad:infrastructure` |
| **TraceIdFilter** | Inyecta `traceId` (UUID por request) en MDC — orden -300, antes de cualquier filtro de aplicación | `shared:web` |
| **Grafana Alloy** | Lee stdout de contenedores Docker vía socket, extrae labels, envía a Loki | `latest` |
| **Loki** | Almacén de logs indexado por labels | `https://loki.arquisoft.top` |
| **Grafana** | Visualización y alertas | `https://grafana.arquisoft.top` |
| **Prometheus** | Métricas (CPU, RAM, latencia, pool BD) | via Actuator `/actuator/prometheus` |

---

## Arquitectura del pipeline de logs

```
Spring Boot (StructuredLogEncoder — perfil prod)
    │  JSON por línea → stdout del proceso
    ▼
Docker daemon (captura stdout de todos los contenedores)
    ▼
Grafana Alloy (loki.source.docker vía /var/run/docker.sock)
    │  loki.relabel: extrae container_name, filtra al backend
    │  loki.process: extrae level como stream label
    │  Descarta DEBUG/TRACE
    ▼
Loki (https://loki.arquisoft.top/loki/api/v1/push)
    ▼
Grafana Explore / Dashboards
```

**Regla de diseño:** el backend escribe solo a stdout — Docker gestiona la entrega. Sin volúmenes compartidos ni archivos de log. Alloy lee directamente del Docker socket.

**Requisito:** el backend debe correr en Docker (`docker-compose.yml` raíz) con perfil `prod` para emitir JSON. Con `./gradlew bootRun --args='--spring.profiles.active=dev'` los logs son texto ANSI y Alloy no puede parsear los campos.

---

## Módulo `shared:logger`

Ubicación: `shared/logger/build.gradle`

```gradle
dependencies {
    // El perfil prod usa StructuredLogEncoder (nativo de Spring Boot 4.x) para JSON
    // en stdout — sin dependencias de terceros para logging JSON.
    implementation "org.springframework.boot:spring-boot-starter-logging"
}
```

El módulo raíz lo importa en `build.gradle`:

```gradle
implementation project(':shared:logger')
```

**Uso en código:** solo `@Slf4j` (Lombok). No existe bean `AppLogger` ni puerto `DomainLogger`. La capa de dominio no tiene logging.

---

## Configuración de Logback (`shared/logger/src/main/resources/logback-spring.xml`)

Tres perfiles — todos con `ConsoleAppender` a stdout únicamente:

| Perfil | Formato | Root level | Propósito |
|---|---|---|---|
| `dev` | Texto + colores ANSI | `INFO` | Lectura humana en terminal local |
| `prod` | JSON (StructuredLogEncoder, formato logstash) | `WARN` | Machine-readable para Alloy → Loki |
| `!dev & !prod` | Texto plano (sin ANSI) | `INFO` | Fallback para tests y arranque sin perfil |

`ConsoleAppender` (Logback 1.5+) usa `UnsynchronizedAppenderBase` + `ReentrantLock` — no `synchronized` — sin riesgo de VT pinning. No se necesita `AsyncAppender`.

**Campos en cada línea JSON (perfil `prod`, `StructuredLogEncoder` formato logstash):**

```json
{
  "@timestamp":  "2026-05-11T20:39:01.471-05:00",
  "@version":    "1",
  "message":     "Started ArquisoftApplication in 32s",
  "logger_name": "com.arquisoft.ArquisoftApplication",
  "thread_name": "main",
  "level":        "INFO",
  "level_value":  20000,
  "traceId":     "550e8400-e29b-41d4-a716-446655440000",
  "userId":      "uuid-yyy",
  "httpMethod":  "POST",
  "httpStatus":  "200",
  "httpUri":     "/api/auth/login",
  "durationMs":  "142",
  "clientIp":    "192.168.1.10"
}
```

`traceId` (UUID) lo inyecta `TraceIdFilter` (orden -300) en el MDC al inicio de cada request.
`userId` lo inyecta `AuditFilter` (LOWEST_PRECEDENCE). Los campos `http*`, `durationMs`, `clientIp` solo aparecen en el evento `AUDIT`.
Los campos `traceId`, `userId`, `http*` están **ausentes** en logs de startup (MDC vacío antes del primer request).

No hay diferencias de estructura entre perfiles — solo formato de salida y root level.

---

## Niveles de log por perfil (`application-{perfil}.yml`)

### `application-dev.yml`

```yaml
logging:
  level:
    com.arquisoft: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    # org.hibernate.type.descriptor.sql.BasicBinder omitido — loguea valores de parámetros SQL (PII)
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

## Grafana Alloy local (`infra/local/`)

### Prerrequisito: backend corriendo en Docker

Alloy lee logs del Docker socket — el backend debe estar corriendo como contenedor con perfil `prod`:

```bash
# Verificar que el backend está corriendo
docker ps --filter name=arquisoft-backend
```

El archivo `.env` en la raíz del proyecto define `SPRING_PROFILES_ACTIVE=prod`, que `docker-compose.yml` hereda automáticamente. Con este perfil el backend emite JSON (StructuredLogEncoder) legible por Alloy.

### Paso a paso para levantar Alloy local

**1. Verificar que el config existe**

```bash
ls infra/local/config.alloy infra/local/docker-compose.yml
```

**2. Verificar que no hay un contenedor anterior**

```bash
docker ps -a --filter name=alloy-local
```

Si aparece en estado `Exited`, eliminarlo antes de continuar:

```bash
docker rm alloy-local
```

> Si el contenedor falló en un arranque anterior (p.ej. por error de sintaxis
> en `config.alloy`), Docker lo deja en `Exited` con el nombre `/alloy-local`
> reservado. El siguiente `docker compose up` falla con *"container name
> already in use"* hasta que se elimine manualmente.

**3. Verificar permiso de lectura del Docker socket**

```bash
ls -la /var/run/docker.sock
# srw-rw---- 1 root docker → el usuario debe estar en el grupo docker
```

Si el usuario no está en el grupo `docker`:

```bash
sudo usermod -aG docker $USER  # requiere cerrar sesión y volver a entrar
```

**4. Levantar el contenedor**

```bash
docker compose -f infra/local/docker-compose.yml up -d
```

**5. Verificar que está leyendo logs del backend**

```bash
docker logs alloy-local 2>&1 | tail -20
```

Confirmación de funcionamiento — este mensaje debe aparecer de forma continua:

```
level=info msg="finished transferring logs" component=tailer container=docker/...
```

Si en cambio aparece un error de configuración:

```
level=error msg="error reading config"
```

Hay un error de sintaxis en `config.alloy`. Corregirlo, eliminar el contenedor con `docker rm alloy-local` y volver al paso 4.

> **Error transitorio al arrancar:** Es normal ver errores `entry too far behind` en el primer arranque. Loki rechaza logs con timestamp muy antiguo (buffer Docker del inicio del contenedor). Se auto-resuelve — los logs nuevos se envían correctamente.

---

### Troubleshooting

| Síntoma | Causa | Solución |
|---|---|---|
| `container name "/alloy-local" is already in use` | Contenedor viejo en estado `Exited` | `docker rm alloy-local` y relanzar |
| `permission denied while trying to connect to the Docker daemon socket` | Usuario no en el grupo `docker` | `sudo usermod -aG docker $USER` y re-login |
| `entry too far behind` al arrancar | Loki rechaza timestamps viejos del buffer Docker | Error transitorio — se auto-resuelve; los logs nuevos fluyen correctamente |
| `could not perform the initial load` | Error de configuración de Alloy | Revisar sintaxis con `docker logs alloy-local 2>&1 \| head -30` |
| Los logs llegan a Loki pero sin label `level` | El backend no corre con perfil `prod` (logs en texto ANSI, no JSON) | Verificar `SPRING_PROFILES_ACTIVE=prod` en `.env` y reiniciar el backend |
| WARN `the attribute 'version' is obsolete` | Campo `version:` obsoleto en docker-compose | Warning inocuo — no afecta el funcionamiento |

---

### Comandos útiles de operación

```bash
# Ver logs en tiempo real
docker logs -f alloy-local

# Reiniciar sin borrar el contenedor (recarga config.alloy)
docker compose -f infra/local/docker-compose.yml restart alloy

# Detener y eliminar el contenedor
docker compose -f infra/local/docker-compose.yml down

# Verificar cuántos bytes ha transferido Alloy desde el backend
docker logs alloy-local 2>&1 | grep "finished transferring" | tail -5

# Verificar que el pipeline llega a Loki remoto (respuesta esperada: 204)
curl -s -o /dev/null -w "%{http_code}" \
  -H "Content-Type: application/json" \
  -XPOST "https://loki.arquisoft.top/loki/api/v1/push" \
  --data-raw "{\"streams\":[{\"stream\":{\"job\":\"test-local\"},\"values\":[[\"$(date +%s%N)\",\"Prueba desde PC\"]]}]}"
```

### Pipeline configurado en `infra/local/config.alloy`

```hcl
// 1. Descubrimiento de contenedores Docker locales
discovery.docker "contenedores_locales" {
    host = "unix:///var/run/docker.sock"
}

// 2. Metadatos Docker: promueve container_name y filtra al backend.
//    Los __meta_* de discovery.docker NO se propagan automáticamente a Loki.
loki.relabel "docker_metadata" {
    forward_to = [loki.process.extract_labels.receiver]

    rule {
        source_labels = ["__meta_docker_container_name"]
        regex         = "/(.*)"
        target_label  = "container"
    }
    rule {
        source_labels = ["container"]
        regex         = "arquisoft-backend"
        action        = "keep"
    }
}

// 3. Pipeline: extrae labels del JSON de Spring Boot (prod), promueve level.
loki.process "extract_labels" {
    forward_to = [loki.write.remote_loki.receiver]

    stage.json {
        expressions = {
            level   = "level",
            message = "message",
            logger  = "logger_name",
            traceId = "traceId",
            userId  = "userId",
        }
    }

    // Baja cardinalidad → stream label (usable en {})
    // container ya es stream label (viene de loki.relabel)
    stage.labels {
        values = { level = "" }
    }

    stage.drop {
        expression          = "\"level\":\"(?:DEBUG|TRACE)\""
        drop_counter_reason = "debug_trace_noise"
    }
}

// 4. Fuente: captura stdout de contenedores vía Docker socket.
loki.source.docker "backend_logs" {
    host       = "unix:///var/run/docker.sock"
    targets    = discovery.docker.contenedores_locales.targets
    forward_to = [loki.relabel.docker_metadata.receiver]
}

// 5. Envío a Loki remoto
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

| Label | Origen | Cardinalidad | Valores típicos |
|---|---|---|---|
| `container` | `loki.relabel` (de `__meta_docker_container_name`) | Baja | `arquisoft-backend` |
| `level` | JSON → `stage.labels` | Baja | `INFO`, `WARN`, `ERROR` |
| `origin` | `external_labels` | Baja | `local-brayan`, otros devs |
| `job` | `external_labels` | Baja | `backend-java-local` |

> **Regla de cardinalidad Loki:** solo campos con pocos valores distintos van como stream labels en `{}`. `traceId` y `userId` son alta cardinalidad — se consultan con `| json` en Grafana.

---

## Queries en Grafana (LogQL)

```logql
# Todos los logs del backend desde tu instancia local
{container="arquisoft-backend", origin="local-brayan"}

# Solo errores
{container="arquisoft-backend", origin="local-brayan", level="ERROR"}

# Buscar texto en el mensaje
{container="arquisoft-backend", level="ERROR"} |= "JWT"

# Trazar un request completo (alta cardinalidad → | json)
{container="arquisoft-backend"} | json | traceId="abc123def456"

# Logs de un usuario específico
{container="arquisoft-backend", origin="local-brayan"} | json | userId="uuid-xxx"

# Rate de errores por minuto (panel de Grafana)
rate({container="arquisoft-backend", level="ERROR"}[1m])
```

---

## Despliegue en producción (Coolify)

### Backend

- Perfil activo: `prod`
- Logs: consola (stdout) únicamente — Docker los captura y Alloy los lee vía socket
- Sin volúmenes compartidos de log

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

> ⚠️ El archivo en el servidor (`/opt/observability/alloy/config.alloy`) requiere actualización manual para alinear con el nuevo patrón Docker-native. Ver `infra/local/config.alloy` como referencia.

```hcl
discovery.docker "containers" {
    host = "unix:///var/run/docker.sock"
}

// Promueve container_name; elimina la "/" inicial que antepone Docker
loki.relabel "add_labels" {
    forward_to = [loki.write.local_loki.receiver]
    rule {
        source_labels = ["__meta_docker_container_name"]
        regex         = "/(.*)"
        target_label  = "container"
    }
}

loki.source.docker "docker_logs" {
    host       = "unix:///var/run/docker.sock"
    targets    = discovery.docker.containers.targets
    forward_to = [loki.relabel.add_labels.receiver]
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

---

## Pendiente manual (servidor Coolify)

El archivo `/opt/observability/alloy/config.alloy` en el servidor requiere actualización para:
1. Añadir regex `/(.*)`  en la regla de `container` para eliminar el `/` inicial
2. Verificar que `loki.source.docker` reenvía a `loki.relabel` (no directamente a `loki.write`)

Referencia: `infra/coolify/docker-compose.yml` + patrón de `infra/local/config.alloy`.