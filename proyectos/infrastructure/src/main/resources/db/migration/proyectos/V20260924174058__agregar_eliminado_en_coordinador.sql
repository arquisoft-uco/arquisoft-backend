-- HU-244 — Baja lógica replicada desde usuarios (usuarios.coordinador.removido). NULL = vigente.
ALTER TABLE coordinador
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
