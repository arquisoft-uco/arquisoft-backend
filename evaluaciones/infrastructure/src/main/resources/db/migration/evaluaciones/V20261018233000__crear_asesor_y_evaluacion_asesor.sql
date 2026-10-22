-- Tabla réplica local de asesor (dueño: contexto usuarios). NULL en eliminado_en = vigente.
CREATE TABLE asesor (
    id UUID NOT NULL,
    identificador VARCHAR(30) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    eliminado_en TIMESTAMPTZ NULL,
    PRIMARY KEY (id)
);

CREATE TABLE evaluacion_asesor (
    id UUID NOT NULL,
    evaluacion_id UUID NOT NULL,
    asesor_id UUID NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_evaluacion_asesor UNIQUE (evaluacion_id),
    CONSTRAINT fk_evaluacion_asesor_evaluacion FOREIGN KEY (evaluacion_id) REFERENCES evaluacion (id),
    CONSTRAINT fk_evaluacion_asesor_asesor FOREIGN KEY (asesor_id) REFERENCES asesor (id)
);

CREATE INDEX idx_eval_asesor ON evaluacion_asesor (asesor_id);
