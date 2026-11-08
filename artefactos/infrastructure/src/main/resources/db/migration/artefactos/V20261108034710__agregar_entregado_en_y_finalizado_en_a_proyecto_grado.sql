-- HT-021: columnas LOCALES de artefactos sobre la tabla réplica proyecto_grado (dueño: contexto proyectos).
-- El dueño no las envía: entregado_en la llena el consumo de entregables.entregable_proyecto_grado.generado
-- y finalizado_en el de proyectos.proyecto_grado.finalizado. NULL = no ocurrido; sin backfill (no hay datos
-- en producción; las filas de desarrollo quedan editables).
ALTER TABLE proyecto_grado ADD COLUMN entregado_en TIMESTAMPTZ NULL;
ALTER TABLE proyecto_grado ADD COLUMN finalizado_en TIMESTAMPTZ NULL;
