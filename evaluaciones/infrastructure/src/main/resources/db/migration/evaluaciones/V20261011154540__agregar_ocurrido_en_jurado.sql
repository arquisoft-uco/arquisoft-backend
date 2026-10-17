-- HU-250 — Tabla réplica local de jurado (dueño: contexto usuarios).
-- Poblada por JuradoAgregadoConsumer al consumir usuarios.jurado.agregado.
-- ocurrido_en es metadato del mecanismo de replicación (no está en el MER) y permite descartar
-- eventos reentregados fuera de orden.
-- DEFAULT epoch: las filas previas a esta migración (ninguna se pobló por evento; solo datos manuales)
-- se tratan como anteriores a cualquier evento, de modo que el primer evento real las actualiza.
ALTER TABLE jurado
    ADD COLUMN IF NOT EXISTS ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01T00:00:00Z';
