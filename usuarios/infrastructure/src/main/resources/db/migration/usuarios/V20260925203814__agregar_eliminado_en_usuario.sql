-- HU-259 — Eliminación lógica del usuario. NULL = vigente; las filas previas quedan vigentes.
ALTER TABLE usuario
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
