ALTER TABLE estado_evaluacion
    ADD COLUMN orden INTEGER;

UPDATE estado_evaluacion SET orden = 1 WHERE id = 'PENDIENTE';
UPDATE estado_evaluacion SET orden = 2 WHERE id = 'EN_PROGRESO';
UPDATE estado_evaluacion SET orden = 3 WHERE id = 'FINALIZADA';

ALTER TABLE estado_evaluacion
    ALTER COLUMN orden SET NOT NULL,
    ADD CONSTRAINT uk_estado_evaluacion_orden UNIQUE (orden);
