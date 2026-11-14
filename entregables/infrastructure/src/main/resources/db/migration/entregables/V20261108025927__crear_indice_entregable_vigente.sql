-- HU-152 — Un solo entregable no cancelado por proyecto de grado.
-- EntregableVigenteUnicoRule da el mensaje al usuario; este índice es la garantía real ante concurrencia.
-- Falla si una base de desarrollo ya tiene dos entregables vivos del mismo proyecto (no hay datos en producción).

CREATE UNIQUE INDEX uq_entregable_vigente_por_proyecto
    ON entregable_proyecto_grado (proyecto_grado_id)
    WHERE estado_entregable_id <> 'CANCELADO';
