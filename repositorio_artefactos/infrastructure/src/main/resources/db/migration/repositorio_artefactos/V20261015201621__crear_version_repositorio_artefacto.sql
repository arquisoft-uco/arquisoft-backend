-- HU-275: versiones de plantillas de un repositorio de artefactos. Las cinco columnas de archivo
-- (uri_archivo, tipo_contenido, tamano_bytes, nombre_archivo, hash_sha256) extienden el MER (D-09) y
-- aun no estan en mer/05_tablas_repositorio_artefactos.sql de arquisoft-docs.
-- uri_archivo es la key relativa al bucket (versiones/{id}); hash_sha256 es opcional en el MER.

CREATE TABLE version_repositorio_artefacto (
    id                       UUID          NOT NULL,
    nombre_documento         VARCHAR(100)  NOT NULL,
    numero_version           INTEGER       NOT NULL,
    fecha_creacion           TIMESTAMPTZ   NOT NULL,
    repositorio_artefacto_id UUID          NOT NULL,
    uri_archivo              VARCHAR(2000) NOT NULL,
    tipo_contenido           VARCHAR(100)  NOT NULL,
    tamano_bytes             BIGINT        NOT NULL,
    nombre_archivo           VARCHAR(255)  NOT NULL,
    hash_sha256              VARCHAR(64)   NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_vra_repositorio FOREIGN KEY (repositorio_artefacto_id)
        REFERENCES repositorio_artefacto (id) ON DELETE CASCADE,
    CONSTRAINT uk_vra_documento_version_repo UNIQUE (nombre_documento, numero_version, repositorio_artefacto_id)
);

CREATE INDEX idx_vra_repositorio ON version_repositorio_artefacto (repositorio_artefacto_id);
