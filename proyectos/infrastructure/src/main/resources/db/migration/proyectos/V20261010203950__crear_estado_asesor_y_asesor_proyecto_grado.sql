-- HU-165 — Catálogo de estados del asesor y vínculo de asesores con el proyecto de grado,
-- copiados de mer/07_tablas_proyectos_grado.sql. Filas del catálogo copiadas literalmente de
-- mer/data/07_data_proyectos_grado.sql.
-- El ON DELETE CASCADE hacia asesor no se dispara: la réplica da de baja de forma lógica (eliminado_en).

CREATE TABLE estado_asesor (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_asesor_nombre UNIQUE (nombre)
);

INSERT INTO estado_asesor (id, nombre, descripcion) VALUES
    ('ASIGNADO',   'Asignado',   'El asesor está actualmente activo en el proyecto.'),
    ('FINALIZADO', 'Finalizado', 'El asesor ha concluido su participación en el proyecto.');

CREATE TABLE asesor_proyecto_grado (
    id                UUID        PRIMARY KEY,
    asesor_id         UUID        NOT NULL,
    proyecto_grado_id UUID        NOT NULL,
    estado_asesor_id  VARCHAR(60) NOT NULL,
    fecha_asignado    TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_apg_asesor   FOREIGN KEY (asesor_id) REFERENCES asesor(id) ON DELETE CASCADE,
    CONSTRAINT fk_apg_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id) ON DELETE CASCADE,
    CONSTRAINT fk_apg_estado   FOREIGN KEY (estado_asesor_id) REFERENCES estado_asesor(id),
    CONSTRAINT uk_apg_unica    UNIQUE (asesor_id, proyecto_grado_id, fecha_asignado)
);

CREATE INDEX idx_apg_estado ON asesor_proyecto_grado(estado_asesor_id);
CREATE INDEX idx_apg_asesor ON asesor_proyecto_grado(asesor_id);
