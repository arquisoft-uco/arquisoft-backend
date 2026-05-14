# Deuda Técnica — Outbox Pattern para publicación de eventos

**Contexto afectado:** `seguridad`  
**Clase afectada:** `CrearUsuarioUseCaseImpl`  
**Prioridad:** Media — el riesgo es bajo con repositorio en memoria, pero se vuelve crítico al migrar a JPA.

---

## Problema actual

En `CrearUsuarioUseCaseImpl`, la persistencia del aggregate y la publicación del evento son dos operaciones **no atómicas**:

```java
usuarioRepository.save(usuario);                              // (1) transacción JDBC
usuario.getUnPublishedEvents().forEach(eventPublisher::publish); // (2) envío a RabbitMQ
```

Si el paso (1) se completa y el paso (2) falla después de agotar los 3 reintentos de `RabbitMQEventPublisher` (broker caído, timeout, etc.), el sistema queda en estado inconsistente:

- El usuario **existe** en la base de datos de `seguridad`.
- El contexto `fichas` **nunca recibió** el evento `seguridad.usuario.creado`.
- El usuario no tiene ficha perfil asociada → falla en consultas posteriores.

---

## Por qué ocurre

RabbitMQ y PostgreSQL son sistemas independientes. No existe un coordinador de transacciones distribuidas (2PC) entre ellos en esta arquitectura. Cualquier intento de publicar al broker _dentro_ de una transacción JDBC no garantiza atomicidad entre ambos.

---

## Solución: Outbox Pattern

El **Outbox Pattern** resuelve esto guardando el evento como una fila en la misma base de datos, dentro de la misma transacción JDBC que el `save` del aggregate. Un proceso separado (scheduler o CDC) lee la tabla outbox y publica los eventos pendientes al broker.

### Flujo

```
[CrearUsuarioUseCaseImpl]
        │
        ├── BEGIN TRANSACTION
        │     ├── INSERT usuarios (aggregate)
        │     └── INSERT domain_events (outbox)
        └── COMMIT
                │
        [OutboxPublisher — scheduler @Scheduled o Debezium CDC]
                │
                ├── SELECT * FROM domain_events WHERE published = false
                ├── RabbitTemplate.convertAndSend(...)
                └── UPDATE domain_events SET published = true
```

### Tabla outbox propuesta

```sql
-- Schema: usuarios (contexto seguridad)
CREATE TABLE domain_events (
    event_id       UUID        PRIMARY KEY,
    aggregate_id   UUID        NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    event_topic    VARCHAR(200) NOT NULL,     -- routing key de RabbitMQ
    payload        JSONB       NOT NULL,
    occurred_at    TIMESTAMPTZ NOT NULL,
    published      BOOLEAN     NOT NULL DEFAULT FALSE,
    published_at   TIMESTAMPTZ
);

CREATE INDEX idx_domain_events_unpublished ON domain_events (published, occurred_at)
    WHERE published = FALSE;
```

### Cambios en código

| Archivo | Cambio |
|---|---|
| `UsuarioRepositoryPort` | Añadir método `saveWithEvent(Usuario, DomainEvent)` o `saveAll(Usuario, List<DomainEvent>)` |
| `UsuarioJpaRepository` (JPA) | Persistir usuario + insertar en `domain_events` en misma `@Transactional` |
| `CrearUsuarioUseCaseImpl` | Reemplazar `eventPublisher.publish()` inline por delegación al repositorio; eliminar drenado manual |
| `OutboxPublisherJob` (nuevo) | `@Scheduled` que lee `domain_events WHERE published = false`, publica y marca como publicado |
| Migración Flyway | `V{n}__create_domain_events_table.sql` en `seguridad/infrastructure` |

---

## Estado actual (temporal)

`InMemoryUsuarioRepository` no tiene transacción real — el riesgo de inconsistencia es teórico mientras no haya JPA. Los 3 reintentos con backoff exponencial de `RabbitMQEventPublisher` mitigan el problema en escenarios de caída momentánea del broker.

**Este pendiente debe implementarse antes de pasar a producción**, cuando `InMemoryUsuarioRepository` sea reemplazado por el adaptador JPA.

---

## Referencias

- [Pattern: Transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html) — microservices.io
- [Debezium CDC](https://debezium.io/documentation/reference/stable/connectors/postgresql.html) — alternativa a scheduler para captura de cambios a nivel de WAL de PostgreSQL
- `docs/ARQUITECTURA_ASINCRONICO_ARQUISOFT.md` — arquitectura de eventos del proyecto
