-- REF-BIB-01: el bucket de los entregables sale de la propiedad biblioteca.almacenamiento.bucket-entregables y el estado
-- se guarda por su id. estado_entregable_id nace nullable para poder convertir las filas existentes: el UPDATE deriva el id
-- del enum a partir del nombre guardado y solo despues se vuelve NOT NULL.
ALTER TABLE entregable_proyecto_grado DROP COLUMN bucket;
ALTER TABLE entregable_proyecto_grado ADD COLUMN estado_entregable_id VARCHAR(60);
UPDATE entregable_proyecto_grado SET estado_entregable_id = UPPER(REPLACE(TRIM(estado_entregable_nombre), ' ', '_'));
ALTER TABLE entregable_proyecto_grado ALTER COLUMN estado_entregable_id SET NOT NULL;
ALTER TABLE entregable_proyecto_grado DROP COLUMN estado_entregable_nombre;
