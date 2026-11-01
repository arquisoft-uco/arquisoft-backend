-- Reapertura con continuación (HU-137): la observación creada en la revisión de la versión siguiente
-- referencia a la original REABIERTA. NULL = no es continuación; las filas existentes quedan así.
-- Una original solo tiene una continuación (UNIQUE); al borrar la original la continuación queda suelta.
ALTER TABLE observacion ADD COLUMN observacion_origen_id UUID NULL;
ALTER TABLE observacion ADD CONSTRAINT fk_obs_origen FOREIGN KEY (observacion_origen_id)
    REFERENCES observacion (id) ON DELETE SET NULL;
ALTER TABLE observacion ADD CONSTRAINT uk_obs_origen UNIQUE (observacion_origen_id);
