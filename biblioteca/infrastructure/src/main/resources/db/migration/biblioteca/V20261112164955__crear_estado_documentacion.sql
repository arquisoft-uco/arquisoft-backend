-- HU-181 — Catálogo estado_documentacion (MER 10_tablas_biblioteca.sql). ADR-012: id VARCHAR(60) = nombre del enum EstadoDocumentacion.
CREATE TABLE estado_documentacion (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_documentacion_nombre UNIQUE (nombre)
);

INSERT INTO estado_documentacion (id, nombre, descripcion) VALUES
    ('PENDIENTE',    'Pendiente',    'La documentación se encuentra pendiente para su publicación'),
    ('PUBLICADO',    'Publicado',    'La documentación se encuentra publicada.'),
    ('NO_PUBLICADO', 'No Publicado', 'La documentación no se encuentra publicada por algún motivo justificable');
