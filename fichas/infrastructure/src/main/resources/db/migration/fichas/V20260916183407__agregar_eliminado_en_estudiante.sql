-- HU-248 — Baja lógica replicada desde usuarios (usuarios.estudiante.removido). NULL = vigente.
ALTER TABLE estudiante
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;

CREATE INDEX idx_estudiante_vigente ON estudiante(id) WHERE eliminado_en IS NULL;
