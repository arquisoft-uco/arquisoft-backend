-- HU-235 — Baja lógica replicada desde usuarios (usuarios.asesor.removido). NULL = vigente.
ALTER TABLE asesor
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
