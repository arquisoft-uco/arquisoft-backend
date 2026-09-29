-- HU-172 — Mapa de ruta de un proyecto de grado (MER 06_tablas_mapas_ruta.sql).
-- uk_maparuta_proyecto materializa MapaRuta-POL-02 (un mapa por proyecto), que el MER no declara;
-- el UNIQUE triple del MER se conserva. Sin idx_maparuta_proyecto: lo cubre el índice del UNIQUE.
CREATE TABLE mapa_ruta (
    id                UUID PRIMARY KEY,
    proyecto_grado_id UUID NOT NULL,
    fecha_inicio      DATE NOT NULL,
    fecha_fin         DATE NOT NULL,
    CONSTRAINT fk_maparuta_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id),
    CONSTRAINT uk_maparuta_proyecto_fechas UNIQUE (proyecto_grado_id, fecha_inicio, fecha_fin),
    CONSTRAINT uk_maparuta_proyecto UNIQUE (proyecto_grado_id)
);
