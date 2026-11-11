-- HU-302: columna LOCAL de artefactos sobre la tabla réplica proyecto_grado (dueño: contexto proyectos).
-- El dueño no la envía: la llena el consumo de proyectos.proyecto_grado.cancelado. NULL = no cancelado;
-- sin backfill (los proyectos cancelados antes de esta migración no existían: el estado es nuevo).
ALTER TABLE proyecto_grado ADD COLUMN cancelado_en TIMESTAMPTZ NULL;
