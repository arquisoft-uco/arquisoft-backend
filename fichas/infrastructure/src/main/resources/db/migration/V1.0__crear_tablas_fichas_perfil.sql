-- Tabla del asesor (debe crearse primero por la FK en ficha_perfil)
CREATE TABLE asesor_ficha (
    id     UUID         NOT NULL,
    nombre VARCHAR(50)  NOT NULL,
    email  VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id)
);

-- Tabla principal de fichas de perfil
CREATE TABLE ficha_perfil (
    id               UUID         NOT NULL,
    titulo_proyecto  VARCHAR(100) NOT NULL,
    asesor_ficha_id  UUID         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_ficha_perfil_titulo UNIQUE (titulo_proyecto),
    CONSTRAINT fk_ficha_perfil_asesor FOREIGN KEY (asesor_ficha_id)
        REFERENCES asesor_ficha(id)
);
