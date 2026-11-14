-- Tabla réplica local de entregable_proyecto_grado (dueño: contexto entregables)
-- HU-152 — Permite verificar sin consulta síncrona que el último entregable del proyecto esté cerrado o cancelado.
-- Sin ON DELETE CASCADE ni catálogo de estado: las réplicas no se borran y esta base no tiene estado_entregable.

CREATE TABLE entregable_proyecto_grado (
    id                   UUID        PRIMARY KEY,
    proyecto_grado_id    UUID        NOT NULL,
    version_entregable   INTEGER     NOT NULL,
    estado_entregable_id VARCHAR(60) NOT NULL,
    ocurrido_en          TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_ent_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id),
    CONSTRAINT uk_ent_proyecto_version UNIQUE (proyecto_grado_id, version_entregable)
);
