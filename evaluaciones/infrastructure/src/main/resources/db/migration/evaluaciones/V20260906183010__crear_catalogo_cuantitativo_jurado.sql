CREATE TABLE categoria_item_cuantitativo_jurado (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cat_cuant_jurado_nombre UNIQUE (nombre)
);

CREATE TABLE item_cuantitativo_jurado (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    categoria_id UUID NOT NULL,
    valor INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_cuant_cat_jurado
        FOREIGN KEY (categoria_id) REFERENCES categoria_item_cuantitativo_jurado(id),
    CONSTRAINT ck_item_cuant_jurado_valor CHECK (valor BETWEEN 0 AND 500)
);

CREATE UNIQUE INDEX uk_item_cuant_jurado_nombre_cat_ci
    ON item_cuantitativo_jurado (LOWER(nombre), categoria_id);
