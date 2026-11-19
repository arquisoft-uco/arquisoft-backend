-- Retiro lógico del jurado: la fila y sus registros se conservan; un jurado retirado puede volver a asignarse.
ALTER TABLE evaluacion_jurado
    ADD COLUMN eliminado_en TIMESTAMPTZ NULL;

ALTER TABLE evaluacion_jurado
    DROP CONSTRAINT uk_evaluacion_jurado;

CREATE UNIQUE INDEX uk_evaluacion_jurado_vigente
    ON evaluacion_jurado (evaluacion_id, jurado_id)
    WHERE eliminado_en IS NULL;

CREATE INDEX idx_evaluacion_jurado_vigentes
    ON evaluacion_jurado (evaluacion_id)
    WHERE eliminado_en IS NULL;
