CREATE TABLE estado_evaluacion_asignada_jurado (
    id VARCHAR(60) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    orden INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_est_eval_asig_jurado_nombre UNIQUE (nombre),
    CONSTRAINT uk_est_eval_asig_jurado_orden UNIQUE (orden)
);

INSERT INTO estado_evaluacion_asignada_jurado (id, nombre, descripcion, orden) VALUES
    ('PENDIENTE_POR_EVALUAR', 'Pendiente por evaluar', 'Indica que el jurado aún no ha evaluado el entregable', 1),
    ('EVALUADA', 'Evaluada', 'Indica que el jurado ya evaluó el entregable', 2);

ALTER TABLE evaluacion_jurado
    ADD COLUMN estado_evaluacion_asignada_jurado_id VARCHAR(60);

UPDATE evaluacion_jurado SET estado_evaluacion_asignada_jurado_id = 'PENDIENTE_POR_EVALUAR';

ALTER TABLE evaluacion_jurado
    ALTER COLUMN estado_evaluacion_asignada_jurado_id SET NOT NULL;

ALTER TABLE evaluacion_jurado
    ADD CONSTRAINT fk_evaluacion_jurado_estado
        FOREIGN KEY (estado_evaluacion_asignada_jurado_id) REFERENCES estado_evaluacion_asignada_jurado (id);

CREATE INDEX idx_eval_jurado_estado ON evaluacion_jurado (estado_evaluacion_asignada_jurado_id);
