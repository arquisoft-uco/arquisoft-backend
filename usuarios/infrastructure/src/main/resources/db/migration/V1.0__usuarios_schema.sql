-- ==================== USUARIOS - Schema ====================
-- Creado por Flyway V1.0

-- Tabla: usuarios.usuario
CREATE TABLE IF NOT EXISTS usuarios.usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT email_format CHECK (email ~* '^[A-Za-z0-9+_.-]+@(.+)$')
);

-- Tabla: usuarios.rol
CREATE TABLE IF NOT EXISTS usuarios.rol (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT rol_nombre_unique UNIQUE (nombre)
);

-- Tabla: usuarios.usuario_rol (Muchos a muchos)
CREATE TABLE IF NOT EXISTS usuarios.usuario_rol (
    usuario_id BIGINT NOT NULL REFERENCES usuarios.usuario(id) ON DELETE CASCADE,
    rol_id BIGINT NOT NULL REFERENCES usuarios.rol(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id)
);

-- Tabla: usuarios.permiso
CREATE TABLE IF NOT EXISTS usuarios.permiso (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT permiso_nombre_unique UNIQUE (nombre)
);

-- Tabla: usuarios.rol_permiso (Muchos a muchos)
CREATE TABLE IF NOT EXISTS usuarios.rol_permiso (
    rol_id BIGINT NOT NULL REFERENCES usuarios.rol(id) ON DELETE CASCADE,
    permiso_id BIGINT NOT NULL REFERENCES usuarios.permiso(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (rol_id, permiso_id)
);

-- Tabla: usuarios.credencial
CREATE TABLE IF NOT EXISTS usuarios.credencial (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuarios.usuario(id) ON DELETE CASCADE,
    password_hash VARCHAR(255) NOT NULL,
    ultimo_login TIMESTAMP,
    intento_fallidos INT DEFAULT 0,
    bloqueado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimización
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuarios.usuario(email);
CREATE INDEX IF NOT EXISTS idx_usuario_estado ON usuarios.usuario(estado);
CREATE INDEX IF NOT EXISTS idx_usuario_rol ON usuarios.usuario_rol(usuario_id);
CREATE INDEX IF NOT EXISTS idx_rol_permiso ON usuarios.rol_permiso(rol_id);
CREATE INDEX IF NOT EXISTS idx_credencial_usuario ON usuarios.credencial(usuario_id);

-- Comentarios
COMMENT ON SCHEMA usuarios IS 'Contexto de Usuarios - Gestión de usuarios, roles, permisos';
COMMENT ON TABLE usuarios.usuario IS 'Entidad principal de usuario';
COMMENT ON TABLE usuarios.rol IS 'Roles que agrupan permisos';
COMMENT ON TABLE usuarios.permiso IS 'Permisos granulares del sistema';
COMMENT ON TABLE usuarios.credencial IS 'Datos de autenticación (contraseña, intentos fallidos, etc)';
