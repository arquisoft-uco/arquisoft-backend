-- HU-147-AJ4 — Versión del artefacto que quedó en el paquete del entregable.
-- Filas previas (solo datos locales de HU-147-AJ3, no integrada): quedan en 1; el DEFAULT se retira para que un insert sin la columna falle.
ALTER TABLE artefacto ADD COLUMN version_artefacto INTEGER NOT NULL DEFAULT 1;
ALTER TABLE artefacto ALTER COLUMN version_artefacto DROP DEFAULT;
ALTER TABLE artefacto ADD CONSTRAINT ck_artefacto_version CHECK (version_artefacto >= 1);
