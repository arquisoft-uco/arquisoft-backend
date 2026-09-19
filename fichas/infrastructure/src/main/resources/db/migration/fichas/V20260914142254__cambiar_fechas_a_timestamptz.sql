-- Las cuatro columnas se mapean a Instant y se crearon como TIMESTAMP: pasan a TIMESTAMPTZ para que
-- la columna guarde un instante absoluto, igual que ocurrido_en en asesor_ficha y estudiante.
--
-- Las migraciones originales ya estaban aplicadas, por eso el cambio va en una migracion nueva.
-- USING asume que los valores existentes se escribieron en UTC, la convencion con la que Hibernate
-- persiste un Instant sobre una columna TIMESTAMP. Los indices y constraints que incluyen estas
-- columnas los reconstruye PostgreSQL en el mismo ALTER.

ALTER TABLE estado_ficha_perfil
    ALTER COLUMN fecha_actualizacion TYPE TIMESTAMPTZ USING fecha_actualizacion AT TIME ZONE 'UTC';

ALTER TABLE estado_evaluacion_ficha
    ALTER COLUMN fecha_actualizacion TYPE TIMESTAMPTZ USING fecha_actualizacion AT TIME ZONE 'UTC';

ALTER TABLE evaluacion_ficha_perfil
    ALTER COLUMN fecha_creacion TYPE TIMESTAMPTZ USING fecha_creacion AT TIME ZONE 'UTC';

ALTER TABLE revision_item
    ALTER COLUMN fecha_creacion TYPE TIMESTAMPTZ USING fecha_creacion AT TIME ZONE 'UTC';
