-- Tabla réplica local de estudiante_proyecto_grado (dueño: contexto proyectos). Poblada por EstudiantesProyectoGradoAsignadosConsumer.
-- ocurrido_en y eliminado_en son metadatos de replicación (no están en el MER).
CREATE TABLE estudiante_proyecto_grado (
    id                UUID        PRIMARY KEY,
    estudiante_id     UUID        NOT NULL,
    proyecto_grado_id UUID        NOT NULL,
    ocurrido_en       TIMESTAMPTZ NOT NULL,
    eliminado_en      TIMESTAMPTZ NULL,
    CONSTRAINT uk_epg_unica UNIQUE (estudiante_id, proyecto_grado_id)
);

CREATE INDEX idx_epg_proyecto ON estudiante_proyecto_grado (proyecto_grado_id);
CREATE INDEX idx_epg_estudiante_vigente ON estudiante_proyecto_grado (estudiante_id) WHERE eliminado_en IS NULL;
