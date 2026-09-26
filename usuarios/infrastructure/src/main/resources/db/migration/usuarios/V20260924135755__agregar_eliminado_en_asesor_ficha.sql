-- HU-238 — Baja lógica del rol asesor de ficha. NULL = vigente; las filas previas quedan vigentes.
ALTER TABLE asesor_ficha
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
