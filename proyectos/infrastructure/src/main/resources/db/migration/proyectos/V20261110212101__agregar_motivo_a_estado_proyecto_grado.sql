-- HU-302 — Motivo de la cancelación, guardado en la fila CANCELADO del historial de estados.
-- Sin DEFAULT: las filas anteriores no son cancelaciones y quedan en NULL.

ALTER TABLE estado_proyecto_grado ADD COLUMN motivo VARCHAR(500) NULL;
