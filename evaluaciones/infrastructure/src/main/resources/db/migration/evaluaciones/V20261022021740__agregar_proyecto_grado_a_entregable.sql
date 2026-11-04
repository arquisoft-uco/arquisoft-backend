-- Proyección entregable: se agrega el proyecto de grado al que pertenece (dato que entregable.generado debe poblar; sin FK entre contextos).
-- DEFAULT centinela de ceros (equivale a UtilUUID.obtenerUUIDPorDefecto()): los entregables previos a esta migración
-- no pertenecen a ningún proyecto, no coinciden con ningún vínculo estudiante-proyecto y ningún estudiante los ve.
ALTER TABLE entregable
    ADD COLUMN proyecto_grado_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000';

CREATE INDEX idx_entregable_proyecto_grado ON entregable (proyecto_grado_id);
