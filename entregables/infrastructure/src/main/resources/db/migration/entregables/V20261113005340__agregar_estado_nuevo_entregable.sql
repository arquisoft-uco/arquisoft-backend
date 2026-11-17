INSERT INTO estado_entregable (id, nombre, descripcion) VALUES
    ('NUEVO', 'Nuevo', 'El entregable fue generado con los artefactos del proyecto y aún no está disponible para evaluación. El asesor puede cancelarlo o habilitarlo para evaluación.');

UPDATE estado_entregable SET descripcion = 'El asesor habilitó el entregable para su evaluación; ya no puede cancelarse y los evaluadores pueden consultarlo.' WHERE id = 'DISPONIBLE_EVALUACION';
