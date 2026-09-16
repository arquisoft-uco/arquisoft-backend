CREATE TABLE observacion_item_jurado (
    id UUID NOT NULL,
    evaluacion_cuantitativa_jurado_id UUID NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_obs_ij_item_desc UNIQUE (evaluacion_cuantitativa_jurado_id, descripcion),
    CONSTRAINT fk_obs_ij_eval FOREIGN KEY (evaluacion_cuantitativa_jurado_id)
        REFERENCES evaluacion_cuantitativa_jurado (id)
);

CREATE INDEX idx_obs_ij_evaluacion ON observacion_item_jurado (evaluacion_cuantitativa_jurado_id);
