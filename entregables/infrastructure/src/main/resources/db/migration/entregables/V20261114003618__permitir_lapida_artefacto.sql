-- HT-023-AJ2 — Lápida en la réplica de artefacto.
-- Una fila sin versión (las ocho columnas de versión en NULL) es una lápida: la omisión o la reactivación del artefacto
-- llegó antes de su primer cierre de revisión, y el cierre posterior la completa sin tocar la omisión.
-- El índice deja de ser parcial: la vista previa y el armado filtran por version_artefacto, no por omitido_en.
ALTER TABLE artefacto ALTER COLUMN id_version_artefacto DROP NOT NULL;
ALTER TABLE artefacto ALTER COLUMN version_artefacto DROP NOT NULL;
ALTER TABLE artefacto ALTER COLUMN fecha_version DROP NOT NULL;
ALTER TABLE artefacto ALTER COLUMN clave_objeto DROP NOT NULL;
ALTER TABLE artefacto ALTER COLUMN id_repositorio_artefacto DROP NOT NULL;
ALTER TABLE artefacto ALTER COLUMN nombre_documento DROP NOT NULL;
ALTER TABLE artefacto ALTER COLUMN id_repositorio DROP NOT NULL;
ALTER TABLE artefacto ALTER COLUMN nombre_repositorio DROP NOT NULL;

ALTER TABLE artefacto ADD CONSTRAINT ck_artefacto_lapida CHECK (
    (id_version_artefacto IS NULL AND version_artefacto IS NULL AND fecha_version IS NULL
        AND clave_objeto IS NULL AND id_repositorio_artefacto IS NULL AND nombre_documento IS NULL
        AND id_repositorio IS NULL AND nombre_repositorio IS NULL)
    OR (id_version_artefacto IS NOT NULL AND version_artefacto IS NOT NULL AND fecha_version IS NOT NULL
        AND clave_objeto IS NOT NULL AND id_repositorio_artefacto IS NOT NULL AND nombre_documento IS NOT NULL
        AND id_repositorio IS NOT NULL AND nombre_repositorio IS NOT NULL));

DROP INDEX idx_artefacto_proyecto;
CREATE INDEX idx_artefacto_proyecto ON artefacto (proyecto_grado_id);
