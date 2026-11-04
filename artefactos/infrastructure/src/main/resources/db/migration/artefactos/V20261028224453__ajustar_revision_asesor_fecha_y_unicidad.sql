-- fecha_creacion no esta en el MER (mer/04_tablas_artefactos.sql): ordena las revisiones de una misma
-- version, que ahora puede tener varias porque el asesor reabre tras CERRADA. Las filas previas
-- reciben la fecha de la migracion: no hay otra fuente para su fecha real.
ALTER TABLE revision_asesor ADD COLUMN fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT now();
ALTER TABLE revision_asesor ALTER COLUMN fecha_creacion DROP DEFAULT;

-- POL-04 redefinida: una sola revision no cerrada por version (antes: una sola revision por version).
ALTER TABLE revision_asesor DROP CONSTRAINT uk_ra_version_artefacto;
CREATE UNIQUE INDEX uk_ra_version_artefacto_abierta
    ON revision_asesor (version_artefacto_id)
    WHERE estado_revision_asesor_id <> 'CERRADA';
