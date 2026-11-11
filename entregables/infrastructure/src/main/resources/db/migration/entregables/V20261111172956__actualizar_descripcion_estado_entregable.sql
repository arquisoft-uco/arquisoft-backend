UPDATE estado_entregable SET descripcion = 'El entregable fue generado con los artefactos del proyecto y espera evaluación. El asesor aún puede cancelarlo para habilitar una entrega nueva.' WHERE id = 'DISPONIBLE_EVALUACION';
UPDATE estado_entregable SET descripcion = 'Los evaluadores están revisando el entregable; mientras tanto no se puede cancelar ni cerrar.' WHERE id = 'EN_EVALUACION';
UPDATE estado_entregable SET descripcion = 'La evaluación aprobó el entregable con observaciones por atender. Puede cerrarse como entrega definitiva del proyecto.' WHERE id = 'APROBADO_CON_OBSERVACIONES';
UPDATE estado_entregable SET descripcion = 'La evaluación aprobó el entregable sin observaciones. Puede cerrarse como entrega definitiva del proyecto.' WHERE id = 'APROBADO';
UPDATE estado_entregable SET descripcion = 'La evaluación no aprobó el entregable. Puede cerrarse como resultado definitivo o cancelarse para habilitar una entrega nueva.' WHERE id = 'RECHAZADO';
UPDATE estado_entregable SET descripcion = 'El entregable deja de ser la entrega vigente sin resultado definitivo: se vuelve a habilitar la edición de los artefactos y se puede generar una entrega nueva.' WHERE id = 'CANCELADO';
UPDATE estado_entregable SET descripcion = 'Resultado definitivo del entregable. No admite más cambios y, si es el último, permite finalizar el proyecto de grado.' WHERE id = 'CERRADO';
