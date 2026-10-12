-- HT-014 — Trazabilidad de estados del proyecto de grado, según mer/07_tablas_proyectos_grado.sql.
-- El catálogo pasa a llamarse estado_proyecto y estado_proyecto_grado queda como historial: el estado
-- vigente de un proyecto es su fila más reciente, y proyecto_grado deja de guardar el estado.
-- Sin backfill: el contexto está en desarrollo y no hay proyectos de grado que conservar.

ALTER TABLE estado_proyecto_grado RENAME TO estado_proyecto;
ALTER INDEX estado_proyecto_grado_pkey RENAME TO estado_proyecto_pkey;

CREATE TABLE estado_proyecto_grado (
    id                  UUID         PRIMARY KEY,
    proyecto_grado_id   UUID         NOT NULL,
    estado_proyecto_id  VARCHAR(60)  NOT NULL,
    fecha_actualizacion TIMESTAMPTZ  NOT NULL,
    CONSTRAINT fk_espg_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id) ON DELETE CASCADE,
    CONSTRAINT fk_espg_estado   FOREIGN KEY (estado_proyecto_id) REFERENCES estado_proyecto(id),
    CONSTRAINT uk_espg_unica    UNIQUE (proyecto_grado_id, estado_proyecto_id, fecha_actualizacion)
);

DROP INDEX idx_proyecto_estado;
ALTER TABLE proyecto_grado DROP COLUMN estado_proyecto_grado_id;

CREATE INDEX idx_espg_estado ON estado_proyecto_grado(estado_proyecto_id);
