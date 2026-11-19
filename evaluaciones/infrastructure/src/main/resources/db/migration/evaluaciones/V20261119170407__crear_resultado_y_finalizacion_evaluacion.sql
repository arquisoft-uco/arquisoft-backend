CREATE TABLE resultado_evaluacion (
    id VARCHAR(60) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    orden INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_resultado_evaluacion_nombre UNIQUE (nombre),
    CONSTRAINT uk_resultado_evaluacion_orden UNIQUE (orden)
);

INSERT INTO resultado_evaluacion (id, nombre, descripcion, orden) VALUES
    ('APROBADA', 'Aprobada', 'La evaluación alcanzó la nota mínima y no tiene observaciones', 1),
    ('APROBADA_CON_OBSERVACIONES', 'Aprobada con observaciones',
     'La evaluación alcanzó la nota mínima y tiene observaciones de asesor o jurado', 2),
    ('RECHAZADA', 'Rechazada', 'La nota final de la evaluación es menor a la mínima', 3);

ALTER TABLE evaluacion
    ADD COLUMN nota_final NUMERIC(4,2) NULL,
    ADD COLUMN resultado_evaluacion_id VARCHAR(60) NULL,
    ADD CONSTRAINT fk_evaluacion_resultado FOREIGN KEY (resultado_evaluacion_id) REFERENCES resultado_evaluacion (id);
