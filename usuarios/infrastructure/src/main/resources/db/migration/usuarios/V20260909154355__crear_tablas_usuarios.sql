-- HU-256 — Registrar información de un nuevo usuario.
-- DDL tomado de mer/02_tablas_usuarios.sql, sin prefijo de base/schema y sin FK cruzada.

-- Objeto de Dominio: EstadoUsuario (ADR-012: PK semántica VARCHAR(60) = EstadoUsuario.name())
CREATE TABLE estado_usuario (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_nombre UNIQUE (nombre)
);

INSERT INTO estado_usuario (id, nombre, descripcion) VALUES
    ('ACTIVO',   'Activo',   'Indica que un usuario puede desempeñarse'),
    ('INACTIVO', 'Inactivo', 'Indica que un usuario no puede desempeñarse');

-- Objeto de Dominio: Usuario
CREATE TABLE usuario (
    id            UUID        PRIMARY KEY,
    identificador VARCHAR(30) NOT NULL,
    nombre        VARCHAR(50) NOT NULL,
    email         VARCHAR(50) NOT NULL,
    contacto      VARCHAR(16) NOT NULL,
    estado_id     VARCHAR(60) NOT NULL,
    CONSTRAINT uk_usuario_identificador UNIQUE (identificador),
    CONSTRAINT uk_usuario_email         UNIQUE (email),
    CONSTRAINT uk_usuario_contacto      UNIQUE (contacto),
    CONSTRAINT fk_usuario_estado FOREIGN KEY (estado_id) REFERENCES estado_usuario(id)
);

CREATE INDEX idx_usuario_busqueda_ident ON usuario(identificador);
CREATE INDEX idx_usuario_busqueda_email ON usuario(email);
