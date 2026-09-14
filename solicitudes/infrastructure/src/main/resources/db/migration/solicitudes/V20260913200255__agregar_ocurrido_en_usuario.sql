-- Recomendacion de revision del PR #87 — la replica de usuario versiona por el instante del hecho
-- en el origen (usuarios), igual que asesor_ficha en fichas.
-- DEFAULT '-infinity': las filas cargadas antes de esta migracion son mas viejas que cualquier
-- evento, de modo que el primer evento que llegue para ellas siempre gana.

ALTER TABLE usuario
    ADD COLUMN ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT '-infinity';
