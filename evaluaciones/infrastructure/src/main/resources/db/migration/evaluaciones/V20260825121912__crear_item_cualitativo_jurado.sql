CREATE TABLE item_cualitativo_jurado (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_item_cual_jurado_nombre_ci
    ON item_cualitativo_jurado (LOWER(nombre));
