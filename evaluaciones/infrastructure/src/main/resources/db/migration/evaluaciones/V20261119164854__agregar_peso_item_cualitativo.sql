-- Peso de cada ítem cualitativo en el resumen de la evaluación (1 a 500).
-- Las filas anteriores pesan 1 hasta que se ajusten con Modificar.
ALTER TABLE item_cualitativo_jurado
    ADD COLUMN peso INTEGER NOT NULL DEFAULT 1,
    ADD CONSTRAINT ck_item_cualitativo_jurado_peso CHECK (peso BETWEEN 1 AND 500);

ALTER TABLE item_cualitativo_asesor
    ADD COLUMN peso INTEGER NOT NULL DEFAULT 1,
    ADD CONSTRAINT ck_item_cualitativo_asesor_peso CHECK (peso BETWEEN 1 AND 500);
