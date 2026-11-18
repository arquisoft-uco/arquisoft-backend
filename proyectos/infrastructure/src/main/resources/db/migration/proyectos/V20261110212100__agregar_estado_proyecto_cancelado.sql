-- HU-302 — Estado terminal CANCELADO del proyecto de grado: el coordinador lo da de baja de forma definitiva.
-- Fila del catálogo copiada de mer/data/07_data_proyectos_grado.sql.

INSERT INTO estado_proyecto (id, nombre, descripcion) VALUES
    ('CANCELADO', 'Cancelado', 'El coordinador dio de baja el proyecto: no continúa y sus archivos de artefactos se eliminan. Estado terminal.');
