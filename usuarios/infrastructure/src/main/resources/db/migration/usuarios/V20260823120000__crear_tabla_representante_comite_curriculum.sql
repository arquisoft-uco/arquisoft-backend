-- ==============================================================================
-- Contexto: usuarios | Base de datos: usuarios
-- ==============================================================================

-- Tabla de representantes del comite curriculum.
-- Cada representante es un usuario existente (FK a usuario).
CREATE TABLE representante_comite_curriculum (
    usuario_id UUID NOT NULL,
    PRIMARY KEY (usuario_id),
    CONSTRAINT fk_representante_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);
