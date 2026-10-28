-- HU-147 — Entregable de un proyecto de grado (MER 08_tablas_entregables.sql).
-- Sin ON DELETE CASCADE sobre fk_ent_proyecto: las réplicas no se borran nunca (baja lógica)
-- y no debe haber borrado en cascada entre contextos.
CREATE TABLE entregable_proyecto_grado (
    id                   UUID        PRIMARY KEY,
    proyecto_grado_id    UUID        NOT NULL,
    version_entregable   INTEGER     NOT NULL,
    asesor_id            UUID        NOT NULL,
    estado_entregable_id VARCHAR(60) NOT NULL,
    CONSTRAINT fk_ent_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id),
    CONSTRAINT fk_ent_asesor   FOREIGN KEY (asesor_id)         REFERENCES asesor(id),
    CONSTRAINT fk_ent_estado   FOREIGN KEY (estado_entregable_id) REFERENCES estado_entregable(id),
    CONSTRAINT uk_ent_proyecto_version UNIQUE (proyecto_grado_id, version_entregable)
);
CREATE INDEX idx_entregable_proyecto ON entregable_proyecto_grado (proyecto_grado_id);
CREATE INDEX idx_entregable_asesor   ON entregable_proyecto_grado (asesor_id);
