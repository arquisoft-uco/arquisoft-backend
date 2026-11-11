-- HT-022 — Historial de estados del entregable (MER 08_tablas_entregables.sql): cada cambio de estado es una fila fechada;
-- el estado vigente es la fila más reciente y entregable_proyecto_grado deja de guardar el estado.
-- ON DELETE CASCADE sobre fk_eepg_entregable, como estado_proyecto_grado.
CREATE TABLE estado_entregable_proyecto_grado (
    id                           UUID        PRIMARY KEY,
    entregable_proyecto_grado_id UUID        NOT NULL,
    estado_entregable_id         VARCHAR(60) NOT NULL,
    fecha_actualizacion          TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_eepg_entregable FOREIGN KEY (entregable_proyecto_grado_id)
        REFERENCES entregable_proyecto_grado(id) ON DELETE CASCADE,
    CONSTRAINT fk_eepg_estado FOREIGN KEY (estado_entregable_id) REFERENCES estado_entregable(id),
    CONSTRAINT uk_eepg_unica UNIQUE (entregable_proyecto_grado_id, estado_entregable_id, fecha_actualizacion)
);
CREATE INDEX idx_eepg_estado ON estado_entregable_proyecto_grado (estado_entregable_id);
CREATE INDEX idx_eepg_entregable_fecha
    ON estado_entregable_proyecto_grado (entregable_proyecto_grado_id, fecha_actualizacion DESC);

-- Sin índice parcial entre tablas: la unicidad del entregable no cancelado por proyecto la dan
-- EntregableVigenteUnicoRule y el bloqueo de la fila de proyecto_grado al generar.
DROP INDEX uq_entregable_vigente_por_proyecto;
ALTER TABLE entregable_proyecto_grado DROP COLUMN estado_entregable_id;
