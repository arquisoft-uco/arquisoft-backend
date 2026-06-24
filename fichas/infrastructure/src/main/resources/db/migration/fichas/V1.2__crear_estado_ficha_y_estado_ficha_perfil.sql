-- =========================================================================
-- V1.2 — Crear tablas estado_ficha (catálogo) y estado_ficha_perfil (trazabilidad)
-- Bounded Context: fichas
-- HU-206: Agregar estado inicial "En Construccion" a ficha perfil
-- =========================================================================

-- Catálogo de estados del ciclo de vida de una ficha de perfil
CREATE TABLE estado_ficha (
    id UUID PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    CONSTRAINT uk_estado_ficha_nombre UNIQUE (nombre)
);

-- Poblar catálogo con datos iniciales del MER
INSERT INTO estado_ficha (id, nombre, descripcion) VALUES
(gen_random_uuid(), 'Aprobada',                   'Se refiere a que la ficha de perfil paso por revisión del comite de curriculum y tuvo una calificación mayor de 3.0'),
(gen_random_uuid(), 'Aprobada Con Observaciones', 'Se refiere a que la ficha de perfil paso por la revisión del comite del curriculum, pero debe ser revisado debido a que necesita una mejora'),
(gen_random_uuid(), 'No Aprobada',                'Se refiere a que la ficha de perfil paso por la revisión del comite del curriculum, pero la ficha de perfil no obtuvo una calificación mayor a 3.0'),
(gen_random_uuid(), 'En Construccion',            'Se refiere a que la ficha de pérfil se encuentra en construcción o desarrollo.'),
(gen_random_uuid(), 'Disponible Para Evaluacion', 'Se refiere a que la ficha de pérfil se encuentra disponible para ser evaluada por los representantes del comite de curriculum.');

-- Tabla de trazabilidad de estados de ficha perfil
CREATE TABLE estado_ficha_perfil (
    id UUID PRIMARY KEY,
    ficha_perfil_id UUID NOT NULL,
    estado_ficha_id UUID NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    CONSTRAINT fk_efp_traz_ficha FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE,
    CONSTRAINT fk_efp_traz_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id)
);
