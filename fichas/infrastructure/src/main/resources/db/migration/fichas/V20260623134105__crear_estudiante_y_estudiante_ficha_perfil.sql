-- Tabla réplica local de estudiante (vista materializada del contexto usuarios)
CREATE TABLE estudiante (
    id UUID PRIMARY KEY,
    identificador VARCHAR(30) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL
);

CREATE INDEX idx_estudiante_id ON estudiante(id);

-- Tabla de relación estudiante-ficha de perfil
CREATE TABLE estudiante_ficha_perfil (
    id UUID PRIMARY KEY,
    ficha_perfil_id UUID NOT NULL,
    estudiante_id UUID NOT NULL,
    CONSTRAINT fk_estudiante_ficha_perfil_ficha
        FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE,
    CONSTRAINT fk_estudiante_ficha_perfil_estudiante
        FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE,
    CONSTRAINT uq_ficha_estudiante UNIQUE (ficha_perfil_id, estudiante_id)
);

CREATE INDEX idx_estudiante_ficha_perfil_ficha ON estudiante_ficha_perfil(ficha_perfil_id);
CREATE INDEX idx_estudiante_ficha_perfil_estudiante ON estudiante_ficha_perfil(estudiante_id);
