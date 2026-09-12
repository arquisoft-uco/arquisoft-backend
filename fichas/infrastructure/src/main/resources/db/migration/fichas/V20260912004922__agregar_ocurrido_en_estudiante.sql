-- HU-247 — La replica de estudiante versiona por el instante del hecho en el origen.
-- DEFAULT '-infinity': las filas cargadas antes de esta HU son mas viejas que cualquier evento,
-- de modo que el primer evento que llegue para ellas siempre gana.

ALTER TABLE estudiante
    ADD COLUMN ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT '-infinity';
