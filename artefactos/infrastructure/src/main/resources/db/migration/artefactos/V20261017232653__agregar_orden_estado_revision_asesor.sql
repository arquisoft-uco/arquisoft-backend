-- La columna orden no esta en el MER (mer/04_tablas_artefactos.sql): fija el orden del ciclo de vida
-- de la revision, porque ni el id ni el nombre lo reflejan alfabeticamente.
ALTER TABLE estado_revision_asesor ADD COLUMN orden INTEGER;

UPDATE estado_revision_asesor SET orden = 1 WHERE id = 'PENDIENTE';
UPDATE estado_revision_asesor SET orden = 2 WHERE id = 'EN_PROGRESO';
UPDATE estado_revision_asesor SET orden = 3 WHERE id = 'RESUELTA';
UPDATE estado_revision_asesor SET orden = 4 WHERE id = 'CERRADA';

ALTER TABLE estado_revision_asesor ALTER COLUMN orden SET NOT NULL;
ALTER TABLE estado_revision_asesor ADD CONSTRAINT uk_estado_revision_asesor_orden UNIQUE (orden);
