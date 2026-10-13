CREATE TABLE repositorio_artefacto (
    id                 UUID         NOT NULL,
    nombre_repositorio VARCHAR(100) NOT NULL,
    descripcion        VARCHAR(300) NOT NULL,
    administrador_id   UUID         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_repo_administrador FOREIGN KEY (administrador_id) REFERENCES administrador (id),
    CONSTRAINT uk_repo_nombre UNIQUE (nombre_repositorio)
);

CREATE INDEX idx_repo_administrador ON repositorio_artefacto (administrador_id);
