-- Tabla réplica local de paquete_entregable (dueño: contexto artefactos).
-- Poblada por PaqueteEntregableGeneradoConsumer al consumir artefactos.paquete_entregable.generado.
-- HU-147-AJ1 — no está en el MER. Sin ON DELETE CASCADE: las réplicas no se borran y el paquete no se elimina.
CREATE TABLE paquete_entregable (
    id                           UUID          PRIMARY KEY,
    entregable_proyecto_grado_id UUID          NOT NULL,
    bucket                       VARCHAR(63)   NOT NULL,
    clave_objeto                 VARCHAR(2000) NOT NULL,
    nombre_archivo               VARCHAR(255)  NOT NULL,
    tamano_bytes                 BIGINT        NOT NULL,
    generado_en                  TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_paq_entregable FOREIGN KEY (entregable_proyecto_grado_id)
        REFERENCES entregable_proyecto_grado(id),
    CONSTRAINT uk_paq_entregable UNIQUE (entregable_proyecto_grado_id),
    CONSTRAINT ck_paq_tamano CHECK (tamano_bytes > 0)
);
