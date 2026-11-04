-- Tabla réplica local de asesor_proyecto_grado (dueño: contexto proyectos); ocurrido_en y finalizado_en son metadatos de replicación.
CREATE TABLE asesor_proyecto_grado (
    id                UUID        PRIMARY KEY,
    asesor_id         UUID        NOT NULL,
    proyecto_grado_id UUID        NOT NULL,
    ocurrido_en       TIMESTAMPTZ NOT NULL,
    finalizado_en     TIMESTAMPTZ NULL,
    CONSTRAINT uk_apg_unica UNIQUE (asesor_id, proyecto_grado_id)
);

CREATE INDEX idx_apg_proyecto ON asesor_proyecto_grado (proyecto_grado_id);
