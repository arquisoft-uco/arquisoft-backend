-- ============================================================================
-- V1.7 — Agregar constraints de unicidad faltantes respecto al MER
-- ============================================================================
-- Contexto: fichas | Base de datos: fichas_perfil
--
-- Motivo: varias reglas de unicidad del MER quedaron sin materializar en las
-- migraciones V1.0–V1.6. Aunque los use cases hacen el chequeo previo
-- (existsPorFichaYTipoItem, existsByEvaluacionAndEstado), ese patrón
-- consultar-luego-insertar es TOCTOU: bajo READ COMMITTED una fila inexistente
-- no se puede bloquear, así que dos peticiones concurrentes pasan ambas la
-- validación y ambas insertan. El chequeo en el use case da el 422 legible;
-- la garantía de integridad es esta constraint.
--
-- NOTA: si la base ya contiene duplicados, estos ALTER fallan y la migración
-- aborta. Es intencional — hay que depurar los datos a mano antes de migrar,
-- no borrarlos silenciosamente desde aquí.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. estado_evaluacion — uk_estado_evaluacion_nombre
--    Omitida en V1.5. Los otros dos catálogos (estado_ficha, tipo_item) sí la
--    tienen; esto solo empareja el catálogo faltante.
-- ----------------------------------------------------------------------------
ALTER TABLE estado_evaluacion
    ADD CONSTRAINT uk_estado_evaluacion_nombre UNIQUE (nombre);

-- ----------------------------------------------------------------------------
-- 2. item — uk_item_ficha
--    Una ficha de perfil no puede tener dos ítems del mismo tipo.
--    El MER define la unicidad sobre (tipo_item_id, contenido, ficha_perfil_id),
--    pero contenido VARCHAR(7000) supera el límite de ~2704 bytes de un índice
--    B-tree de PostgreSQL, por lo que se omite del constraint.
-- ----------------------------------------------------------------------------
ALTER TABLE item
    ADD CONSTRAINT uk_item_ficha UNIQUE (tipo_item_id, ficha_perfil_id);

-- ----------------------------------------------------------------------------
-- 3. estado_ficha_perfil — uk_trazabilidad_ficha_estado
--    Incluye fecha_actualizacion a propósito: una ficha SÍ puede volver a un
--    estado anterior (EN_CONSTRUCCION → DISPONIBLE_PARA_EVALUACION →
--    APROBADA_CON_OBSERVACIONES → EN_CONSTRUCCION). Una constraint de solo
--    (ficha_perfil_id, estado_ficha_id) rompería el ciclo de vida.
-- ----------------------------------------------------------------------------
ALTER TABLE estado_ficha_perfil
    ADD CONSTRAINT uk_trazabilidad_ficha_estado
    UNIQUE (ficha_perfil_id, estado_ficha_id, fecha_actualizacion);

-- ----------------------------------------------------------------------------
-- 4. estado_evaluacion_ficha — uk_eef_trazabilidad
--    DIVERGENCIA DELIBERADA CON EL MER: el MER incluye fecha_actualizacion,
--    pero como la fecha se genera con Instant.now() dos inserciones
--    concurrentes siempre difieren en ese campo y la constraint no protegería
--    nada. Se aplica sobre (evaluacion_ficha_perfil_id, estado_evaluacion_id),
--    que es lo que ya enforcea AgregarEstadoEvaluacionFichaUseCase y lo que
--    respalda el dominio: EN_EVALUACION no es asignable manualmente y no hay
--    transición desde un estado terminal, de modo que una evaluación tiene a lo
--    sumo EN_EVALUACION + un estado terminal. Nunca se repite un estado.
-- ----------------------------------------------------------------------------
ALTER TABLE estado_evaluacion_ficha
    ADD CONSTRAINT uk_eef_trazabilidad
    UNIQUE (evaluacion_ficha_perfil_id, estado_evaluacion_id);

-- ----------------------------------------------------------------------------
-- 5. item — idx_item_ficha_ref
--    El índice de uk_item_ficha tiene tipo_item_id como columna líder, así que
--    no sirve para filtrar solo por ficha_perfil_id (listar los ítems de una
--    ficha, cascada de borrado). Índice explícito según el MER.
-- ----------------------------------------------------------------------------
CREATE INDEX idx_item_ficha_ref ON item(ficha_perfil_id);

-- ============================================================================
-- FUERA DE ALCANCE (documentado para trazabilidad):
--
-- · evaluacion_ficha_perfil: el MER pide uk_eval_rep_ficha_fecha
--   (representante, ficha, fecha). V1.4 ya tiene uk_representante_ficha
--   (representante, ficha), MÁS estricta y alineada con
--   RegistrarEvaluacionFichaPerfilUseCase. No se toca — lo desactualizado es
--   el MER, no la migración.
--
-- · uk_revision_item_fecha, uk_obs_item_rev_msg, uk_obs_eval_msg: sus tablas
--   (revision_item, observacion_item, observacion_evaluacion) y sus catálogos
--   (estado_revision, estado_observacion_revision) todavía no existen.
--   Sus constraints llegan con la migración que las cree.
-- ============================================================================
