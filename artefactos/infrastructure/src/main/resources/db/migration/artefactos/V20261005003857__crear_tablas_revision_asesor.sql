CREATE TABLE estado_revision_asesor (
    id VARCHAR(60) PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_revision_asesor_nombre UNIQUE (nombre)
);

INSERT INTO estado_revision_asesor (id, nombre, descripcion) VALUES
    ('PENDIENTE',    'Pendiente',    'Nueva revisión elaborada por el asesor'),
    ('EN_PROGRESO',  'En progreso',  'Indica que el estudiante ha iniciado los ajustes para pasar la revisión'),
    ('RESUELTA',     'Resuelta',     'Indica que el estudiante ya finalizó los ajustes'),
    ('CERRADA',      'Cerrada',      'Indica que el asesor cerró la revisión');

-- Tabla réplica local de asesor (dueño: contexto usuarios).
CREATE TABLE asesor (
    id UUID PRIMARY KEY,
    identificador VARCHAR(30) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    eliminado_en TIMESTAMPTZ NULL
);

-- Tabla réplica local de estudiante (dueño: contexto usuarios).
CREATE TABLE estudiante (
    id UUID PRIMARY KEY,
    identificador VARCHAR(30) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    eliminado_en TIMESTAMPTZ NULL
);

-- Tabla réplica local de proyecto_grado (dueño: contexto proyectos).
CREATE TABLE proyecto_grado (
    id UUID PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL
);

-- Tabla réplica local de version_repositorio_artefacto (dueño: contexto repositorio_artefactos).
CREATE TABLE version_repositorio_artefacto (
    id UUID PRIMARY KEY,
    nombre_documento VARCHAR(100) NOT NULL,
    version_documento INTEGER NOT NULL,
    id_repositorio UUID NOT NULL,
    nombre_repositorio VARCHAR(100) NOT NULL
);

CREATE TABLE artefacto (
    id UUID PRIMARY KEY,
    proyecto_grado_id UUID NOT NULL,
    version_repositorio_artefacto_id UUID NOT NULL,
    asesor_id UUID NOT NULL,
    CONSTRAINT fk_artefacto_proyecto FOREIGN KEY (proyecto_grado_id)
        REFERENCES proyecto_grado(id),
    CONSTRAINT fk_artefacto_version_repo FOREIGN KEY (version_repositorio_artefacto_id)
        REFERENCES version_repositorio_artefacto(id),
    CONSTRAINT fk_artefacto_asesor FOREIGN KEY (asesor_id)
        REFERENCES asesor(id),
    CONSTRAINT uk_artefacto_proyecto_version_repo UNIQUE (proyecto_grado_id, version_repositorio_artefacto_id)
);

CREATE TABLE estudiante_artefacto (
    id UUID PRIMARY KEY,
    artefacto_id UUID NOT NULL,
    estudiante_id UUID NOT NULL,
    CONSTRAINT fk_ea_artefacto FOREIGN KEY (artefacto_id)
        REFERENCES artefacto(id) ON DELETE CASCADE,
    CONSTRAINT fk_ea_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiante(id),
    CONSTRAINT uk_ea_artefacto_estudiante UNIQUE (artefacto_id, estudiante_id)
);

CREATE TABLE version_artefacto (
    id UUID PRIMARY KEY,
    artefacto_id UUID NOT NULL,
    version_artefacto INTEGER NOT NULL,
    fecha_creacion TIMESTAMPTZ NOT NULL,
    uri_archivo VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_va_artefacto FOREIGN KEY (artefacto_id)
        REFERENCES artefacto(id) ON DELETE CASCADE,
    CONSTRAINT uk_va_artefacto_version UNIQUE (artefacto_id, version_artefacto),
    CONSTRAINT uk_va_artefacto_uri UNIQUE (artefacto_id, uri_archivo)
);

CREATE TABLE revision_asesor (
    id UUID PRIMARY KEY,
    version_artefacto_id UUID NOT NULL,
    estado_revision_asesor_id VARCHAR(60) NOT NULL,
    CONSTRAINT fk_ra_version_artefacto FOREIGN KEY (version_artefacto_id)
        REFERENCES version_artefacto(id) ON DELETE CASCADE,
    CONSTRAINT fk_ra_estado FOREIGN KEY (estado_revision_asesor_id)
        REFERENCES estado_revision_asesor(id),
    CONSTRAINT uk_ra_version_artefacto UNIQUE (version_artefacto_id)
);

CREATE INDEX idx_artefacto_proyecto ON artefacto(proyecto_grado_id);
CREATE INDEX idx_artefacto_asesor ON artefacto(asesor_id);
CREATE INDEX idx_version_artefacto_ref ON version_artefacto(artefacto_id);
CREATE INDEX idx_revision_asesor_version ON revision_asesor(version_artefacto_id);
