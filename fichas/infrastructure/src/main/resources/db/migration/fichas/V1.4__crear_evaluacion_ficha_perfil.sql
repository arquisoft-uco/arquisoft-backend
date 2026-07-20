-- ============================================================================
-- V1.4 — Crear tablas para evaluación de fichas de perfil
-- ============================================================================
-- Autor: Agente Implementador
-- Fecha: 2026-07-06
-- Contexto: fichas
-- Base de datos: fichas_perfil
-- HU: HU-190 — Registrar nueva evaluación de ficha de perfil
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tabla: representante_comite_curriculum
-- Descripción: Réplica local de representantes del comité de currículum.
--              Se puebla vía eventos AMQP del contexto usuarios.
-- ----------------------------------------------------------------------------
CREATE TABLE representante_comite_curriculum (
    id            UUID PRIMARY KEY,
    identificador VARCHAR(30) NOT NULL,
    nombre        VARCHAR(50) NOT NULL,
    email         VARCHAR(50) NOT NULL
);

-- ----------------------------------------------------------------------------
-- Tabla: evaluacion_ficha_perfil
-- Descripción: Registros de evaluaciones de fichas de perfil por representantes
--              del comité de currículum.
-- Reglas:
--   - Un representante NO puede tener dos evaluaciones para la misma ficha
--     (UNIQUE constraint en representante_comite_id + ficha_perfil_id).
--   - Si se elimina la ficha, se eliminan sus evaluaciones (ON DELETE CASCADE).
-- ----------------------------------------------------------------------------
CREATE TABLE evaluacion_ficha_perfil (
    id                     UUID PRIMARY KEY,
    representante_comite_id UUID NOT NULL,
    ficha_perfil_id        UUID NOT NULL,
    fecha_creacion         TIMESTAMP NOT NULL,

    CONSTRAINT fk_evaluacion_representante
        FOREIGN KEY (representante_comite_id)
        REFERENCES representante_comite_curriculum(id),

    CONSTRAINT fk_evaluacion_ficha
        FOREIGN KEY (ficha_perfil_id)
        REFERENCES ficha_perfil(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_representante_ficha
        UNIQUE (representante_comite_id, ficha_perfil_id)
);
