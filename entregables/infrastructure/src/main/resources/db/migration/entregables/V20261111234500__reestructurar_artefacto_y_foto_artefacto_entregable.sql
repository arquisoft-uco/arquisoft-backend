-- Tabla réplica local de artefacto (dueño: contexto artefactos)
-- HT-023 — Réplica local de la última versión cerrada de cada artefacto (dueño: contexto artefactos).
-- Sustituye el modelo de HT-022, donde artefacto compartía el id de la versión: ahora artefacto.id es el id del
-- artefacto en artefactos y el id de la versión viaja en id_version_artefacto. Solo avanza con una versión mayor.
-- artefacto_entregable pasa a ser la foto inmutable de lo que quedó en el paquete de cada entregable, con sus propios
-- datos del artefacto y sin FK a artefacto.
-- Sin backfill: el proyecto no se ha desplegado y los datos locales de ambas tablas se descartan; los artefactos se
-- vuelven a replicar con los eventos de cierre y los entregables existentes se ajustan a mano.
DROP TABLE artefacto_entregable;
DROP TABLE artefacto;

CREATE TABLE artefacto (
    id                       UUID          PRIMARY KEY,
    proyecto_grado_id        UUID          NOT NULL,
    id_version_artefacto     UUID          NOT NULL,
    version_artefacto        INTEGER       NOT NULL,
    fecha_version            TIMESTAMPTZ   NOT NULL,
    clave_objeto             VARCHAR(2000) NOT NULL,
    id_repositorio_artefacto UUID          NOT NULL,
    nombre_documento         VARCHAR(100)  NOT NULL,
    id_repositorio           UUID          NOT NULL,
    nombre_repositorio       VARCHAR(100)  NOT NULL,
    omitido_en               TIMESTAMPTZ   NULL,
    omision_ocurrida_en      TIMESTAMPTZ   NULL,
    CONSTRAINT fk_artefacto_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id),
    CONSTRAINT ck_artefacto_version CHECK (version_artefacto >= 1)
);
CREATE INDEX idx_artefacto_proyecto ON artefacto (proyecto_grado_id) WHERE omitido_en IS NULL;

CREATE TABLE artefacto_entregable (
    id                       UUID          PRIMARY KEY,
    artefacto_id             UUID          NOT NULL,
    entregable_id            UUID          NOT NULL,
    uri_archivo              VARCHAR(2000) NOT NULL,
    id_version_artefacto     UUID          NOT NULL,
    version_artefacto        INTEGER       NOT NULL,
    nombre_documento         VARCHAR(100)  NOT NULL,
    id_repositorio_artefacto UUID          NOT NULL,
    id_repositorio           UUID          NOT NULL,
    nombre_repositorio       VARCHAR(100)  NOT NULL,
    CONSTRAINT fk_ae_entregable FOREIGN KEY (entregable_id) REFERENCES entregable_proyecto_grado(id),
    CONSTRAINT uk_ae_unica UNIQUE (artefacto_id, entregable_id),
    CONSTRAINT ck_ae_version CHECK (version_artefacto >= 1)
);
CREATE INDEX idx_ae_entregable ON artefacto_entregable (entregable_id);
