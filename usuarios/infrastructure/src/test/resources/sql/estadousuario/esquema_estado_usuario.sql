CREATE TABLE IF NOT EXISTS estado_usuario (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_nombre UNIQUE (nombre)
);
