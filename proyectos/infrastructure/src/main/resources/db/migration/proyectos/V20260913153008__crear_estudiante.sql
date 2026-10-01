-- HU-234 — Tabla réplica local de estudiante (dueño: contexto usuarios).
-- Poblada por EstudianteAgregadoProyectosConsumer al consumir usuarios.estudiante.agregado.
-- Anchos de identificador/nombre/email tomados de mer/07_tablas_proyectos_grado.sql, donde
-- estudiante ya existe como tabla de identidad referenciada por estudiante_proyecto_grado.estudiante_id.
-- ocurrido_en es metadato del mecanismo de replicación (no está en el MER) y permite descartar
-- eventos reentregados fuera de orden.

CREATE TABLE estudiante (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    ocurrido_en   TIMESTAMPTZ  NOT NULL,
    PRIMARY KEY (id)
);
