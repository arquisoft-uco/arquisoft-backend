-- Tabla réplica local de jurado (dueño: contexto usuarios). NULL en eliminado_en = vigente.
ALTER TABLE jurado
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;

CREATE INDEX idx_jurado_vigente ON jurado(id) WHERE eliminado_en IS NULL;
