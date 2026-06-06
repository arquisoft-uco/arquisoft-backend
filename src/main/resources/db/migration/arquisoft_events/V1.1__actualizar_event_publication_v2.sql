-- ==============================================================================
-- Migración: actualizar event_publication al esquema v2 de Spring Modulith 2.0.0
-- ==============================================================================
-- Spring Modulith 2.0.0 usa JdbcEventPublicationRepositoryV2 que requiere tres
-- columnas adicionales respecto al esquema v1 (creado en V1.0):
--   - status                  TEXT
--   - completion_attempts     INT
--   - last_resubmission_date  TIMESTAMPTZ
--
-- También reemplaza el índice parcial de V1.0 por los dos índices canónicos de SM 2.x.
-- Se usan IF NOT EXISTS / IF EXISTS para que esta migración sea idempotente
-- y funcione tanto en entornos con V1.0 aplicado como en instalaciones frescas.
-- ==============================================================================

ALTER TABLE event_publication
    ADD COLUMN IF NOT EXISTS status                  TEXT,
    ADD COLUMN IF NOT EXISTS completion_attempts     INT,
    ADD COLUMN IF NOT EXISTS last_resubmission_date  TIMESTAMP WITH TIME ZONE;

-- Reemplazar el índice parcial de V1.0 por los índices canónicos de Spring Modulith 2.x
DROP INDEX IF EXISTS idx_event_publication_pending;

CREATE INDEX IF NOT EXISTS event_publication_serialized_event_hash_idx
    ON event_publication USING hash(serialized_event);

CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx
    ON event_publication (completion_date);
