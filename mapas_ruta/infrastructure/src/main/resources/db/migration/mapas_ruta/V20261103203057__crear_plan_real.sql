-- HU-053 — Tabla plan_real (MER 06_tablas_mapas_ruta.sql). La crea esta HU porque HU-004, que la escribe, aún no existe.
-- uk_pr_tarea materializa un único plan real por tarea y ya indexa tarea_id.
-- ON DELETE CASCADE: remover una tarea arrastra su plan real.
CREATE TABLE plan_real (
    id               UUID         PRIMARY KEY,
    tarea_id         UUID         NOT NULL,
    tiempo_real_dias INTEGER      NOT NULL,
    descripcion_real VARCHAR(200),
    CONSTRAINT fk_pr_tarea FOREIGN KEY (tarea_id) REFERENCES tarea(id) ON DELETE CASCADE,
    CONSTRAINT uk_pr_tarea UNIQUE (tarea_id)
);
