-- Contacto del estudiante en la réplica estudiante_proyecto_grado: vacío significa contacto aún no replicado
-- (filas anteriores a esta migración hasta que llegue un evento de proyectos más nuevo).
ALTER TABLE estudiante_proyecto_grado
    ADD COLUMN nombre VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN email VARCHAR(50) NOT NULL DEFAULT '';

-- Tabla réplica local de coordinador por proyecto (dueño: contexto proyectos)
CREATE TABLE coordinador_proyecto_grado (
    proyecto_grado_id UUID NOT NULL,
    coordinador_id UUID NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    ocurrido_en TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (proyecto_grado_id)
);
