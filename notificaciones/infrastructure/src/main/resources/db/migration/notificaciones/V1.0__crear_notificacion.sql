-- Registro de notificaciones enviadas.
--
-- event_id es UNIQUE a proposito: es la clave de idempotencia. RabbitMQ reentrega un mensaje
-- cuando el consumer muere sin confirmar, y la restriccion corta el duplicado a nivel de base
-- aunque dos consumers concurrentes pasen a la vez la comprobacion previa de la aplicacion.

CREATE TABLE notificacion (
    id             UUID         NOT NULL,
    event_id       VARCHAR(36)  NOT NULL,
    tipo           VARCHAR(60)  NOT NULL,
    destinatario   VARCHAR(50)  NOT NULL,
    asunto         VARCHAR(200) NOT NULL,
    estado         VARCHAR(20)  NOT NULL,
    detalle_error  TEXT,
    fecha_creacion TIMESTAMPTZ  NOT NULL,
    fecha_envio    TIMESTAMPTZ,
    CONSTRAINT pk_notificacion PRIMARY KEY (id),
    CONSTRAINT uq_notificacion_event_id UNIQUE (event_id)
);

-- Consulta operativa: recuperar los envios fallidos para reintentarlos.
CREATE INDEX idx_notificacion_estado ON notificacion (estado);
