-- HU-146 — Vínculo artefacto-entregable (MER 08_tablas_entregables.sql).
-- Sin ON DELETE CASCADE: las tablas cuelgan de réplicas que no se borran (baja lógica) y no debe haber borrado en cascada.
-- Ningún contexto puebla aún esta tabla.
CREATE TABLE artefacto_entregable (
    id            UUID PRIMARY KEY,
    artefacto_id  UUID NOT NULL,
    entregable_id UUID NOT NULL,
    CONSTRAINT fk_ae_artefacto  FOREIGN KEY (artefacto_id)  REFERENCES artefacto(id),
    CONSTRAINT fk_ae_entregable FOREIGN KEY (entregable_id) REFERENCES entregable_proyecto_grado(id),
    CONSTRAINT uk_ae_unica UNIQUE (artefacto_id, entregable_id)
);
CREATE INDEX idx_ae_entregable ON artefacto_entregable (entregable_id);
