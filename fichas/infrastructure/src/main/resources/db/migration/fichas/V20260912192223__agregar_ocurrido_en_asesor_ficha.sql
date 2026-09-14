-- HU-237 — La replica de asesor_ficha versiona por el instante del hecho en el origen.
-- DEFAULT '-infinity': las filas cargadas antes de esta HU son mas viejas que cualquier evento,
-- de modo que el primer evento que llegue para ellas siempre gana.

ALTER TABLE asesor_ficha
    ADD COLUMN ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT '-infinity';
