-- Tabla réplica local de proyecto_grado (dueño: contexto proyectos). HU-152-302-AJ1: El estado pasa a ser el mismo
-- estado_proyecto_id que publica el dueño y reemplaza a finalizado_en / cancelado_en.
-- DEFAULT 'EN_PROCESO': rige las filas anteriores a que existiera cualquier cambio de estado.
-- ocurrido_en NULL = ningún evento de estado aplicado; el consumidor descarta eventos con ocurridoEn igual o anterior.
ALTER TABLE proyecto_grado ADD COLUMN estado_proyecto_id VARCHAR(60) NOT NULL DEFAULT 'EN_PROCESO';
ALTER TABLE proyecto_grado ADD COLUMN ocurrido_en TIMESTAMPTZ NULL;

-- finalizado_en y cancelado_en guardaban el ocurridoEn del evento: el backfill de ocurrido_en es exacto.
-- Los estados terminales son excluyentes: un proyecto no pasa de FINALIZADO a CANCELADO.
UPDATE proyecto_grado
   SET estado_proyecto_id = CASE WHEN cancelado_en IS NOT NULL THEN 'CANCELADO'
                                 WHEN finalizado_en IS NOT NULL THEN 'FINALIZADO'
                                 ELSE 'EN_PROCESO' END,
       ocurrido_en = GREATEST(finalizado_en, cancelado_en);

ALTER TABLE proyecto_grado DROP COLUMN finalizado_en;
ALTER TABLE proyecto_grado DROP COLUMN cancelado_en;
