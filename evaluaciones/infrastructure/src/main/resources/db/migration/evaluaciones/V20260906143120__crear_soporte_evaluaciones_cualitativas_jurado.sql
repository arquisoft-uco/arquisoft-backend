CREATE TABLE jurado (
    id UUID NOT NULL,
    identificador VARCHAR(30) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE entregable (
    id UUID NOT NULL,
    proyecto VARCHAR(200) NOT NULL,
    version_entregable INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE estado_evaluacion (
    id VARCHAR(60) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_estado_evaluacion_nombre UNIQUE (nombre)
);

INSERT INTO estado_evaluacion (id, nombre, descripcion) VALUES
    ('PENDIENTE', 'Pendiente', 'Indica que una evaluación está pendiente por realizar'),
    ('EN_PROGRESO', 'En progreso', 'Indica que una evaluación está en curso'),
    ('FINALIZADA', 'Finalizada', 'Indica que una evaluación ha sido finalizada');

CREATE TABLE evaluacion (
    id UUID NOT NULL,
    entregable_id UUID NOT NULL,
    estado_evaluacion_id VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_evaluacion_entregable UNIQUE (entregable_id),
    CONSTRAINT fk_evaluacion_entregable FOREIGN KEY (entregable_id) REFERENCES entregable (id),
    CONSTRAINT fk_evaluacion_estado FOREIGN KEY (estado_evaluacion_id) REFERENCES estado_evaluacion (id)
);

CREATE TABLE evaluacion_jurado (
    id UUID NOT NULL,
    evaluacion_id UUID NOT NULL,
    jurado_id UUID NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_evaluacion_jurado UNIQUE (evaluacion_id, jurado_id),
    CONSTRAINT fk_evaluacion_jurado_evaluacion FOREIGN KEY (evaluacion_id) REFERENCES evaluacion (id),
    CONSTRAINT fk_evaluacion_jurado_jurado FOREIGN KEY (jurado_id) REFERENCES jurado (id)
);

CREATE TABLE evaluacion_cualitativa_jurado (
    id UUID NOT NULL,
    evaluacion_jurado_id UUID NOT NULL,
    item_id UUID NOT NULL,
    criterio_id UUID NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_eval_cual_jurado_item UNIQUE (evaluacion_jurado_id, item_id),
    CONSTRAINT fk_eval_cual_jurado_evaluacion_jurado FOREIGN KEY (evaluacion_jurado_id) REFERENCES evaluacion_jurado (id),
    CONSTRAINT fk_eval_cual_jurado_item FOREIGN KEY (item_id) REFERENCES item_cualitativo_jurado (id),
    CONSTRAINT fk_eval_cual_jurado_criterio FOREIGN KEY (criterio_id) REFERENCES criterio_item_cualitativo_jurado (id)
);

CREATE INDEX idx_evaluacion_entregable ON evaluacion (entregable_id);
CREATE INDEX idx_eval_jurado ON evaluacion_jurado (evaluacion_id);
CREATE INDEX idx_eval_cual_jurado ON evaluacion_cualitativa_jurado (evaluacion_jurado_id);
