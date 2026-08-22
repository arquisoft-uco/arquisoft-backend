> [!NOTE]
> Documentación de referencia humana. Los agentes cargan `.claude/skills/arquisoft-arquitectura.md`
> y `.claude/skills/arquisoft-estandares.md` como contexto conciso, y vienen aquí solo para el
> detalle extendido.

# Arquitectura Asincrónica de Arquisoft — Eventos de Dominio + Outbox

## Índice

1. [Visión General](#visión-general)
2. [Qué contextos publican y consumen eventos hoy](#qué-contextos-publican-y-consumen-eventos-hoy)
3. [Outbox Pattern — Atomicidad entre BD y broker](#outbox-pattern--atomicidad-entre-bd-y-broker)
4. [Configuración de RabbitMQ](#configuración-de-rabbitmq)
5. [Ejemplo real end-to-end: `AsesorFichaCambiadoEvent`](#ejemplo-real-end-to-end-asesorfichacambiadoevent)
6. [Convención de colas y routing keys](#convención-de-colas-y-routing-keys)
7. [Confiabilidad: reintentos, DLQ, trazabilidad](#confiabilidad-reintentos-dlq-trazabilidad)
8. [Stack tecnológico](#stack-tecnológico)
9. [Referencias](#referencias)

---

## Visión General

Arquisoft es un **monolito modular** con **9 bounded contexts** (`seguridad`, `usuarios`,
`fichas`, `notificaciones`, `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`,
`evaluaciones`), cada uno con su propia base de datos y sin dependencias directas entre sí en
tiempo de compilación. **Solo `seguridad`, `usuarios`, `fichas` y `notificaciones` tienen
implementación real hoy**; el resto es scaffolding (solo `{Contexto}DataSourceConfig`, sin
dominio ni casos de uso todavía) — por eso este documento no describe flujos de eventos para
esos cinco contextos: no existen todavía.

Donde dos contextos con implementación real necesitan comunicarse, lo hacen exclusivamente
**publicando y consumiendo eventos de dominio via RabbitMQ** — nunca con una llamada directa
entre módulos ni compartiendo su base de datos. El publicador no sabe quién consume el evento
ni cuántos consumidores hay.

```
UseCase (transacción)
    │
    ├── persiste el aggregate en la BD del contexto
    └── EventPublisher.publish(evento)
            │
            ▼
    Spring Modulith intercepta y persiste el evento
    en event_publication — MISMA transacción, MISMA BD
            │
        (COMMIT)
            │
            ▼
    Tras el commit, Spring Modulith publica a RabbitMQ
    (routing key = temaEvento del evento)
            │
            ▼
    Exchange "arquisoft.events" (Topic, durable)
            │
    ┌───────┴───────┐
    ▼               ▼
Cola contexto A   Cola contexto B
(un @RabbitListener por contexto consumidor)
```

---

## Qué contextos publican y consumen eventos hoy

| Evento | `temaEvento` (routing key) | Publica | Consume | Efecto |
|---|---|---|---|---|
| `UsuarioCreadoEvent` | `usuarios.usuario.creado` | `usuarios` | `fichas` (`UsuarioCreadoConsumer`) | `fichas` necesita saber que un usuario existe para ciertos flujos propios |
| `AsesorFichaCambiadoEvent` | `fichas.ficha_perfil.asesor_cambiado` | `fichas` | `notificaciones` (`AsesorFichaCambiadoConsumer`) | Envía el correo de notificación al nuevo asesor |

Estos son, hoy, los **únicos dos flujos de eventos reales** del sistema. No existe un exchange
o cola por cada combinación teórica de contextos — solo se crea la cola que un consumidor real
necesita, cuando existe ese consumidor.

Cada evento de dominio extiende `DomainEvent` (`shared:domain`, paquete
`com.arquisoft.shared.events`) y declara sus propios campos tipados — `DomainEvent` no tiene
`aggregateId` genérico:

```java
// fichas/domain/.../fichaperfil/event/AsesorFichaCambiadoEvent.java
public class AsesorFichaCambiadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "fichas.ficha_perfil.asesor_cambiado";
    public static final String EVENT_TYPE  = "AsesorFichaCambiadoEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final UUID asesorFichaId;
    private final String asesorNombre;
    private final String asesorEmail;

    public AsesorFichaCambiadoEvent(UUID fichaPerfilId, String tituloProyecto,
            UUID asesorFichaId, String asesorNombre, String asesorEmail) {
        super(EVENT_TOPIC, EVENT_TYPE);   // idEvento, ocurridoEn se generan automáticamente
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.asesorFichaId = asesorFichaId;
        this.asesorNombre = asesorNombre;
        this.asesorEmail = asesorEmail;
    }
    // getters...
}
```

---

## Outbox Pattern — Atomicidad entre BD y broker

Sin el Outbox Pattern, `save(aggregate)` y `eventPublisher.publish(event)` son dos operaciones
independientes: si el broker cae justo después del `save`, el evento se pierde y el sistema
queda inconsistente sin que nadie se entere.

### Solución: Spring Modulith 2.0.0 Event Publication Registry

```java
// {Accion}UseCaseImpl — sin @Transactional propia; corre dentro de la
// transacción abierta por el Interactor
XxxDomain xxx = XxxDomain.crear(...);        // 1. crea + acumula el evento en memoria
xxxOutputPort.save(xxx);                     // 2. persiste el aggregate
xxx.extraerEventosSinPublicar()
   .forEach(eventPublisher::publish);        // 3. drena y publica
```

`eventPublisher` es `SpringModulithEventPublisher` (`shared:amqp`), que delega en
`ApplicationEventPublisher`. Spring Modulith intercepta ese `publish` y, dentro de la misma
transacción que el `save` del paso 2, inserta una fila en la tabla `event_publication` — **de
la base de datos del propio contexto**. Tras el commit, publica el evento a RabbitMQ con
`temaEvento` como routing key.

### `event_publication` es por contexto, no centralizada

**No existe una base de datos `arquisoft_events` compartida.** Cada contexto que publica
eventos tiene su propia tabla `event_publication` en su propia BD — hoy, `fichas` y `usuarios`:

```sql
-- fichas/infrastructure/src/main/resources/db/migration/fichas/V1.9__crear_event_publication.sql
-- usuarios/infrastructure/src/main/resources/db/migration/usuarios/V1.1__crear_event_publication.sql
CREATE TABLE event_publication (
    id UUID NOT NULL, listener_id TEXT NOT NULL, event_type TEXT NOT NULL,
    serialized_event TEXT NOT NULL, publication_date TIMESTAMPTZ NOT NULL,
    completion_date TIMESTAMPTZ, status TEXT, completion_attempts INT,
    last_resubmission_date TIMESTAMPTZ, PRIMARY KEY (id)
);
```

`ContextAwareEventPublicationRepository` (`src/main/java/com/arquisoft/config/outbox/`)
auto-detecta al arranque qué `DataSource` tiene la tabla y enruta el `INSERT` a la transacción
activa de ese contexto — habilitar el outbox en un contexto nuevo es solo agregar esta
migración, sin tocar código Java.

### Reintentos

| Mecanismo | Cuándo actúa | Qué hace |
|---|---|---|
| `republish-outstanding-events-on-restart` | Al arrancar la app | Republica todo lo que no tenga `completion_date` |
| Staleness checker | Periódico | Marca como `FAILED` eventos atascados sin completar |
| `FailedEventRetryConfig` (`src/main/config`) | Cada 5 minutos | Llama `FailedEventPublications.resubmit()` — reintenta lo marcado `FAILED` |

El staleness checker **no reintenta**, solo marca. El reintento real lo hace
`FailedEventRetryConfig`.

---

## Configuración de RabbitMQ

Un único exchange y una única cola de dead-letter, compartidos por todo el proyecto
(`shared:amqp/RabbitMQConfig.java`):

```java
public static final String EXCHANGE_NAME = "arquisoft.events";  // Topic, durable
public static final String DLX_NAME = "arquisoft.dlx";          // Direct, durable
```

El `RabbitTemplate` (mismo archivo) resuelve dos problemas de confiabilidad que un
`convertAndSend` desnudo no cubre:

- **Publisher Returns** (`setMandatory(true)` + `setReturnsCallback`): si el broker no puede
  enrutar el mensaje a ninguna cola, se lo devuelve al publicador en vez de descartarlo en
  silencio.
- **Publisher Confirms** (`setConfirmCallback`): el broker confirma o rechaza cada publicación;
  un `NACK` se registra con el `correlationId` del evento (`evento.getIdEvento()`).

Un `MessagePostProcessor` inyecta los headers `X-Trace-Id`/`X-Transaction-Id`/`X-User-Id` (`TrazaHeaders`, tomados
del MDC) en cada mensaje publicado, incluidos los reintentos del outbox — así el consumidor
puede reconstruir la traza aunque el evento se haya reintentado horas después.

El `JsonMapper` de RabbitMQ (`rabbitObjectMapper`) desactiva
`FAIL_ON_UNKNOWN_PROPERTIES` — **Tolerant Reader**: un consumidor no se rompe si el productor
agrega un campo nuevo al evento.

---

## Ejemplo real end-to-end: `AsesorFichaCambiadoEvent`

### Productor — `fichas`

El `CambiarAsesorFichaUseCaseImpl` construye y publica el evento tras persistir el cambio (ver
CLAUDE.md → "Domain events" para el patrón completo de drenado/publicación).

### Consumidor — `notificaciones`

```java
// notificaciones/infrastructure/.../notificacion/command/primaryadapter/amqp/AsesorFichaCambiadoConsumer.java
@Component
public class AsesorFichaCambiadoConsumer extends AbstractEventConsumer {

    private final EnviarNotificacionInteractor enviarNotificacionInteractor;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    public AsesorFichaCambiadoConsumer(
            EnviarNotificacionInteractor enviarNotificacionInteractor,
            @Qualifier("rabbitObjectMapper") ObjectMapper objectMapper,
            AppLogger logger, CatalogoMensajes catalogo) {
        super(objectMapper);
        this.enviarNotificacionInteractor = enviarNotificacionInteractor;
        this.logger = logger;
        this.catalogo = catalogo;
    }

    @RabbitListener(queues = NotificacionesFichasQueueConfig.ASESOR_CAMBIADO_QUEUE)
    public void onAsesorFichaCambiado(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            var payload = deserialize(message, AsesorFichaCambiadoPayload.class);
            enviarNotificacionInteractor.ejecutar(new EnviarNotificacionCommand(
                    payload.idEvento(),
                    TipoNotificacion.ASESOR_FICHA_CAMBIADO,
                    payload.asesorNombre(),
                    payload.asesorEmail(),
                    catalogo.formatear(PlantillaKey.ASUNTO_ASESOR_CAMBIADO, payload.tituloProyecto()),
                    catalogo.formatear(PlantillaKey.CUERPO_ASESOR_CAMBIADO,
                            payload.asesorNombre(), payload.tituloProyecto())));
        });
    }
}
```

`AbstractEventConsumer` (`shared:amqp/consumer/`) centraliza lo que todo consumidor necesita:
deserializar el body (`deserialize`), propagar la correlación al MDC (`X-Trace-Id`/`X-User-Id`
del mensaje), y el ack/nack manual — si `handler.handle()` lanza, hace
`channel.basicNack(deliveryTag, false, false)` (sin *requeue*: el mensaje va directo al DLX en
vez de reintentarse infinitamente ante un error de negocio no recuperable); si no lanza, hace
`channel.basicAck`.

La traducción de "qué dice el correo" vive en el consumer, no en el caso de uso de
`notificaciones` — agregar un evento nuevo es agregar otro consumer con su propia plantilla,
sin tocar dominio ni aplicación.

---

## Convención de colas y routing keys

La cola sigue el patrón `{contextoConsumidor}.{routingKey}`, y la routing key es el
`temaEvento` (`EVENT_TOPIC`) del evento del productor — así el nombre de la cola deja claro,
solo con leerlo, quién la escucha y qué está esperando:

```java
// notificaciones/infrastructure/.../config/NotificacionesFichasQueueConfig.java
public static final String ASESOR_CAMBIADO_QUEUE =
        "notificaciones.fichas.ficha_perfil.asesor_cambiado";
public static final String ASESOR_CAMBIADO_ROUTING_KEY =
        "fichas.ficha_perfil.asesor_cambiado";   // == AsesorFichaCambiadoEvent.EVENT_TOPIC

@Bean
public Queue notificacionesAsesorCambiadoQueue() {
    return QueueBuilder.durable(ASESOR_CAMBIADO_QUEUE)
            .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
            .withArgument("x-dead-letter-routing-key", ASESOR_CAMBIADO_QUEUE + ".dead")
            .build();
}

@Bean
public Binding notificacionesAsesorCambiadoBinding(
        Queue notificacionesAsesorCambiadoQueue,
        @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange) {
    return BindingBuilder.bind(notificacionesAsesorCambiadoQueue)
            .to(arquisoftEventsExchange).with(ASESOR_CAMBIADO_ROUTING_KEY);
}
```

Cada contexto consumidor declara su propia clase `{ContextoConsumidor}{ContextoProductor}QueueConfig`
con una cola por evento que realmente consume — no hay una cola "genérica" ni un binding
comodín (`#`).

---

## Confiabilidad: reintentos, DLQ, trazabilidad

- **Nivel broker (publicación):** Publisher Confirms + Publisher Returns (ver arriba).
- **Nivel aplicación (publicación):** `RabbitMQEventPublisher` reintenta hasta 3 veces con
  backoff exponencial (500ms, 1s, 2s) ante `AmqpException` transitorias; un error no
  transitorio (p. ej. fallo de serialización) no se reintenta, se loguea y se propaga. En la
  práctica, la ruta activa hoy es `SpringModulithEventPublisher` (ver Outbox arriba);
  `RabbitMQEventPublisher` es la implementación de respaldo (`@ConditionalOnMissingBean`).
- **Nivel consumidor:** `AbstractEventConsumer` — `nack` sin *requeue* ante excepción envía el
  mensaje al DLX (`arquisoft.dlx`) en vez de reintroducirlo en la cola original, evitando
  bucles de re-entrega ante errores de negocio no recuperables.
- **Nivel outbox:** ver "Reintentos" en la sección de Outbox Pattern arriba.
- **Trazabilidad:** `X-Trace-Id`/`X-User-Id` viajan en los headers del mensaje AMQP desde la
  publicación (inyectados por `traceHeadersPostProcessor`) hasta el consumo (leídos y puestos
  en el MDC por `AbstractEventConsumer.withCorrelation`), de modo que un log del consumidor se
  puede correlacionar con el request HTTP original que disparó el evento.

`TrazabilidadFilter` (`shared:web/filter/`) registra además cada request HTTP síncrono
(`METHOD`, `URI`, `USER`, `TIME`, `STATUS`) — complementario a la trazabilidad asíncrona, no
parte de ella.

### El salto de hilo que casi rompe la trazabilidad

La publicación real hacia RabbitMQ (arriba, "tras el commit, Spring Modulith publica...") no
ocurre en el hilo del request: internamente `spring-modulith-events-amqp` la resuelve con un
listener `@Async @Transactional(REQUIRES_NEW) @TransactionalEventListener`, ejecutado en el
`applicationTaskExecutor` que autoconfigura Spring Boot. El `MDC` es un `ThreadLocal` — ese
hilo del executor arranca vacío, así que `traceHeadersPostProcessor` (`TrazaMessagePostProcessor`)
no encontraba `correlacionId`/`transaccionId` reales y generaba unos nuevos al momento de
publicar, rompiendo la correlación entre el productor y cualquier consumidor.

`MdcTaskDecorator` (`shared:tracing/infrastructure/traza/config/`) cierra ese hueco: captura el
MDC del hilo que encola la tarea (todavía dentro del `AlcanceTraza` del request) y lo restaura
en el hilo del executor antes de correr la tarea real, revirtiéndolo al terminar para no filtrar
contexto entre tareas no relacionadas que reutilicen el mismo hilo del pool. Se registra como
`@Bean TaskDecorator` en `TrazabilidadConfig`; Spring Boot lo recoge automáticamente para el
`applicationTaskExecutor` sin declarar `@EnableAsync` ni un executor propio.

Es, a propósito, un problema de **hilos dentro del mismo proceso**, no de propagación entre
contextos — la propagación entre contextos siempre viajó en los headers del mensaje AMQP (arriba)
y no depende de memoria compartida. Por eso este decorator seguirá siendo necesario aunque algún
contexto se extraiga a su propio microservicio: cada productor seguirá teniendo su propio salto
de hilo (request → executor async) antes de que el evento llegue al broker.

---

## Stack tecnológico

| Componente | Versión / detalle |
|---|---|
| Message Queue | RabbitMQ 4.2.5 — exchange Topic durable + DLX Direct durable |
| Base de datos | PostgreSQL 18, una por contexto (no un schema compartido) |
| Outbox | Spring Modulith 2.0.0 — tabla `event_publication` por contexto |
| Cache | Redis 7 (Lettuce) |
| Almacenamiento de archivos | MinIO (`shared:minio`) — usado hoy por `fichas` para la guía de elaboración (`MinioGuiaController`) |
| Autenticación | Keycloak 26.6 (OAuth2/OIDC Resource Server) |
| Framework | Spring Boot 4.0.5, Java 21 (Virtual Threads habilitados) |
| Build | Gradle 9.0.0 |
| Testing | JUnit 6.0.3 + Mockito + AssertJ |

---

## Referencias

- **Arquitectura y Estructura**: `docs/ARQUITECTURA_Y_ESTRUCTURA.md`
- **Convenciones completas del proyecto**: `AGENTS.md` (raíz) y `CLAUDE.md` (raíz)
- **Spring Modulith Event Publication Registry**: https://docs.spring.io/spring-modulith/docs/current/reference/html/#events
