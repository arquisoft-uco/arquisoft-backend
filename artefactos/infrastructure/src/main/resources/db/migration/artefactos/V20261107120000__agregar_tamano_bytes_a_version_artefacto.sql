-- HT-020: peso de cada versión para la cuota de almacenamiento por proyecto de grado.
-- Las filas anteriores no tienen peso medido y entran con 0 (no cuentan en la cuota); el DEFAULT solo
-- existe para poblarlas y se retira de inmediato para que un INSERT sin tamaño falle en la base.
ALTER TABLE version_artefacto ADD COLUMN tamano_bytes BIGINT NOT NULL DEFAULT 0;
ALTER TABLE version_artefacto ALTER COLUMN tamano_bytes DROP DEFAULT;
ALTER TABLE version_artefacto ADD CONSTRAINT ck_va_tamano_bytes CHECK (tamano_bytes >= 0);
