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

`correlacionId`, `transaccionId`, `transaccionPadreId` y `usuarioId` son alta cardinalidad — se consultan con `| json`, no como stream labels.

> **Nota sobre `__meta_docker_*`:** estos metadatos solo están disponibles en `discovery.relabel` referenciado via `relabel_rules` en `loki.source.docker`. No están disponibles en `loki.relabel` usado como `forward_to`. Ver comentarios en `config.alloy`.

---

## Campos JSON emitidos por el backend

El backend usa `StructuredLogEncoder` (Spring Boot 4.x nativo, perfil `prod`). Campos relevantes para `| json` en Grafana:

| Campo | Quién lo inyecta | Cuándo está presente |
|---|---|---|
| `level`, `message`, `logger_name` | Logback | Siempre |
| `servicioNombre`, `version` | `logging.structured.json.add` en `application.yml` | Siempre (perfil `prod`) |
| `correlacionId` | `TrazabilidadFilter` (orden -300) / `AbstractEventConsumer` / `@Scheduled` (`gestorTraza.abrir`) | Todo request, consumo o job programado (ausente en startup) |
| `transaccionId` | idem — **nuevo en cada salto** | idem |
| `transaccionPadreId` | idem — solo si hay un salto anterior conocido | Ver tabla "Origen del `transaccionPadreId`" abajo |
| `origen` | idem — `HTTP`, `EVENTO`, `PROGRAMADO` | idem |
| `usuarioId` | `IdentidadTrazaFilter` (`seguridad`) / consumidor AMQP | Todo el request; ver semillas abajo |
| `clienteIp`, `metodoHttp`, `rutaUri` | `TrazabilidadFilter` | **Solo `origen=HTTP`** |
| `colaEvento` | `AbstractEventConsumer` | **Solo `origen=EVENTO`** — nombre de la cola/routing key que entregó el mensaje |
| `tiempoEntrada` | `TrazabilidadFilter` / `AbstractEventConsumer` / `@Scheduled` | Todo request, consumo o job programado |
| `codigoEstado`, `duracionMs`, `tiempoSalida` | `TrazabilidadFilter` (al cerrar) | Solo en evento `AUDIT` (solo HTTP) |

`clienteIp`/`metodoHttp`/`rutaUri` y `colaEvento` son mutuamente excluyentes por diseño: `TrazaDomain`
modela el detalle propio de cada origen con un tipo sellado (`DetalleOrigenTraza` — un record por origen),
así que una traza `EVENTO` o `PROGRAMADO` nunca escribe esas tres claves HTTP en el log (antes de corregirlo,
salían con valores basura como `clienteIp=INVALID`/`rutaUri=UNKNOWN`).

### Origen del `transaccionPadreId`

Identifica de qué salto anterior nació este — útil para reconstruir el orden exacto de una cadena
HTTP → evento → evento, no solo que pertenecen a la misma `correlacionId`.

| `origen` | De dónde sale | Cuándo está ausente |
|---|---|---|
| `HTTP` | Segmento `parent-id` del header `traceparent` entrante (W3C) | Primer salto: nadie llamó a este endpoint con `traceparent` |
| `EVENTO` | Header AMQP `X-Transaction-Id`, que el productor ya escribe con su propio `transaccionId` (`TrazaMessagePostProcessor`) | El mensaje no trae esa cabecera (mensajes muy antiguos, o publicados fuera del pipeline estándar) |
| `PROGRAMADO` | — | Siempre ausente: un job programado no tiene llamador |

Rutas excluidas del evento `AUDIT`: `/api/actuator/`, `/api/swagger-ui`, `/api/v3/api-docs`, `/api/swagger-resources`
(configurable en `arquisoft.trazas.rutas-excluidas-auditoria`).

### Cómo leer `usuarioId`

No es "ausente o presente": lleva una semilla según el origen, y sólo se sobrescribe cuando hay identidad real.

| Valor | Significado |
|---|---|
| `<uuid>` | El `sub` del JWT — la autenticación se resolvió |
| `ANONYMOUS` | Petición HTTP sin identidad: 401 de autenticación, o 429 del rate limiter (corre antes de validar el JWT, por diseño) |
| `EVENT` | Consumo AMQP sin `X-User-Id` en el mensaje |
| `SYSTEM` | Tarea programada (`origen=PROGRAMADO`) |

Un **403 sí lleva el `sub` real**: `IdentidadTrazaFilter` se registra dentro de la cadena de Spring Security,
después de `BearerTokenAuthenticationFilter` y antes de `AuthorizationFilter`.

### Qué NO se registra

Nunca entran al MDC la cabecera `Authorization`, tokens de acceso o refresco, el `jti`, cookies, cuerpos de
petición o respuesta, ni el **query string** — `rutaUri` es siempre el path (`getRequestURI()`).
La IP del cliente es dato personal bajo GDPR / Ley 1581 y se registra bajo interés legítimo (seguridad y abuso
de la API); `arquisoft.trazas.anonimizar-ip=true` la reduce a la subred (último octeto IPv4, /48 en IPv6).

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
# Toda la transacción, extremo a extremo: incluye los saltos por AMQP, que conservan
# la correlación del request HTTP que los originó (gracias a MdcTaskDecorator, que
# propaga el MDC al hilo @Async que publica el evento — ver ARQUITECTURA_ASINCRONICO_ARQUISOFT.md).
{container="arquisoft-backend"} | json | correlacionId="<id>"

# Un solo salto dentro de esa transacción (un request, o un consumo de evento)
{container="arquisoft-backend"} | json | transaccionId="<id>"

# Qué salto originó este: reconstruye el orden exacto de la cadena, no solo que
# comparten correlacionId. Tomar el transaccionId de un salto y buscarlo como padre:
{container="arquisoft-backend"} | json | transaccionPadreId="<transaccionId del salto anterior>"

# Todo lo que hizo un usuario
{container="arquisoft-backend", origin="local-tuNombre"} | json | usuarioId="<uuid>"

# Auditoría de accesos denegados — antes estos requests no dejaban rastro alguno
{container="arquisoft-backend"} | json | message="AUDIT" | codigoEstado=~"401|403|429"

# Peticiones lentas
{container="arquisoft-backend"} | json | message="AUDIT" | duracionMs > 1000

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
