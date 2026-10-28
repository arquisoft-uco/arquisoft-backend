-- HU-001 — Tabla plan_estimado (MER 06_tablas_mapas_ruta.sql).
-- uk_pe_tarea materializa un único plan estimado por tarea y ya indexa tarea_id.
-- ON DELETE CASCADE: remover una tarea (HU-011) arrastra su plan estimado.
CREATE TABLE plan_estimado (
    id                   UUID         PRIMARY KEY,
    tarea_id             UUID         NOT NULL,
    tiempo_estimado_dias INTEGER      NOT NULL,
    descripcion_estimado VARCHAR(200),
    CONSTRAINT fk_pe_tarea FOREIGN KEY (tarea_id) REFERENCES tarea(id) ON DELETE CASCADE,
    CONSTRAINT uk_pe_tarea UNIQUE (tarea_id)
);
