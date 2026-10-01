-- Recomendacion de revision del PR #87 — fecha_creacion debe ser TIMESTAMPTZ, no TIMESTAMP,
-- para que la columna hable el mismo tipo que Instant en el dominio (SolicitudDomain).
--
-- V20260827144741 ya estaba aplicada cuando se detecto esto, asi que el cambio no se edito ahi
-- (eso rompe el checksum de Flyway para quien ya la tenia aplicada) — va en una migracion nueva.
-- USING asume que los valores existentes se escribieron en UTC, la misma convencion que usa
-- UtilFecha.generarInstanteActual() en todo el proyecto.

ALTER TABLE solicitud
    ALTER COLUMN fecha_creacion TYPE TIMESTAMPTZ USING fecha_creacion AT TIME ZONE 'UTC';
