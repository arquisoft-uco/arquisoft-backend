-- HT-025: ocurrido_en guarda el ocurridoEn del último evento de estado aplicado a la réplica;
-- el consumidor descarta eventos con ocurridoEn igual o anterior.
-- NULL = ningún evento aplicado: la tabla no guardaba la fecha del evento, y el primer evento posterior gana.
ALTER TABLE proyecto_grado ADD COLUMN ocurrido_en TIMESTAMPTZ NULL;
