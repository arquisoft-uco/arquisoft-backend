-- =========================================================================
-- Crear tipo_solicitud_rol: rol de negocio que puede ENVIAR cada tipo de solicitud
-- Bounded Context: solicitudes
-- HU-101: la consulta de tipos de solicitud filtra por el rol del llamante
-- ADR-012: tabla nueva -> rol VARCHAR(60); la FK conserva el ancho de tipo_solicitud.id (60)
-- =========================================================================

CREATE TABLE tipo_solicitud_rol (
    tipo_solicitud_id VARCHAR(60) NOT NULL,
    rol               VARCHAR(60) NOT NULL,
    CONSTRAINT pk_tipo_solicitud_rol PRIMARY KEY (tipo_solicitud_id, rol),
    CONSTRAINT fk_tipo_solicitud_rol_tipo FOREIGN KEY (tipo_solicitud_id) REFERENCES tipo_solicitud(id)
);

CREATE INDEX idx_tipo_solicitud_rol_rol ON tipo_solicitud_rol(rol);

INSERT INTO tipo_solicitud_rol (tipo_solicitud_id, rol) VALUES
('NOVEDAD_PARA_EL_COORDINADOR',         'ESTUDIANTE'),
('NOVEDAD_PARA_EL_ASESOR',              'ESTUDIANTE'),
('CAMBIO_DE_ASESOR',                    'ESTUDIANTE'),
('AMPLIACION_DE_PLAZO',                 'ESTUDIANTE'),
('REGISTRO_Y_MODIFICACION_DE_USUARIOS', 'COORDINADOR');
