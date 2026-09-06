CREATE TABLE estado_revision (
    id          VARCHAR(50)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_revision_nombre UNIQUE (nombre)
);

INSERT INTO estado_revision (id, nombre, descripcion) VALUES
    ('NUEVA',                 'Nueva',                 'La revision ha sido creada recientemente y aún no ha sido revisada o procesada.'),
    ('VISUALIZADA',           'Visualizada',           'La revisión ha sido vista por el estudiante, pero aún no se ha tomado acción sobre ella.'),
    ('EN_PROGRESO',           'En Progreso',           'La revisión está en desarrollo o ejecución, y se están realizando las acciones necesarias para su resolución.'),
    ('CORRECCION_DISPONIBLE', 'Correccion Disponible', 'Se ha completado el trabajo, pero requiere revisión o validación. Puede implicar que se han realizado cambios o ajustes y están listos para ser evaluados.'),
    ('CERRADA',               'Cerrada',               'La revisión ha sido completada y aprobada. No requiere más modificaciones ni acciones adicionales.');

-- revision_item se creó (V20260724005914) sin FK a ningún catálogo, así que pudo acumular
-- valores de prueba que nunca fueron un estado real (ej. 'APROBADO', de pruebas manuales
-- anteriores a que este catálogo existiera). Sin este DELETE, el ALTER TABLE de abajo falla
-- en cualquier base que arrastre ese tipo de fila; en una base sin filas huérfanas no borra nada.
DELETE FROM revision_item
 WHERE estado_revision_id NOT IN (SELECT id FROM estado_revision);

ALTER TABLE revision_item
    ADD CONSTRAINT fk_rev_estado FOREIGN KEY (estado_revision_id) REFERENCES estado_revision(id);
