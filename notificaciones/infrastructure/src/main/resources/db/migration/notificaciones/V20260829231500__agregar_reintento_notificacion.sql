-- Reintento de envios fallidos.
--
-- destinatario_nombre y cuerpo no se guardaban: el cuerpo se renderiza en el consumidor a partir
-- del payload del evento, que tampoco se conserva. Sin ellos, un reintento no puede reconstruir el
-- correo, y re-renderizar la plantilla tampoco sirve porque sus parametros no quedan en ninguna
-- columna. Se persiste el mensaje ya renderizado para reenviar exactamente lo que fallo.
--
-- El DEFAULT '' cubre las filas existentes, que se enviaron antes de que hubiera estas columnas y
-- por tanto ya no son reintentables.

ALTER TABLE notificacion
    ADD COLUMN destinatario_nombre  VARCHAR(100) NOT NULL DEFAULT '',
    ADD COLUMN cuerpo               TEXT         NOT NULL DEFAULT '',
    ADD COLUMN intentos             INTEGER      NOT NULL DEFAULT 0,
    ADD COLUMN fecha_ultimo_intento TIMESTAMPTZ;
