-- Tabla réplica local de estudiante_proyecto_grado (dueño: contexto proyectos); ocurrido_en y destituido_en son metadatos de replicación. Sin consumidor todavía: se siembra a mano hasta el ajuste de la réplica.
CREATE TABLE estudiante_proyecto_grado (
    id                UUID        PRIMARY KEY,
    estudiante_id     UUID        NOT NULL,
    proyecto_grado_id UUID        NOT NULL,
    ocurrido_en       TIMESTAMPTZ NOT NULL,
    destituido_en     TIMESTAMPTZ NULL,
    CONSTRAINT uk_epg_unica UNIQUE (estudiante_id, proyecto_grado_id)
);

CREATE INDEX idx_epg_proyecto ON estudiante_proyecto_grado (proyecto_grado_id);
