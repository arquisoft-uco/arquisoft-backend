-- Tabla réplica local de estudiante (dueño: contexto usuarios); ocurrido_en es metadato de replicación y no está en el MER.
-- El DEFAULT trata cualquier fila previa como anterior a cualquier evento real.
ALTER TABLE estudiante ADD COLUMN ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01T00:00:00Z';

CREATE INDEX idx_estudiante_vigente ON estudiante (id) WHERE eliminado_en IS NULL;
