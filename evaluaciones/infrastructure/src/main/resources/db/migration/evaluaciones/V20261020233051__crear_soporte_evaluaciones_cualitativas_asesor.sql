CREATE TABLE item_cualitativo_asesor (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_item_cual_asesor_nombre UNIQUE (nombre)
);

CREATE TABLE criterio_item_cualitativo_asesor (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_crit_cual_asesor_nombre UNIQUE (nombre)
);

CREATE TABLE evaluacion_cualitativa_asesor (
    id UUID NOT NULL,
    evaluacion_asesor_id UUID NOT NULL,
    item_id UUID NOT NULL,
    criterio_id UUID NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_eca_eval_item UNIQUE (evaluacion_asesor_id, item_id),
    CONSTRAINT fk_eca_evaluacion_asesor FOREIGN KEY (evaluacion_asesor_id) REFERENCES evaluacion_asesor (id),
    CONSTRAINT fk_eca_item FOREIGN KEY (item_id) REFERENCES item_cualitativo_asesor (id),
    CONSTRAINT fk_eca_criterio FOREIGN KEY (criterio_id) REFERENCES criterio_item_cualitativo_asesor (id)
);

CREATE INDEX idx_eval_cual_asesor ON evaluacion_cualitativa_asesor (evaluacion_asesor_id);
