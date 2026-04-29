-- ============================================================
-- Migración V1: Creación del schema usuarios y tablas base
-- Contexto: seguridad → schema PostgreSQL: usuarios
-- HU-260: Consultar información de los usuarios (Administrador)
-- ============================================================

CREATE SCHEMA IF NOT EXISTS usuarios;

SET search_path TO usuarios;

-- ─────────────────────────────────────────────────────────────
-- Tabla principal de usuarios
-- ─────────────────────────────────────────────────────────────
CREATE TABLE usuario (
    id                 UUID         PRIMARY KEY,
    keycloak_user_id   UUID         NOT NULL,
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL,
    identificador      VARCHAR(50)  NOT NULL,
    estado             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT uk_usuario_keycloak UNIQUE (keycloak_user_id),
    CONSTRAINT uk_usuario_email    UNIQUE (email),
    CONSTRAINT uk_usuario_ident    UNIQUE (identificador),
    CONSTRAINT ck_usuario_estado   CHECK  (estado IN ('ACTIVO', 'INACTIVO'))
);

-- ─────────────────────────────────────────────────────────────
-- Catálogo inmutable de roles del sistema
-- Los códigos coinciden exactamente con UsuarioRole.getCode()
-- ─────────────────────────────────────────────────────────────
CREATE TABLE rol (
    id          UUID         PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL,
    descripcion VARCHAR(200),

    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

-- ─────────────────────────────────────────────────────────────
-- Relación N:M usuario ↔ rol
-- ─────────────────────────────────────────────────────────────
CREATE TABLE usuario_rol (
    usuario_id UUID NOT NULL,
    rol_id     UUID NOT NULL,

    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES rol(id)
);

-- ─────────────────────────────────────────────────────────────
-- Índices para optimizar las consultas con filtros (HU-260)
-- ─────────────────────────────────────────────────────────────
CREATE INDEX idx_usuario_keycloak     ON usuario(keycloak_user_id);
CREATE INDEX idx_usuario_email        ON usuario(email);
CREATE INDEX idx_usuario_ident        ON usuario(identificador);
CREATE INDEX idx_usuario_estado       ON usuario(estado);
CREATE INDEX idx_usuario_nombre_ap    ON usuario(nombre, apellido);
CREATE INDEX idx_ur_rol               ON usuario_rol(rol_id);

-- ─────────────────────────────────────────────────────────────
-- Datos iniciales: 8 roles del sistema
-- Coinciden con UsuarioRole.getCode() — inmutables, no se
-- crean ni eliminan por HUs de negocio.
-- ─────────────────────────────────────────────────────────────
INSERT INTO rol (id, nombre, descripcion) VALUES
    (gen_random_uuid(), 'ESTUDIANTE',
        'Estudiante activo con proyecto de grado en curso'),
    (gen_random_uuid(), 'ASESOR',
        'Docente asesor de proyectos de grado'),
    (gen_random_uuid(), 'ASESOR_FICHA',
        'Asesor asignado a la ficha perfil de un proyecto'),
    (gen_random_uuid(), 'JURADO',
        'Evaluador externo asignado como jurado de sustentación'),
    (gen_random_uuid(), 'COORDINADOR',
        'Coordinador del programa académico'),
    (gen_random_uuid(), 'ADMINISTRADOR',
        'Administrador general del sistema'),
    (gen_random_uuid(), 'BIBLIOTECARIO',
        'Responsable de la gestión de la biblioteca digital'),
    (gen_random_uuid(), 'REPRESENTANTE_COMITE_CURRICULUM',
        'Representante del comité de currículo ante el programa');
