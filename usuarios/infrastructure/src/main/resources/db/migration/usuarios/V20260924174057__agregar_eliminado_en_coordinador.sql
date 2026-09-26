-- HU-244 — Baja lógica del rol coordinador. NULL = vigente; las filas previas quedan vigentes.
ALTER TABLE coordinador
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
