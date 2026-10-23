-- HU-007 — Catálogo estado_item y tabla tarea (MER 06_tablas_mapas_ruta.sql).
-- ADR-012: id VARCHAR(60) = nombre del enum EstadoItem. Sin plan_estimado ni plan_real (otras HU).
-- uk_tarea_mapadescripcion materializa Tarea-POL-02 (la descripción no se repite en el mapa).
CREATE TABLE estado_item (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_item_nombre UNIQUE (nombre)
);

INSERT INTO estado_item (id, nombre, descripcion) VALUES
    ('NUEVA',       'Nueva',       'La tarea ha sido creada, pero aún no se ha iniciado.'),
    ('EN_PROGRESO', 'En progreso', 'La tarea está en desarrollo y actualmente se está trabajando en ella.'),
    ('FINALIZADA',  'Finalizada',  'La tarea ha sido completada y aprobada, sin necesidad de más cambios.');

CREATE TABLE tarea (
    id             UUID         PRIMARY KEY,
    mapa_ruta_id   UUID         NOT NULL,
    descripcion    VARCHAR(200) NOT NULL,
    asesor_id      UUID         NOT NULL,
    estado_item_id VARCHAR(60)  NOT NULL,
    CONSTRAINT fk_tarea_maparuta FOREIGN KEY (mapa_ruta_id) REFERENCES mapa_ruta(id) ON DELETE CASCADE,
    CONSTRAINT fk_tarea_asesor FOREIGN KEY (asesor_id) REFERENCES asesor(id),
    CONSTRAINT fk_tarea_estado FOREIGN KEY (estado_item_id) REFERENCES estado_item(id),
    CONSTRAINT uk_tarea_mapadescripcion UNIQUE (mapa_ruta_id, descripcion)
);

CREATE INDEX idx_tarea_asesor ON tarea(asesor_id);
CREATE INDEX idx_tarea_estado ON tarea(estado_item_id);
