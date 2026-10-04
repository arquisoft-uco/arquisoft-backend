-- HU-253 — Tabla réplica local de representante del comité (dueño: contexto usuarios), versionada por el
-- instante del hecho en el origen. DEFAULT '-infinity': las filas previas son más viejas que cualquier evento.
-- eliminado_en: baja lógica replicada (NULL = vigente); la escribe HU254.
ALTER TABLE representante_comite_curriculum
    ADD COLUMN ocurrido_en  TIMESTAMPTZ NOT NULL DEFAULT '-infinity',
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;

CREATE INDEX idx_representante_comite_vigente ON representante_comite_curriculum(id) WHERE eliminado_en IS NULL;
