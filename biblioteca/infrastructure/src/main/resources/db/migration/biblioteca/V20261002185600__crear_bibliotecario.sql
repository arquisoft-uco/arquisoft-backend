-- HU-240 — Tabla réplica local de bibliotecario (dueño: contexto usuarios).
-- Poblada por BibliotecarioAgregadoConsumer al consumir usuarios.bibliotecario.agregado.
-- Anchos de identificador/nombre/email y eliminado_en tomados de mer/10_tablas_biblioteca.sql.
-- ocurrido_en es metadato del mecanismo de replicación (no está en el MER) y permite descartar
-- eventos reentregados fuera de orden.

CREATE TABLE bibliotecario (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    ocurrido_en   TIMESTAMPTZ  NOT NULL,
    eliminado_en  TIMESTAMPTZ  NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_bibliotecario_vigente ON bibliotecario (id) WHERE eliminado_en IS NULL;
