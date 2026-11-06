-- =========================================================================
-- Crear tipo_solicitud_rol_recibida: rol de negocio que puede RECIBIR cada tipo de solicitud
-- Bounded Context: solicitudes
-- HU-298: la consulta de tipos de solicitud para recibir filtra por el rol del llamante
-- ADR-012: tabla nueva -> rol VARCHAR(60); la FK conserva el ancho de tipo_solicitud.id (60)
-- =========================================================================

CREATE TABLE tipo_solicitud_rol_recibida (
    tipo_solicitud_id VARCHAR(60) NOT NULL,
    rol               VARCHAR(60) NOT NULL,
    CONSTRAINT pk_tipo_solicitud_rol_recibida PRIMARY KEY (tipo_solicitud_id, rol),
    CONSTRAINT fk_tipo_solicitud_rol_recibida_tipo FOREIGN KEY (tipo_solicitud_id) REFERENCES tipo_solicitud(id)
);

CREATE INDEX idx_tipo_solicitud_rol_recibida_rol ON tipo_solicitud_rol_recibida(rol);

INSERT INTO tipo_solicitud_rol_recibida (tipo_solicitud_id, rol) VALUES
('NOVEDAD_PARA_EL_COORDINADOR',         'COORDINADOR'),
('NOVEDAD_PARA_EL_ASESOR',              'ASESOR'),
('CAMBIO_DE_ASESOR',                    'COORDINADOR'),
('AMPLIACION_DE_PLAZO',                 'COORDINADOR'),
('REGISTRO_Y_MODIFICACION_DE_USUARIOS', 'ADMINISTRADOR');
