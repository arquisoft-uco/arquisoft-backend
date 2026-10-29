-- HU-150 — Catálogo estado_entregable (MER 08_tablas_entregables.sql).
-- ADR-012: id VARCHAR(60) = nombre del enum EstadoEntregable.
CREATE TABLE estado_entregable (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_entregable_nombre UNIQUE (nombre)
);

INSERT INTO estado_entregable (id, nombre, descripcion) VALUES
    ('DISPONIBLE_EVALUACION',      'Disponible Evaluacion',      'Indica que un entregable ha sido generado y está disponible para su evaluación'),
    ('EN_EVALUACION',              'En Evaluacion',              'Indica que un entregable está siendo evaluado'),
    ('APROBADO_CON_OBSERVACIONES', 'Aprobado Con Observaciones', 'Indica que un entregable fue aprobado en su evaluación pero presenta observaciones'),
    ('APROBADO',                   'Aprobado',                   'Indica que un entregable fue aprobado completamente en su evaluación'),
    ('RECHAZADO',                  'Rechazado',                  'Indica que un entregable fue rechazado en su evaluación'),
    ('CANCELADO',                  'Cancelado',                  'Indica que un entregable ha sido cancelado por lo que no se le debería hacer una evaluación'),
    ('CERRADO',                    'Cerrado',                    'Indica que un entregable fue aprobado y se continuará con la siguiente etapa del proceso');
