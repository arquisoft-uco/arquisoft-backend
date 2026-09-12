CREATE TABLE criterio_item_cualitativo_jurado (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_crit_cual_jurado_nombre UNIQUE (nombre)
);
