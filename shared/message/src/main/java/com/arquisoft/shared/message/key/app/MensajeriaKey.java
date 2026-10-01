package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum MensajeriaKey implements ClaveMensaje {

    LOG_MENSAJE_NO_ENRUTADO("app.infraestructura.mensajeria.log.mensaje-no-enrutado", 3),
    LOG_BROKER_RECHAZO("app.infraestructura.mensajeria.log.broker-rechazo", 2),
    LOG_EVENTO_PUBLICADO("app.infraestructura.mensajeria.log.evento-publicado", 3),
    LOG_PUBLICACION_REINTENTO("app.infraestructura.mensajeria.log.publicacion-reintento", 5),
    LOG_PUBLICACION_NO_RECUPERABLE("app.infraestructura.mensajeria.log.publicacion-no-recuperable", 3),
    LOG_PUBLICACION_AGOTADA("app.infraestructura.mensajeria.log.publicacion-agotada", 4),
    LOG_EVENTO_AL_OUTBOX("app.infraestructura.mensajeria.log.evento-al-outbox", 2),
    LOG_EVENTO_RECIBIDO("app.infraestructura.mensajeria.log.evento-recibido", 2),
    LOG_EVENTO_PROCESADO("app.infraestructura.mensajeria.log.evento-procesado", 2),
    LOG_EVENTO_A_DLQ("app.infraestructura.mensajeria.log.evento-a-dlq", 2),
    LOG_EVENTO_REENCOLADO("app.infraestructura.mensajeria.log.evento-reencolado", 2),
    LOG_EVENTO_A_DLQ_TRAS_REINTENTO("app.infraestructura.mensajeria.log.evento-a-dlq-tras-reintento", 2),

    VALOR_CORRELACION_DESCONOCIDA("app.infraestructura.mensajeria.valor.correlacion-desconocida", 0);

    private final String clave;
    private final int parametros;

    MensajeriaKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
