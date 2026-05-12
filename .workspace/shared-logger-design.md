# Diseño: Módulo `shared:logger`

_Última actualización: 2026-05-12_

## Decisión central — ¿Dónde vive el log?

| Capa | ¿Log? | Estrategia |
|---|---|---|
| **Domain** | ❌ No | El dominio emite `DomainEvent`; la lógica de negocio no produce logs. Si hay necesidad de trazar una invariante en desarrollo, lanzar excepción, no loguear. |
| **Application** | ✅ Sí | `@Slf4j` (Lombok). Los use cases son el punto natural para registrar inicio/fin de operaciones de negocio. |
| **Infrastructure** | ✅ Sí | `@Slf4j` (Lombok). HTTP, persistencia, mensajería, filtros. |

## Módulo `shared:logger`

Responsabilidad única: proveer configuración Logback centralizada (JSON estructurado + `AsyncAppender`) y constantes MDC compartidas.  
No expone beans Spring; los contextos solo declaran la dependencia para heredar la configuración.

```
shared/
  logger/
    build.gradle
    src/main/
      java/com/arquisoft/shared/logger/
        MdcKeys.java                  # Constantes de claves MDC (evita magic strings)
      resources/
        logback-spring.xml            # Configuración principal; carga el fragmento por perfil
        logback-json-appender.xml     # Fragmento reutilizable: JSON_FILE + AsyncAppenders
```

### `build.gradle`

```gradle
dependencies {
    // SLF4J + Logback — transitivos de spring-boot-starter-*; declarado para
    // garantizar el classpath del encoder en cualquier contexto que dependa de este módulo.
    implementation "org.springframework.boot:spring-boot-starter-logging"

    // Encoder JSON para Logback — emite NDJSON indexable por Loki/ELK.
    implementation "net.logstash.logback:logstash-logback-encoder:${logstashVersion}"

    // Micrometer Tracing bridge (Brave) — SIN exporter configurado en este proyecto.
    // Spring Boot NO instancia el bean Tracer → MDCScopeDecorator no se activa →
    // MDC.get("traceId") == null → AuditFilter genera UUID de correlación como fallback.
    // Cuando se configure un exporter (Zipkin/OTLP), el fallback se desactiva automáticamente.
    implementation "io.micrometer:micrometer-tracing-bridge-brave"

    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"
}
```

### `MdcKeys.java`

Constantes tipadas para las claves MDC — elimina magic strings dispersos en filtros y use cases.

```java
package com.arquisoft.shared.logger;

public final class MdcKeys {
    public static final String TRACE_ID = "traceId";
    public static final String USER_ID  = "userId";
    // SPAN_ID reservado para cuando se configure exporter Brave/OTel
    // public static final String SPAN_ID = "spanId";
    private MdcKeys() {}
}
```

---

## Correlación de requests — patrón Correlation ID (MVP)

Para el MVP se adopta **Correlation ID** en lugar de trazado distribuido completo (sin spanId):

| Campo MDC | Origen | Descripción |
|---|---|---|
| `traceId` | `AuditFilter` (fallback UUID) | UUID hex sin guiones por request. Cuando se configure un exporter de Micrometer, el bridge Brave/OTel lo inyectará directamente. |
| `userId` | `AuditFilter.extractUserId()` | Claim `sub` del JWT. `"anonymous"` si no hay token. Poblado **antes** de `filterChain.doFilter()`. |

**Flujo en `AuditFilter`:**

```
request → extractUserId() → MDC.put(USER_ID, userId)
        → MDC.get(TRACE_ID) == null?  → MDC.put(TRACE_ID, UUID)
        → filterChain.doFilter()       ← todos los logs del request ya tienen ambos campos
        → finally: MDC.clear()
```

### Propagación a RabbitMQ

Ver [`.workspace/pendiente-amqp-traceid.md`](.workspace/pendiente-amqp-traceid.md) — guía completa para propagar `traceId` via headers AMQP.

---

## Thread safety bajo Virtual Threads (Java 21)

**Riesgo:** Los appenders síncronos de Logback usan `synchronized`. En Virtual Threads, `synchronized` provoca **pinning** (el VT queda atado a su carrier thread mientras espera el lock), degradando el throughput bajo alta concurrencia de logs.

**Solución:** `AsyncAppender` desacopla el hilo que escribe el log del I/O del appender mediante una `BlockingQueue` interna. El hilo productor (Virtual Thread) encola el evento y continúa; un hilo dedicado drena la cola sin pinning.

**MDC:** Usa `ThreadLocal` internamente. En Java 21, cada Virtual Thread tiene su propio `ThreadLocal`, por lo que `traceId` y `userId` quedan correctamente aislados por VT sin configuración adicional.

---

## Configuración Logback — estructura de archivos

La configuración se divide en dos archivos para eliminar duplicación:

| Archivo | Propósito |
|---|---|
| `logback-spring.xml` | Define propiedades por perfil e incluye el fragmento |
| `logback-json-appender.xml` | Fragmento `<included>`: `JSON_FILE` + `ASYNC_CONSOLE` + `ASYNC_FILE` |

### `logback-json-appender.xml` (fragmento `<included>`)

Recibe 4 propiedades del perfil que lo incluye:

```xml
<included>
    <appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/arquisoft.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/archived/arquisoft.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>50MB</maxFileSize>
            <maxHistory>${json.maxHistory}</maxHistory>
            <totalSizeCap>${json.totalSizeCap}</totalSizeCap>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"app":"arquisoft-backend","env":"${activeProfile}"}</customFields>
            <excludedFields>
                <fieldName>@version</fieldName>     <!-- siempre "1", sin valor informativo -->
                <fieldName>level_value</fieldName>  <!-- duplica el campo "level" legible -->
            </excludedFields>
            <includeMdcKeyName>traceId</includeMdcKeyName>
            <includeMdcKeyName>userId</includeMdcKeyName>
        </encoder>
    </appender>

    <appender name="ASYNC_CONSOLE" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>${async.queueSize}</queueSize>
        <discardingThreshold>${async.discardingThreshold}</discardingThreshold>
        <includeCallerData>false</includeCallerData>
        <appender-ref ref="CONSOLE"/>
    </appender>

    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>${async.queueSize}</queueSize>
        <discardingThreshold>${async.discardingThreshold}</discardingThreshold>
        <includeCallerData>false</includeCallerData>
        <appender-ref ref="JSON_FILE"/>
    </appender>
</included>
```

### `logback-spring.xml` (configuración principal)

```xml
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>

    <!-- dev: retención 7d, 500MB, nunca descarta (discardingThreshold=0), root INFO -->
    <springProfile name="dev">
        <property name="json.maxHistory"           value="7"/>
        <property name="json.totalSizeCap"         value="500MB"/>
        <property name="async.queueSize"           value="2048"/>
        <property name="async.discardingThreshold" value="0"/>
        <include resource="logback-json-appender.xml"/>
        <root level="INFO">
            <appender-ref ref="ASYNC_CONSOLE"/>
            <appender-ref ref="ASYNC_FILE"/>
        </root>
    </springProfile>

    <!-- prod: retención 30d, 2GB, descarta INFO/DEBUG bajo presión (threshold=20),
         root WARN — com.arquisoft se eleva a INFO desde application-prod.yml -->
    <springProfile name="prod">
        <property name="json.maxHistory"           value="30"/>
        <property name="json.totalSizeCap"         value="2GB"/>
        <property name="async.queueSize"           value="4096"/>
        <property name="async.discardingThreshold" value="20"/>
        <include resource="logback-json-appender.xml"/>
        <root level="WARN">
            <appender-ref ref="ASYNC_CONSOLE"/>
            <appender-ref ref="ASYNC_FILE"/>
        </root>
    </springProfile>
</configuration>
```

**`discardingThreshold` en prod = 20:** bajo presión de cola (>80% llena), Logback descarta `TRACE`, `DEBUG` e `INFO`. `WARN` y `ERROR` nunca se descartan. En dev = 0: no se descarta nada.

---

## Cómo usar el logger en cada contexto

### 1. Declarar dependencia

```gradle
// {contexto}/infrastructure/build.gradle  (o application si el use case logea)
implementation project(':shared:logger')
```

### 2. Anotar la clase con `@Slf4j`

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class CrearProyectoUseCaseImpl implements CrearProyectoUseCase {

    @Override
    public ProyectoDTO crear(CrearProyectoDTO dto) {
        log.debug("Iniciando creación de proyecto — estudiante={}", dto.getEstudianteId());
        ProyectoDTO resultado = ProyectoDTO.fromDomain(...);
        log.info("Proyecto creado — id={}", resultado.getId());
        return resultado;
    }
}
```

### 3. Niveles recomendados

| Nivel | Cuándo |
|---|---|
| `log.info()` | Fin de operación de negocio relevante, evento de sistema |
| `log.warn()` | Errores 4xx, precondición degradada, rate-limit activado |
| `log.error("msg", cause)` | Errores 5xx — **siempre pasar `Throwable` como último argumento** |
| `log.debug()` | Solo dev — desactivado en prod (`root level="WARN"`) |

### 4. MDC — NO poblar manualmente en application/domain

`AuditFilter` (infrastructure de `seguridad`) ya gestiona `traceId` y `userId` para **todo** request HTTP. En use cases y controllers, **no hacer `MDC.put()`** — los campos ya están disponibles en el contexto del hilo.

Si un use case necesita agregar contexto específico de operación:

```java
// Sí permitido: contexto adicional de negocio dentro de una operación larga
MDC.put("proyectoId", proyectoId.toString());
try {
    // ... lógica
} finally {
    MDC.remove("proyectoId");  // limpiar solo lo que pusiste; no llamar MDC.clear()
}
```

---

## Registro en `settings.gradle`

```gradle
include 'shared:logger'
```

---

## Resumen de decisiones

- **`@Slf4j`** es el único mecanismo de logging — no hay bean `AppLogger`, no hay port de dominio.
- **Dominio libre de logging** — emite `DomainEvent` y lanza excepciones; las capas externas registran el cuándo.
- **Correlation ID** para MVP (`traceId` + `userId`): sin spanId, sin exporter. Upgrade a trazado distribuido = solo configurar exporter Zipkin/OTLP.
- **`MdcKeys`** centraliza las constantes de clave MDC — nunca magic strings en filtros ni use cases.
- **`logback-json-appender.xml`** como fragmento `<included>` elimina la duplicación del encoder entre perfiles.
- **`AsyncAppender`** elimina el riesgo de pinning de Virtual Threads en el I/O del appender.
- **`discardingThreshold=0` en dev**, `=20` en prod: equilibrio entre completitud de logs y throughput.
- **Prod emite JSON** (`LogstashEncoder`) → stdout → Promtail → Loki (ADR-005).
- **Dev también emite JSON** — facilita validar el formato JSON localmente antes de desplegar.
- **Un solo `logback-spring.xml`** en `shared:logger`; todos los contextos heredan la configuración al declarar la dependencia Gradle.
