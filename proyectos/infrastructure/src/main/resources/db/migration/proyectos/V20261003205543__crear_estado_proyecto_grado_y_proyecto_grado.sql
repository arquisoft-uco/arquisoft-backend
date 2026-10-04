-- HU-184 — Catálogo de estados del proyecto de grado y proyecto de grado.
-- Según mer/07_tablas_proyectos_grado.sql: ficha_perfil queda en desuso en este contexto;
-- proyecto_grado referencia la ficha por id (sin FK: vive en la base fichas_perfil) y copia su título.
-- Filas del catálogo copiadas literalmente de mer/data/07_data_proyectos_grado.sql.

CREATE TABLE estado_proyecto_grado (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_proyecto_nombre UNIQUE (nombre)
);

INSERT INTO estado_proyecto_grado (id, nombre, descripcion) VALUES
    ('EN_PROCESO',            'En proceso',            'El proyecto se encuentra en desarrollo activo. Los estudiantes están trabajando en su ejecución.'),
    ('LISTO_PARA_REVISION',   'Listo Para Revisión',   'El proyecto ha sido completado y está pendiente de revisión por parte del asesor o coordinador.'),
    ('ATRASADO',              'Atrasado',              'El proyecto no ha cumplido con los plazos establecidos y requiere ajustes para su continuidad.'),
    ('FINALIZADO',            'Finalizado',            'El proyecto ha sido aprobado y concluido satisfactoriamente.');

CREATE TABLE proyecto_grado (
    id                       UUID         PRIMARY KEY,
    estado_proyecto_grado_id VARCHAR(60)  NOT NULL,
    coordinador_id           UUID         NOT NULL,
    ficha_perfil_id          UUID         NOT NULL,
    titulo_proyecto          VARCHAR(100) NOT NULL,
    CONSTRAINT fk_pg_estado      FOREIGN KEY (estado_proyecto_grado_id) REFERENCES estado_proyecto_grado(id),
    CONSTRAINT fk_pg_coordinador FOREIGN KEY (coordinador_id) REFERENCES coordinador(id),
    CONSTRAINT uk_pg_ficha       UNIQUE (ficha_perfil_id)
);

CREATE INDEX idx_proyecto_estado      ON proyecto_grado(estado_proyecto_grado_id);
CREATE INDEX idx_proyecto_coordinador ON proyecto_grado(coordinador_id);
