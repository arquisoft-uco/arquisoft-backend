-- HU-147 — Tabla réplica local de asesor (dueño: contexto usuarios).
-- Poblada por AsesorAgregadoConsumer y AsesorRemovidoConsumer al consumir usuarios.asesor.agregado
-- y usuarios.asesor.removido. Anchos tomados de mer/08_tablas_entregables.sql.
-- ocurrido_en es metadato del mecanismo de replicación (no está en el MER) y permite descartar
-- eventos reentregados fuera de orden. eliminado_en NULL = vigente.
CREATE TABLE asesor (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    ocurrido_en   TIMESTAMPTZ  NOT NULL,
    eliminado_en  TIMESTAMPTZ  NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_asesor_vigente ON asesor (id) WHERE eliminado_en IS NULL;
