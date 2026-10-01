-- Esquema base del contexto solicitudes (MER: mer/11_tablas_solicitudes.sql).
-- Base de datos: solicitudes. Sin prefijo de base/schema; sin FK cruzada a otro contexto
-- (usuario/remitente/destinatario son réplicas locales pobladas por eventos AMQP).

-- 1. Tablas de referencia (catálogo)
-- ***************************************************************

CREATE TABLE estado_respuesta (
    id VARCHAR(60) PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_respuesta_nombre UNIQUE (nombre)
);

CREATE TABLE tipo_solicitud (
    id VARCHAR(60) PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_tipo_solicitud_nombre UNIQUE (nombre)
);

-- 2. Roles e identidades (proyecciones del contexto usuarios)
-- ***************************************************************

CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    identificador VARCHAR(30) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL
);

CREATE TABLE remitente (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    CONSTRAINT fk_remitente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT uk_remitente_usuario UNIQUE (usuario_id)
);

CREATE TABLE destinatario (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    CONSTRAINT fk_destinatario_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT uk_destinatario_usuario UNIQUE (usuario_id)
);

-- 3. Entidades de negocio
-- ***************************************************************

CREATE TABLE solicitud (
    id UUID PRIMARY KEY,
    destinatario_id UUID NOT NULL,
    remitente_id UUID NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    mensaje_solicitud VARCHAR(100) NOT NULL,
    tipo_solicitud_id VARCHAR(60) NOT NULL,
    CONSTRAINT fk_solicitud_destinatario FOREIGN KEY (destinatario_id)
        REFERENCES destinatario(id),
    CONSTRAINT fk_solicitud_remitente FOREIGN KEY (remitente_id)
        REFERENCES remitente(id),
    CONSTRAINT fk_solicitud_tipo FOREIGN KEY (tipo_solicitud_id)
        REFERENCES tipo_solicitud(id),
    CONSTRAINT uk_solicitud_unica UNIQUE (destinatario_id, remitente_id, fecha_creacion, mensaje_solicitud)
);

CREATE TABLE respuesta (
    id UUID PRIMARY KEY,
    solicitud_id UUID NOT NULL,
    fecha_respuesta TIMESTAMP NOT NULL,
    contenido VARCHAR(100) NOT NULL,
    estado_respuesta_id VARCHAR(60) NOT NULL,
    CONSTRAINT fk_respuesta_solicitud FOREIGN KEY (solicitud_id)
        REFERENCES solicitud(id) ON DELETE CASCADE,
    CONSTRAINT fk_respuesta_estado FOREIGN KEY (estado_respuesta_id)
        REFERENCES estado_respuesta(id),
    CONSTRAINT uk_respuesta_solicitud UNIQUE (solicitud_id)
);

-- ÍNDICES
CREATE INDEX idx_solicitud_destinatario ON solicitud(destinatario_id);
CREATE INDEX idx_solicitud_remitente ON solicitud(remitente_id);
CREATE INDEX idx_solicitud_tipo ON solicitud(tipo_solicitud_id);
CREATE INDEX idx_respuesta_estado ON respuesta(estado_respuesta_id);

-- 4. Datos de catálogo (mer/data/11_data_solicitudes.sql)
-- ***************************************************************

INSERT INTO estado_respuesta (id, nombre, descripcion) VALUES
    ('APROBADA',     'Aprobada',     'La respuesta fue revisada y cumple con los criterios establecidos.'),
    ('NO_APROBADA',  'No aprobada',  'La respuesta fue revisada y no cumple con los criterios establecidos.'),
    ('EN_REVISION',  'En revisión',  'La respuesta está en proceso de evaluación y aún no tiene un veredicto final.');

INSERT INTO tipo_solicitud (id, nombre, descripcion) VALUES
    ('NOVEDAD_PARA_EL_COORDINADOR',          'Novedad para el Coordinador',          'Solicitud para temas que surgen de improvisto y que no están tipados'),
    ('NOVEDAD_PARA_EL_ASESOR',               'Novedad para el Asesor',               'Solicitud para temas que surgen de improvisto y que no están tipados'),
    ('CAMBIO_DE_ASESOR',                     'Cambio de Asesor',                     'Solicitud para modificar el asesor'),
    ('AMPLIACION_DE_PLAZO',                  'Ampliación de Plazo',                  'Solicitud para extender la fecha de entrega del proyecto'),
    ('REGISTRO_Y_MODIFICACION_DE_USUARIOS',  'Registro y modificación de Usuarios',  'Solicitud para el registro o modificación de usuarios');
