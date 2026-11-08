-- HU-152 — El estado LISTO_PARA_REVISION deja de existir: el ciclo de vida del trabajo entregado lo gestiona
-- entregables. Ningún comando asignó nunca ese estado, así que el historial no lo referencia.

DELETE FROM estado_proyecto WHERE id = 'LISTO_PARA_REVISION';
