-- HU-248 — Baja lógica del rol estudiante. NULL = vigente; las filas previas quedan vigentes.
ALTER TABLE estudiante
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
