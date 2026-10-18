-- Tabla réplica local de administrador (dueño: contexto usuarios). Poblada por AdministradorAgregadoConsumer; ocurrido_en es metadato de replicación y no está en el MER

CREATE TABLE administrador (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    ocurrido_en   TIMESTAMPTZ  NOT NULL,
    eliminado_en  TIMESTAMPTZ  NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_administrador_vigente ON administrador (id) WHERE eliminado_en IS NULL;
