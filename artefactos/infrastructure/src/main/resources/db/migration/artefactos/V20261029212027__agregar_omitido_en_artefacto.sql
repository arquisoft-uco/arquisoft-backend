-- Baja lógica del artefacto (HU-103): NULL = activo, con fecha = omitido.
-- Las filas existentes quedan activas. La columna no está en el MER; el Event Storming la nombra OmitidoEn.
ALTER TABLE artefacto ADD COLUMN omitido_en TIMESTAMPTZ NULL;
