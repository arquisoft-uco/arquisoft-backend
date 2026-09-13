-- HU-243 — Tabla réplica local de coordinador (dueño: contexto usuarios).
-- Poblada por CoordinadorAgregadoConsumer al consumir usuarios.coordinador.agregado.
-- ocurrido_en incluido desde la creación (a diferencia de asesor_ficha, que lo agregó en una
-- migración posterior) porque esta tabla es nueva y no arrastra filas previas sin ese dato.

CREATE TABLE coordinador (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    ocurrido_en   TIMESTAMPTZ  NOT NULL,
    PRIMARY KEY (id)
);
