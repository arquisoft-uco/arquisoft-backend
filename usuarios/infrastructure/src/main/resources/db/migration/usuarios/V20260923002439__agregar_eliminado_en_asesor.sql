-- HU-235 — Baja lógica del rol asesor. NULL = vigente; las filas previas quedan vigentes.
ALTER TABLE asesor
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
