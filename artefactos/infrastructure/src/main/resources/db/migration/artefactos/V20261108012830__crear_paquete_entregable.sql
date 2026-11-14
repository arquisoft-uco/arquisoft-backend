-- HU-147-AJ1 — Registro propio del paquete ZIP congelado de un entregable de proyecto de grado.
-- No está en el MER. El paquete no cuenta para la cuota de HT-020 (que suma solo version_artefacto.tamano_bytes).
-- entregable_proyecto_grado_id no lleva FK: el entregable es de otro contexto (entregables).
CREATE TABLE paquete_entregable (
    id                           UUID          PRIMARY KEY,
    entregable_proyecto_grado_id UUID          NOT NULL,
    proyecto_grado_id            UUID          NOT NULL,
    version_entregable           INTEGER       NOT NULL,
    clave_objeto                 VARCHAR(2000) NOT NULL,
    tamano_bytes                 BIGINT        NOT NULL,
    generado_en                  TIMESTAMPTZ   NOT NULL,
    CONSTRAINT fk_pe_proyecto FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id),
    CONSTRAINT uk_pe_entregable UNIQUE (entregable_proyecto_grado_id),
    CONSTRAINT ck_pe_version CHECK (version_entregable >= 1),
    CONSTRAINT ck_pe_tamano CHECK (tamano_bytes > 0)
);

CREATE INDEX idx_pe_proyecto ON paquete_entregable (proyecto_grado_id, version_entregable);
