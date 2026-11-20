-- HU-148-AJ5: nadie lee asesor_id en biblioteca y el evento entregables.entregable_proyecto_grado.aprobado no lo trae.
-- bucket, clave_objeto y nombre_archivo siguen NULL en las filas que ya existen: pueden ser manuales y no tener ubicación.
ALTER TABLE entregable_proyecto_grado DROP COLUMN asesor_id;
