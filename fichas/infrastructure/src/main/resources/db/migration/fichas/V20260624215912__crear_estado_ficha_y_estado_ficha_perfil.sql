-- =========================================================================
-- V1.2 — Crear tablas estado_ficha (catálogo) y estado_ficha_perfil (trazabilidad)
-- Bounded Context: fichas
-- HU-206: Agregar estado inicial "En Construccion" a ficha perfil
-- ADR-012: PK semántica VARCHAR(50) en estado_ficha (valor = constante del enum Java)
-- =========================================================================

-- Catálogo de estados del ciclo de vida de una ficha de perfil
CREATE TABLE estado_ficha (
    id          VARCHAR(50)  PRIMARY KEY,
    nombre      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    CONSTRAINT uk_estado_ficha_nombre UNIQUE (nombre)
);

-- Poblar catálogo con datos iniciales (PK = constante del enum Java)
INSERT INTO estado_ficha (id, nombre, descripcion) VALUES
('APROBADA',                   'Aprobada',                   'Se refiere a que la ficha de perfil paso por revisión del comite de curriculum y tuvo una calificación mayor de 3.0'),
('APROBADA_CON_OBSERVACIONES', 'Aprobada Con Observaciones', 'Se refiere a que la ficha de perfil paso por la revisión del comite del curriculum, pero debe ser revisado debido a que necesita una mejora'),
('NO_APROBADA',                'No Aprobada',                'Se refiere a que la ficha de perfil paso por la revisión del comite del curriculum, pero la ficha de perfil no obtuvo una calificación mayor a 3.0'),
('EN_CONSTRUCCION',            'En Construccion',            'Se refiere a que la ficha de perfil se encuentra en construcción o desarrollo.'),
('DISPONIBLE_PARA_EVALUACION', 'Disponible Para Evaluacion', 'Se refiere a que la ficha de perfil se encuentra disponible para ser evaluada por los representantes del comite de curriculum.');

-- Tabla de trazabilidad de estados de ficha perfil
CREATE TABLE estado_ficha_perfil (
    id                  UUID        PRIMARY KEY,
    ficha_perfil_id     UUID        NOT NULL,
    estado_ficha_id     VARCHAR(50) NOT NULL,
    fecha_actualizacion TIMESTAMP   NOT NULL,
    CONSTRAINT fk_efp_traz_ficha  FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE,
    CONSTRAINT fk_efp_traz_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id)
);
