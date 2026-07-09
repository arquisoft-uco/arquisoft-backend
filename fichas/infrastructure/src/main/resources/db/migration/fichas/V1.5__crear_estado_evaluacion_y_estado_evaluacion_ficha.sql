-- V1.5__crear_estado_evaluacion_y_estado_evaluacion_ficha.sql
-- Catálogo de estados de evaluación + tabla de trazabilidad de estados por evaluación

-- ============================================================================
-- TABLA CATÁLOGO: estado_evaluacion (PK semántica VARCHAR)
-- ============================================================================
CREATE TABLE estado_evaluacion (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255)
);

-- Poblar catálogo con 5 estados
INSERT INTO estado_evaluacion (id, nombre, descripcion) VALUES
    ('EN_EVALUACION', 'En Evaluación', 'La evaluación está en proceso de revisión'),
    ('APROBADA', 'Aprobada', 'La evaluación ha sido aprobada sin observaciones'),
    ('APROBADA_CON_OBSERVACIONES', 'Aprobada Con Observaciones', 'La evaluación ha sido aprobada con observaciones que requieren seguimiento'),
    ('NO_APROBADA', 'No Aprobada', 'La evaluación no ha sido aprobada'),
    ('DESCARTADA', 'Descartada', 'La evaluación ha sido descartada');

-- ============================================================================
-- TABLA TRAZABILIDAD: estado_evaluacion_ficha
-- ============================================================================
CREATE TABLE estado_evaluacion_ficha (
    id UUID PRIMARY KEY,
    evaluacion_ficha_perfil_id UUID NOT NULL,
    estado_evaluacion_id VARCHAR(50) NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    CONSTRAINT fk_estado_eval_ficha_evaluacion
        FOREIGN KEY (evaluacion_ficha_perfil_id)
        REFERENCES evaluacion_ficha_perfil(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_estado_eval_ficha_estado
        FOREIGN KEY (estado_evaluacion_id)
        REFERENCES estado_evaluacion(id)
);

-- Índice para optimizar lookups de último estado por evaluación
CREATE INDEX idx_estado_evaluacion_ficha_evaluacion
    ON estado_evaluacion_ficha(evaluacion_ficha_perfil_id, fecha_actualizacion DESC);
