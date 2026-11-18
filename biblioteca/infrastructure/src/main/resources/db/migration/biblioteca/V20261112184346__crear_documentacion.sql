-- HU-177 — Tablas de documentacion (MER 10_tablas_biblioteca.sql, secciones 2, 3 y 4).
-- entregable_proyecto_grado es réplica local del contexto entregables y autor del contexto usuarios:
-- ninguna lleva FK hacia otra base. Quién las puebla queda fuera de esta migración.
-- estado_documentacion y bibliotecario ya existen (V20261112164955 y V20261002185600).

-- Tabla réplica local de entregable_proyecto_grado (dueño: contexto entregables)
CREATE TABLE entregable_proyecto_grado (
    id UUID PRIMARY KEY,
    proyecto_grado_id UUID NOT NULL,
    version_entregable INTEGER NOT NULL,
    asesor_id UUID NOT NULL,
    estado_entregable_nombre VARCHAR(60) NOT NULL,
    CONSTRAINT uk_epg_proyecto_version UNIQUE (proyecto_grado_id, version_entregable)
);
CREATE INDEX idx_epg_proyecto ON entregable_proyecto_grado(proyecto_grado_id);

-- Tabla réplica local de autor (dueño: contexto usuarios)
CREATE TABLE autor (
    id UUID PRIMARY KEY,
    identificador VARCHAR(30) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    eliminado_en TIMESTAMPTZ NULL
);

CREATE TABLE documentacion (
    id UUID PRIMARY KEY,
    url VARCHAR(2000) NOT NULL,
    fecha_publicacion TIMESTAMPTZ NOT NULL,
    entregable_proyecto_grado_id UUID NOT NULL,
    bibliotecario_id UUID NOT NULL,
    estado_documentacion_id VARCHAR(60) NOT NULL,
    CONSTRAINT fk_doc_entregable FOREIGN KEY (entregable_proyecto_grado_id)
        REFERENCES entregable_proyecto_grado(id),
    CONSTRAINT fk_doc_bibliotecario FOREIGN KEY (bibliotecario_id)
        REFERENCES bibliotecario(id),
    CONSTRAINT fk_doc_estado FOREIGN KEY (estado_documentacion_id)
        REFERENCES estado_documentacion(id),
    CONSTRAINT uk_doc_entregable UNIQUE (entregable_proyecto_grado_id),
    CONSTRAINT uk_doc_url UNIQUE (url)
);
CREATE INDEX idx_doc_bibliotecario ON documentacion(bibliotecario_id);
CREATE INDEX idx_doc_estado ON documentacion(estado_documentacion_id);

CREATE TABLE autor_documentacion (
    id UUID PRIMARY KEY,
    autor_id UUID NOT NULL,
    documentacion_id UUID NOT NULL,
    CONSTRAINT fk_ad_autor FOREIGN KEY (autor_id)
        REFERENCES autor(id),
    CONSTRAINT fk_ad_documentacion FOREIGN KEY (documentacion_id)
        REFERENCES documentacion(id) ON DELETE CASCADE,
    CONSTRAINT uk_ad_autor_documentacion UNIQUE (autor_id, documentacion_id)
);
CREATE INDEX idx_ad_documentacion ON autor_documentacion(documentacion_id);
CREATE INDEX idx_ad_autor ON autor_documentacion(autor_id);
