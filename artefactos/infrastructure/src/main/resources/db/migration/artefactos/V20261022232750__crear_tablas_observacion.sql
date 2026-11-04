CREATE TABLE estado_observacion (
    id VARCHAR(60) PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_observacion_nombre UNIQUE (nombre)
);

INSERT INTO estado_observacion (id, nombre, descripcion) VALUES
    ('NUEVA',       'Nueva',       'Nueva observación asignada por el asesor'),
    ('EN_PROGRESO', 'En progreso', 'La observación está siendo validada por el estudiante'),
    ('CERRADA',     'Cerrada',     'La observación ha sido resuelta por el estudiante'),
    ('SOLUCIONADA', 'Solucionada', 'La observación ha sido resuelta por el estudiante y espera ser revisada por el asesor'),
    ('REABIERTA',   'Reabierta',   'La observación ha sido reabierta por el asesor');

CREATE TABLE observacion (
    id UUID PRIMARY KEY,
    revision_asesor_id UUID NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    estado_observacion_id VARCHAR(60) NOT NULL,
    CONSTRAINT fk_obs_revision FOREIGN KEY (revision_asesor_id)
        REFERENCES revision_asesor(id) ON DELETE CASCADE,
    CONSTRAINT fk_obs_estado FOREIGN KEY (estado_observacion_id)
        REFERENCES estado_observacion(id),
    CONSTRAINT uk_obs_revision_desc UNIQUE (revision_asesor_id, descripcion)
);
