CREATE TABLE item_cuantitativo_asesor (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    categoria_id UUID NOT NULL,
    valor INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_cuant_cat_asesor FOREIGN KEY (categoria_id) REFERENCES categoria_item_cuantitativo_asesor (id),
    CONSTRAINT uk_item_cuant_asesor_nombre_cat UNIQUE (nombre, categoria_id)
);

CREATE TABLE evaluacion_cuantitativa_asesor (
    id UUID NOT NULL,
    evaluacion_asesor_id UUID NOT NULL,
    item_id UUID NOT NULL,
    puntaje INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_eca2_eval_item UNIQUE (evaluacion_asesor_id, item_id),
    CONSTRAINT fk_eca2_eval FOREIGN KEY (evaluacion_asesor_id) REFERENCES evaluacion_asesor (id),
    CONSTRAINT fk_eca2_item FOREIGN KEY (item_id) REFERENCES item_cuantitativo_asesor (id)
);

CREATE INDEX idx_eval_cuant_asesor ON evaluacion_cuantitativa_asesor (evaluacion_asesor_id);
