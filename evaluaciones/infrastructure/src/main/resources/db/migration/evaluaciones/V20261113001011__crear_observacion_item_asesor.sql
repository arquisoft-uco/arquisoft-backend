CREATE TABLE observacion_item_asesor (
    id UUID PRIMARY KEY,
    evaluacion_cuantitativa_asesor_id UUID NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    CONSTRAINT fk_obs_ia_eval FOREIGN KEY (evaluacion_cuantitativa_asesor_id)
        REFERENCES evaluacion_cuantitativa_asesor (id) ON DELETE CASCADE,
    CONSTRAINT uk_obs_ia_item_desc UNIQUE (evaluacion_cuantitativa_asesor_id, descripcion)
);
