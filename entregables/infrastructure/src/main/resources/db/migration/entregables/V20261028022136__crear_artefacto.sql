-- HU-146 — Artefacto (MER 08_tablas_entregables.sql).
-- Ningún contexto puebla aún esta tabla ni artefacto_entregable: la consulta responde lista vacía hasta que exista un productor.
CREATE TABLE artefacto (
    id                       UUID          PRIMARY KEY,
    uri_archivo              VARCHAR(2000) NOT NULL,
    id_artefacto             UUID          NOT NULL,
    id_repositorio_artefacto UUID          NOT NULL,
    nombre_documento         VARCHAR(100)  NOT NULL,
    id_repositorio           UUID          NOT NULL,
    nombre_repositorio       VARCHAR(100)  NOT NULL
);
CREATE INDEX idx_artefacto_nombre_doc ON artefacto (nombre_documento);
