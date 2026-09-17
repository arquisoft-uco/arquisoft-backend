-- HU-248 — Baja lógica replicada desde usuarios (usuarios.estudiante.removido). NULL = vigente.
ALTER TABLE estudiante
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
