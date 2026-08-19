package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum MensajeriaKey implements ClaveMensaje {

    LOG_MENSAJE_NO_ENRUTADO("app.infraestructura.mensajeria.log.mensaje-no-enrutado", 0),
    LOG_BROKER_RECHAZO("app.infraestructura.mensajeria.log.broker-rechazo", 0),
    LOG_EVENTO_PUBLICADO("app.infraestructura.mensajeria.log.evento-publicado", 0),
    LOG_PUBLICACION_REINTENTO("app.infraestructura.mensajeria.log.publicacion-reintento", 0),
    LOG_PUBLICACION_NO_RECUPERABLE("app.infraestructura.mensajeria.log.publicacion-no-recuperable", 0),
    LOG_PUBLICACION_AGOTADA("app.infraestructura.mensajeria.log.publicacion-agotada", 0),
    LOG_EVENTO_A_DLQ("app.infraestructura.mensajeria.log.evento-a-dlq", 0),

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
