-- HU-152 — Estado del proyecto de grado en la réplica local (dueño: contexto proyectos).
-- DEFAULT 'EN_PROCESO': las filas anteriores a esta HU son proyectos replicados antes de que pudiera finalizarse
-- ninguno, así que todos estaban en proceso. Solo ProyectoGradoFinalizadoConsumer la escribe después del alta.

ALTER TABLE proyecto_grado
    ADD COLUMN estado_proyecto_id VARCHAR(60) NOT NULL DEFAULT 'EN_PROCESO';
