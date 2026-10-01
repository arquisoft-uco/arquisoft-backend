-- HU-243 — Tabla réplica local de coordinador (dueño: contexto usuarios).
-- Poblada por CoordinadorAgregadoConsumer al consumir usuarios.coordinador.agregado.
-- Anchos de identificador/nombre/email tomados de mer/07_tablas_proyectos_grado.sql, donde
-- coordinador ya existe como tabla de identidad referenciada por proyecto_grado.coordinador_id.
-- ocurrido_en es metadato del mecanismo de replicación (no está en el MER) y permite descartar
-- eventos reentregados fuera de orden.

CREATE TABLE coordinador (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    ocurrido_en   TIMESTAMPTZ  NOT NULL,
    PRIMARY KEY (id)
);
