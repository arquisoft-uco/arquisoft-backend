CREATE TABLE evaluacion_cuantitativa_jurado (
    id UUID NOT NULL,
    evaluacion_jurado_id UUID NOT NULL,
    item_id UUID NOT NULL,
    puntaje INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_ecj2_eval_item UNIQUE (evaluacion_jurado_id, item_id),
    CONSTRAINT fk_ecj2_eval FOREIGN KEY (evaluacion_jurado_id) REFERENCES evaluacion_jurado (id),
    CONSTRAINT fk_ecj2_item FOREIGN KEY (item_id) REFERENCES item_cuantitativo_jurado (id)
);

CREATE INDEX idx_eval_cuant_jurado ON evaluacion_cuantitativa_jurado (evaluacion_jurado_id);
