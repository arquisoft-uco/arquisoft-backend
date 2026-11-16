-- HU-307 — Tabla réplica local de estudiante_proyecto_grado (dueño: contexto proyectos). Poblada por
-- EstudiantesProyectoGradoAsignadosConsumer y EstudianteProyectoGradoDestituidoConsumer.
-- ocurrido_en y eliminado_en son metadatos de replicación (no están en el MER). Sin FK hacia autor ni hacia
-- proyecto_grado: cada tabla la puebla un evento distinto y puede llegar antes que la otra.
-- Los índices usan el prefijo espg: idx_epg_proyecto y uk_epg_proyecto_version ya existen en la base
-- sobre entregable_proyecto_grado.
CREATE TABLE estudiante_proyecto_grado (
    id UUID PRIMARY KEY,
    proyecto_grado_id UUID NOT NULL,
    autor_id UUID NOT NULL,
    ocurrido_en TIMESTAMPTZ NOT NULL,
    eliminado_en TIMESTAMPTZ NULL,
    CONSTRAINT uk_espg_proyecto_autor UNIQUE (proyecto_grado_id, autor_id)
);
CREATE INDEX idx_espg_autor ON estudiante_proyecto_grado (autor_id);
CREATE INDEX idx_espg_vigente ON estudiante_proyecto_grado (proyecto_grado_id) WHERE eliminado_en IS NULL;
