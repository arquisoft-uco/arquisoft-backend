-- Tabla réplica local de proyecto_grado (dueño: contexto proyectos). HU-152-302-AJ1: ocurrido_en guarda el ocurridoEn
-- del último evento de estado aplicado; el consumidor descarta eventos con ocurridoEn igual o anterior.
-- NULL = ningún evento aplicado: la tabla no guardaba la fecha del evento, así que no hay nada que reconstruir
-- y el primer evento posterior gana. estado_proyecto_id ya existe.
ALTER TABLE proyecto_grado ADD COLUMN ocurrido_en TIMESTAMPTZ NULL;
