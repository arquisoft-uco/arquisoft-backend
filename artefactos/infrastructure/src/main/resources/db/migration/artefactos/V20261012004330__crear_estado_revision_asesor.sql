-- La columna orden no esta en el MER (mer/04_tablas_artefactos.sql): fija el orden del ciclo de vida
-- de la revision, porque ni el id ni el nombre lo reflejan alfabeticamente.
CREATE TABLE estado_revision_asesor (
    id          VARCHAR(60)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    orden       INTEGER      NOT NULL,
    CONSTRAINT uk_estado_revision_asesor_nombre UNIQUE (nombre),
    CONSTRAINT uk_estado_revision_asesor_orden UNIQUE (orden)
);

INSERT INTO estado_revision_asesor (id, nombre, descripcion, orden) VALUES
    ('PENDIENTE', 'Pendiente', 'Nueva revisión elaborada por el asesor', 1),
    ('EN_PROGRESO', 'En progreso', 'Indica que el estudiante ha iniciado los ajustes para pasar la revisión', 2),
    ('RESUELTA', 'Resuelta', 'Indica que el estudiante ya finalizó los ajustes', 3),
    ('CERRADA', 'Cerrada', 'Indica que el asesor cerró la revisión', 4);
