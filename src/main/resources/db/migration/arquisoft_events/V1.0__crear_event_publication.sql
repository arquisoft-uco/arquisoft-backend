-- ==============================================================================
-- Base de datos: arquisoft_events
-- Spring Modulith Event Publication Registry — Outbox Pattern centralizado
-- ==============================================================================
-- Esta tabla almacena todos los eventos de dominio de todos los bounded contexts
-- antes de ser publicados en RabbitMQ. Garantiza que ningún evento se pierda:
-- si RabbitMQ estaba caído al momento del commit, el evento queda aquí con
-- completion_date = NULL y el scheduler de Spring Modulith lo reintenta.
-- ==============================================================================

CREATE TABLE event_publication (
    id               UUID         NOT NULL,
    listener_id      TEXT         NOT NULL,
    event_type       TEXT         NOT NULL,
    serialized_event TEXT         NOT NULL,
    publication_date TIMESTAMPTZ  NOT NULL,
    completion_date  TIMESTAMPTZ,
    PRIMARY KEY (id)
);

-- Índice parcial: acelera la consulta de eventos pendientes de publicar.
-- Spring Modulith consulta periódicamente los registros donde completion_date IS NULL.
CREATE INDEX idx_event_publication_pending
    ON event_publication (publication_date)
    WHERE completion_date IS NULL;
