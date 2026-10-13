-- HU-238 — Baja lógica replicada desde usuarios (usuarios.asesorficha.removido). NULL = vigente.
ALTER TABLE asesor_ficha
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;

CREATE INDEX idx_asesor_ficha_vigente ON asesor_ficha(id) WHERE eliminado_en IS NULL;
