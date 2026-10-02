CREATE TABLE observacion_evaluacion (
    id                         UUID         PRIMARY KEY,
    evaluacion_ficha_perfil_id UUID         NOT NULL,
    observacion                VARCHAR(200) NOT NULL,
    CONSTRAINT fk_obs_eval_base FOREIGN KEY (evaluacion_ficha_perfil_id)
        REFERENCES evaluacion_ficha_perfil(id) ON DELETE CASCADE,
    CONSTRAINT uk_obs_eval_msg UNIQUE (evaluacion_ficha_perfil_id, observacion)
);
