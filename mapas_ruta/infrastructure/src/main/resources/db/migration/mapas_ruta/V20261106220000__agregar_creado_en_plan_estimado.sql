-- HU-004 — Fecha de registro del plan estimado: base del tiempo real de la tarea (plan_real.tiempo_real_dias).
-- Los planes estimados existentes quedan con la fecha de esta migración como creado_en, así que el tiempo
-- real de sus tareas se mide desde el despliegue y no desde su registro original.
ALTER TABLE plan_estimado ADD COLUMN creado_en TIMESTAMPTZ NOT NULL DEFAULT now();
