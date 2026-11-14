-- HU-136: las descripciones del catálogo estado_observacion describen el flujo real y coinciden con el MER
-- (mer/data/04_data_artefactos.sql). CERRADA decía "resuelta por el estudiante", que es SOLUCIONADA.
UPDATE estado_observacion SET descripcion = 'El asesor la registró en su revisión y aún no la ha enviado al estudiante'
 WHERE id = 'NUEVA';
UPDATE estado_observacion SET descripcion = 'El asesor envió la revisión y el estudiante está atendiendo la observación'
 WHERE id = 'EN_PROGRESO';
UPDATE estado_observacion SET descripcion = 'El estudiante la marcó como resuelta y espera que el asesor la verifique'
 WHERE id = 'SOLUCIONADA';
UPDATE estado_observacion SET descripcion = 'El asesor aceptó la solución; es un estado final y ya no admite cambios'
 WHERE id = 'CERRADA';
UPDATE estado_observacion
   SET descripcion = 'El asesor no aceptó la solución; continúa como una observación nueva en la revisión de la siguiente versión'
 WHERE id = 'REABIERTA';
