-- HU-148-AJ5: contacto del estudiante en la réplica estudiante_proyecto_grado (dueño: contexto proyectos), para el
-- correo de cambio de estado del entregable. NULL = contacto aún no replicado: las filas anteriores lo quedan hasta que
-- llegue un evento de proyectos más nuevo (sin backfill) y la consulta de destinatarios las excluye.
ALTER TABLE estudiante_proyecto_grado
    ADD COLUMN nombre VARCHAR(50) NULL,
    ADD COLUMN email VARCHAR(50) NULL;
