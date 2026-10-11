-- Revalidacion de HU-056 — respuesta.fecha_respuesta debe ser TIMESTAMPTZ, no TIMESTAMP, para que la
-- columna hable el mismo tipo que Instant en el dominio (RespuestaDomain) y coincida con el MER
-- (mer/11_tablas_solicitudes.sql).
--
-- V20260827144741 ya esta aplicada, asi que el cambio no se edita ahi (rompe el checksum de Flyway) —
-- va en una migracion nueva, con la misma forma que V20260913214554 para solicitud.fecha_creacion.
-- USING asume que los valores existentes se escribieron en UTC, la misma convencion que usa
-- UtilFecha.generarInstanteActual() en todo el proyecto.

ALTER TABLE respuesta
    ALTER COLUMN fecha_respuesta TYPE TIMESTAMPTZ USING fecha_respuesta AT TIME ZONE 'UTC';
