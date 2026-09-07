CREATE TABLE entregable_proyecto_acceso (
    entregable_id UUID NOT NULL,
    proyecto_id UUID NOT NULL,
    version_entregable INTEGER NOT NULL,
    activo BOOLEAN NOT NULL,
    ocurrido_en TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (entregable_id)
);

CREATE INDEX idx_entregable_proyecto_acceso_proyecto ON entregable_proyecto_acceso (proyecto_id);

CREATE TABLE proyecto_estudiante_acceso (
    proyecto_id UUID NOT NULL,
    estudiante_id UUID NOT NULL,
    activo BOOLEAN NOT NULL,
    ocurrido_en TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (proyecto_id, estudiante_id)
);

CREATE INDEX idx_proyecto_estudiante_acceso_estudiante ON proyecto_estudiante_acceso (estudiante_id, activo);
