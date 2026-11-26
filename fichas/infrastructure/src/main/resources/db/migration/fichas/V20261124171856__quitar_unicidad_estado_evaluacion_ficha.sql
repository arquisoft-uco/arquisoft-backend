-- Reactivar y volver a descartar repiten un estado en el historial; que no se repita el estado actual lo decide la regla de transición.
ALTER TABLE estado_evaluacion_ficha DROP CONSTRAINT uk_eef_trazabilidad;
