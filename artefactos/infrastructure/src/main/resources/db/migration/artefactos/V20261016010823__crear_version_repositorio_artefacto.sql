-- HU-275 — Tabla réplica local de version_repositorio_artefacto (dueño: contexto repositorio_artefactos).
-- Poblada por VersionRepositorioArtefactoSubidaConsumer. Sin uri_archivo ni hash_sha256: artefactos no
-- conoce el bucket ni las keys del dueño.
CREATE TABLE version_repositorio_artefacto (
    id UUID PRIMARY KEY,
    nombre_documento VARCHAR(100) NOT NULL,
    version_documento INTEGER NOT NULL,
    id_repositorio UUID NOT NULL,
    nombre_repositorio VARCHAR(100) NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_contenido VARCHAR(100) NOT NULL,
    tamano_bytes BIGINT NOT NULL
);
