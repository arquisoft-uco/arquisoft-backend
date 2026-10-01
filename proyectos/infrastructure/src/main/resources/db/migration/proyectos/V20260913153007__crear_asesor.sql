-- HU-234 — Tabla réplica local de asesor (dueño: contexto usuarios).
-- Poblada por AsesorAgregadoProyectosConsumer al consumir usuarios.asesor.agregado.
-- Anchos de identificador/nombre/email tomados de mer/07_tablas_proyectos_grado.sql, donde
-- asesor ya existe como tabla de identidad referenciada por asesor_proyecto_grado.asesor_id.
-- ocurrido_en es metadato del mecanismo de replicación (no está en el MER) y permite descartar
-- eventos reentregados fuera de orden.

CREATE TABLE asesor (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    ocurrido_en   TIMESTAMPTZ  NOT NULL,
    PRIMARY KEY (id)
);
