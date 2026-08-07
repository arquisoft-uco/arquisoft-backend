# Deuda Técnica — Outbox Pattern para publicación de eventos

> **Estado: RESUELTA** — Implementado con Spring Modulith 2.0.0 en la rama
> `feature/spring-modulith-outbox-pattern`. Ver sección [Solución implementada](#solución-implementada).

**Contexto afectado:** `seguridad`  
**Clase afectada:** `CrearUsuarioUseCase`  
**Prioridad:** ~~Media~~ → Resuelta

---

## Problema original

En `CrearUsuarioUseCase`, la persistencia del aggregate y la publicación del evento eran dos operaciones **no atómicas**:

```java
usuarioOutputPort.save(usuario);
usuario.extraerEventosSinPublicar().forEach(eventPublisher::publish);
```

Si el paso (1) se completaba y el paso (2) fallaba (broker caído, timeout, etc.), el sistema quedaba inconsistente:

- El usuario **existía** en la base de datos de `seguridad`.
- El contexto `fichas` **nunca recibía** el evento `seguridad.usuario.creado`.
- El usuario no tenía ficha perfil asociada.

---

## Solución implementada

Se integró **Spring Modulith 2.0.0** con su Event Publication Registry (Outbox Pattern nativo).

### Flujo actual

```
[CrearUsuarioUseCase] — @Transactional
        │
        ├── BEGIN TRANSACTION
        │     ├── INSERT usuarios (aggregate)
        │     └── INSERT event_publication (outbox — misma TX)
        └── COMMIT
                │
        [Spring Modulith — post-commit]
                │
                ├── Publica a RabbitMQ vía AMQP externalization
                │     ├── OK  → DELETE event_publication (completion-mode: delete)
                │     └── FAIL → status = FAILED, row permanece en BD
                │
        [FailedEventRetryConfig — @Scheduled cada 5 min]
                │
                └── Llama FailedEventPublications.resubmit()
                      └── Reintenta eventos FAILED con minAge = 2m
```

### Componentes clave

| Componente | Ubicación | Responsabilidad |
|---|---|---|
| `SpringModulithEventPublisher` | `shared/amqp` | Delega a `ApplicationEventPublisher` — Spring Modulith intercepta y persiste |
| `ModulithAmqpExternalizationConfig` | `shared/amqp` | Routing: `DomainEvent` → exchange `arquisoft.events` con routing key del `temaEvento` |
| `ArquisoftEventsDataSourceConfig` | `src/main/config` | DataSource `@Primary` para la BD centralizada `arquisoft_events` |
| `FailedEventRetryConfig` | `src/main/config` | Reintenta eventos `FAILED` periódicamente sin reinicio |
| `arquisoft_events` DB | PostgreSQL | BD centralizada que contiene la tabla `event_publication` |

### Tabla `event_publication` (schema v2)

```sql
CREATE TABLE event_publication (
    id                    UUID         PRIMARY KEY,
    listener_id           TEXT         NOT NULL,
    event_type            TEXT         NOT NULL,
    serialized_event      TEXT         NOT NULL,
    publication_date      TIMESTAMPTZ  NOT NULL,
    completion_date       TIMESTAMPTZ,
    status                TEXT,        -- NULL | PROCESSING | RESUBMITTED | FAILED | COMPLETED
    completion_attempts   INT,
    last_resubmission_date TIMESTAMPTZ
);
```

### Configuración en `application.yml`

```yaml
spring:
  modulith:
    events:
      completion-mode: delete
      republish-outstanding-events-on-restart: true
      staleness:
        check-intervall: 1m
        processing: 2m
        resubmission: 10m

arquisoft:
  events:
    failed-retry-interval: PT5M
```

### Ciclo de vida de un evento

| Estado `status` | Descripción | Quién lo maneja |
|---|---|---|
| `NULL` | Recién persistido, pendiente de publicar | Spring Modulith post-commit |
| `PROCESSING` | En proceso de publicación | Staleness checker → FAILED si >2m |
| `RESUBMITTED` | En reintento | Staleness checker → FAILED si >10m |
| `FAILED` | Publicación fallida explícitamente | `FailedEventRetryConfig` → reintento cada 5m |
| `COMPLETED` | Publicado correctamente | Borrado (completion-mode: delete) |

### Importante: qué hace el staleness checker

El staleness checker **NO reintenta** eventos. Solo **marca como `FAILED`** los eventos
que llevan demasiado tiempo en `PROCESSING` o `RESUBMITTED` (e.g., la app crasheó
durante la publicación). El reintento real de eventos `FAILED` lo ejecuta `FailedEventRetryConfig`.

---

## Referencias

- [Pattern: Transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html)
- [Spring Modulith — Event Publications](https://docs.spring.io/spring-modulith/docs/current/reference/html/#events)
- `docs/ARQUITECTURA_ASINCRONICO_ARQUISOFT.md` — arquitectura de eventos del proyecto
