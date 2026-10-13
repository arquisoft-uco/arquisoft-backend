-- =========================================================================
-- Crear estado_ficha_rol: roles de negocio habilitados para consultar cada estado ficha
-- Bounded Context: fichas
-- HU-036: la consulta de estados filtra por el rol del llamante
-- ADR-012: tabla nueva -> rol VARCHAR(60); la FK conserva el ancho de estado_ficha.id (50, excepción documentada)
-- =========================================================================

CREATE TABLE estado_ficha_rol (
    estado_ficha_id VARCHAR(50) NOT NULL,
    rol             VARCHAR(60) NOT NULL,
    CONSTRAINT pk_estado_ficha_rol PRIMARY KEY (estado_ficha_id, rol),
    CONSTRAINT fk_estado_ficha_rol_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id)
);

CREATE INDEX idx_estado_ficha_rol_rol ON estado_ficha_rol(rol);

INSERT INTO estado_ficha (id, nombre, descripcion) VALUES
('DESCARTADA', 'Descartada', 'Se refiere a que el asesor de ficha descarto la ficha de perfil; no es un estado final y puede volver a construccion.');

INSERT INTO estado_ficha_rol (estado_ficha_id, rol) VALUES
('EN_CONSTRUCCION',            'ASESOR_FICHA'),
('DISPONIBLE_PARA_EVALUACION', 'ASESOR_FICHA'),
('DESCARTADA',                 'ASESOR_FICHA'),
('APROBADA',                   'COORDINADOR'),
('APROBADA',                   'REPRESENTANTE_COMITE'),
('APROBADA_CON_OBSERVACIONES', 'COORDINADOR'),
('APROBADA_CON_OBSERVACIONES', 'REPRESENTANTE_COMITE'),
('NO_APROBADA',                'COORDINADOR'),
('NO_APROBADA',                'REPRESENTANTE_COMITE');
