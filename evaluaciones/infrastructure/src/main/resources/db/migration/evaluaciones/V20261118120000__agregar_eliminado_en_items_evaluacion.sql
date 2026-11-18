-- Borrado lógico de los ítems de evaluación: NULL en eliminado_en = vigente. Las filas existentes
-- quedan vigentes. Un ítem eliminado se conserva para el historial de las evaluaciones que lo usaron.
ALTER TABLE item_cualitativo_jurado ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
ALTER TABLE item_cuantitativo_jurado ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
ALTER TABLE item_cualitativo_asesor ADD COLUMN eliminado_en TIMESTAMPTZ NULL;
ALTER TABLE item_cuantitativo_asesor ADD COLUMN eliminado_en TIMESTAMPTZ NULL;

-- La unicidad del nombre aplica solo entre ítems vigentes.
DROP INDEX uk_item_cual_jurado_nombre_ci;
CREATE UNIQUE INDEX uk_item_cual_jurado_nombre_ci
    ON item_cualitativo_jurado (LOWER(nombre)) WHERE eliminado_en IS NULL;

DROP INDEX uk_item_cuant_jurado_nombre_cat_ci;
CREATE UNIQUE INDEX uk_item_cuant_jurado_nombre_cat_ci
    ON item_cuantitativo_jurado (LOWER(nombre), categoria_id) WHERE eliminado_en IS NULL;

ALTER TABLE item_cualitativo_asesor DROP CONSTRAINT uk_item_cual_asesor_nombre;
CREATE UNIQUE INDEX uk_item_cual_asesor_nombre
    ON item_cualitativo_asesor (nombre) WHERE eliminado_en IS NULL;

ALTER TABLE item_cuantitativo_asesor DROP CONSTRAINT uk_item_cuant_asesor_nombre_cat;
CREATE UNIQUE INDEX uk_item_cuant_asesor_nombre_cat
    ON item_cuantitativo_asesor (nombre, categoria_id) WHERE eliminado_en IS NULL;
