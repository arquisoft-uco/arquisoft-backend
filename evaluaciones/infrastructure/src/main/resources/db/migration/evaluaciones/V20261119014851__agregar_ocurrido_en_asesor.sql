-- Tabla réplica local de asesor (dueño: contexto usuarios). ocurrido_en es metadato de replicación (no está en el MER): descarta eventos reentregados fuera de orden.
-- DEFAULT epoch: las filas previas a esta migración (solo manuales, ninguna poblada por evento) se tratan como anteriores a cualquier evento, de modo que el primer evento real las actualiza.
ALTER TABLE asesor
    ADD COLUMN IF NOT EXISTS ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01T00:00:00Z';
