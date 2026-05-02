-- Schema de test para H2 en memoria — HU-260
-- Crea el schema 'usuarios' y las tablas necesarias para los tests de repositorio.
-- Flyway está deshabilitado en los tests (@DataJpaTest).

CREATE SCHEMA IF NOT EXISTS usuarios;

-- Catálogo de estados
CREATE TABLE IF NOT EXISTS usuarios.estado_usuario (
    id          UUID        NOT NULL PRIMARY KEY,
    nombre      VARCHAR(20) NOT NULL,
    descripcion VARCHAR(200),
    CONSTRAINT uk_estado_usuario_nombre UNIQUE (nombre)
);

-- Catálogo de roles
CREATE TABLE IF NOT EXISTS usuarios.rol (
    id          UUID        NOT NULL PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

-- Tabla principal de usuarios
CREATE TABLE IF NOT EXISTS usuarios.usuario (
    id               UUID         NOT NULL PRIMARY KEY,
    keycloak_user_id UUID         NOT NULL,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL,
    identificador    VARCHAR(50)  NOT NULL,
    estado_id        UUID         NOT NULL,
    CONSTRAINT uk_usuario_keycloak  UNIQUE (keycloak_user_id),
    CONSTRAINT uk_usuario_email     UNIQUE (email),
    CONSTRAINT uk_usuario_ident     UNIQUE (identificador),
    CONSTRAINT fk_usuario_estado    FOREIGN KEY (estado_id) REFERENCES usuarios.estado_usuario(id)
);

-- Relación N:M usuario ↔ rol
CREATE TABLE IF NOT EXISTS usuarios.usuario_rol (
    usuario_id UUID NOT NULL,
    rol_id     UUID NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios.usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES usuarios.rol(id)
);

-- Datos catálogo fijos (UUIDs deterministas para los tests)
INSERT INTO usuarios.estado_usuario (id, nombre, descripcion)
VALUES
    ('11111111-0000-0000-0000-000000000001', 'ACTIVO',   'Usuario activo'),
    ('11111111-0000-0000-0000-000000000002', 'INACTIVO', 'Usuario inactivo')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO usuarios.rol (id, nombre, descripcion)
VALUES
    ('22222222-0000-0000-0000-000000000001', 'ESTUDIANTE',   'Estudiante'),
    ('22222222-0000-0000-0000-000000000002', 'COORDINADOR',  'Coordinador'),
    ('22222222-0000-0000-0000-000000000003', 'ADMINISTRADOR','Administrador')
ON CONFLICT (nombre) DO NOTHING;
