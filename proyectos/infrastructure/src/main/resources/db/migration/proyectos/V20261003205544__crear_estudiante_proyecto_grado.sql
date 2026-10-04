-- HU-184 — Vínculo de estudiantes con el proyecto de grado, copiado de mer/07_tablas_proyectos_grado.sql.
-- El ON DELETE CASCADE hacia estudiante no se dispara: la réplica borra de forma lógica (eliminado_en).

CREATE TABLE estudiante_proyecto_grado (
    id                UUID PRIMARY KEY,
    estudiante_id     UUID NOT NULL,
    proyecto_grado_id UUID NOT NULL,
    CONSTRAINT fk_epg_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE,
    CONSTRAINT fk_epg_proyecto   FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id) ON DELETE CASCADE,
    CONSTRAINT uk_epg_unica      UNIQUE (estudiante_id, proyecto_grado_id)
);

CREATE INDEX idx_epg_estudiante ON estudiante_proyecto_grado(estudiante_id);
