CREATE TABLE categoria_item_cuantitativo_asesor (
    id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cat_cuant_asesor_nombre UNIQUE (nombre)
);
