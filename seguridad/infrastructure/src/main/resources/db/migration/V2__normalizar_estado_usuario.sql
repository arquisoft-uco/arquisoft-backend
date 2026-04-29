SET search_path TO usuarios;

-- 1. Catálogo de estados
CREATE TABLE estado_usuario (
    id          UUID        PRIMARY KEY,
    nombre      VARCHAR(20) NOT NULL,
    descripcion VARCHAR(200),
    CONSTRAINT uk_estado_usuario_nombre UNIQUE (nombre)
);

-- 2. Datos iniciales
INSERT INTO estado_usuario (id, nombre, descripcion) VALUES
    (gen_random_uuid(), 'ACTIVO',   'Usuario habilitado para operar en el sistema'),
    (gen_random_uuid(), 'INACTIVO', 'Usuario deshabilitado; no puede iniciar sesión');

-- 3. Nueva columna FK (nullable para poder migrar datos primero)
ALTER TABLE usuario ADD COLUMN estado_id UUID;

-- 4. Poblar estado_id desde el VARCHAR existente
UPDATE usuario u
SET estado_id = eu.id
FROM estado_usuario eu
WHERE eu.nombre = u.estado;

-- 5. NOT NULL ahora que todos los registros tienen FK
ALTER TABLE usuario ALTER COLUMN estado_id SET NOT NULL;

-- 6. FK constraint
ALTER TABLE usuario
    ADD CONSTRAINT fk_usuario_estado
    FOREIGN KEY (estado_id) REFERENCES estado_usuario(id);

-- 7. Eliminar columna y constraint obsoletos
ALTER TABLE usuario DROP CONSTRAINT ck_usuario_estado;
ALTER TABLE usuario DROP COLUMN estado;

-- 8. Índice sobre la FK
CREATE INDEX idx_usuario_estado ON usuario(estado_id);
