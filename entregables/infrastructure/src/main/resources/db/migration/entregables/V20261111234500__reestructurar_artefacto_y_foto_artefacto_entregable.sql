-- Tabla réplica local de artefacto (dueño: contexto artefactos)
-- HT-023 — Réplica local de la última versión cerrada de cada artefacto (dueño: contexto artefactos).
-- Sustituye el modelo de HT-022, donde artefacto compartía el id de la versión: ahora artefacto.id es el id del
-- artefacto en artefactos y el id de la versión viaja en id_version_artefacto. Solo avanza con una versión mayor.
-- Una fila sin versión (las ocho columnas de versión en NULL) es una lápida: la omisión o la reactivación del artefacto
-- llegó antes de su primer cierre de revisión, y el cierre posterior la completa sin tocar la omisión.
-- artefacto_entregable pasa a ser la foto inmutable de lo que quedó en el paquete de cada entregable, con sus propios
-- datos del artefacto y sin FK a artefacto.
-- Sin backfill: el proyecto no se ha desplegado y los datos locales de ambas tablas se descartan; los artefactos se
-- vuelven a replicar con los eventos de cierre y los entregables existentes se ajustan a mano.
DROP TABLE artefacto_entregable;
DROP TABLE artefacto;

CREATE TABLE artefacto (
    id                       UUID          PRIMARY KEY,
    proyecto_grado_id        UUID          NOT NULL,
    id_version_artefacto     UUID          NULL,
    version_artefacto        INTEGER       NULL,
    fecha_version            TIMESTAMPTZ   NULL,
    clave_objeto             VARCHAR(2000) NULL,
    id_repositorio_artefacto UUID          NULL,
    nombre_documento         VARCHAR(100)  NULL,
    id_repositorio           UUID          NULL,
    nombre_repositorio       VARCHAR(100)  NULL,
    omitido_en               TIMESTAMPTZ   NULL,
    omision_ocurrida_en      TIMESTAMPTZ   NULL,
    CONSTRAINT fk_artefacto_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id),
    CONSTRAINT ck_artefacto_version CHECK (version_artefacto >= 1),
    CONSTRAINT ck_artefacto_lapida CHECK (
        (id_version_artefacto IS NULL AND version_artefacto IS NULL AND fecha_version IS NULL
            AND clave_objeto IS NULL AND id_repositorio_artefacto IS NULL AND nombre_documento IS NULL
            AND id_repositorio IS NULL AND nombre_repositorio IS NULL)
        OR (id_version_artefacto IS NOT NULL AND version_artefacto IS NOT NULL AND fecha_version IS NOT NULL
            AND clave_objeto IS NOT NULL AND id_repositorio_artefacto IS NOT NULL AND nombre_documento IS NOT NULL
            AND id_repositorio IS NOT NULL AND nombre_repositorio IS NOT NULL))
);
CREATE INDEX idx_artefacto_proyecto ON artefacto (proyecto_grado_id);

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
