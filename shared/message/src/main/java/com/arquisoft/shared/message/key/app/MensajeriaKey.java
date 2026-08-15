package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.PaquetesMensajes;
import com.arquisoft.shared.message.ClaveMensaje;

/** Mensajería asíncrona ({@code shared:amqp}): publicación y consumo de eventos. */
public enum MensajeriaKey implements ClaveMensaje {

    LOG_MENSAJE_NO_ENRUTADO("app.infraestructura.mensajeria.log.mensaje-no-enrutado"),
    LOG_BROKER_RECHAZO("app.infraestructura.mensajeria.log.broker-rechazo"),
    LOG_EVENTO_PUBLICADO("app.infraestructura.mensajeria.log.evento-publicado"),
    LOG_PUBLICACION_REINTENTO("app.infraestructura.mensajeria.log.publicacion-reintento"),
    LOG_PUBLICACION_NO_RECUPERABLE("app.infraestructura.mensajeria.log.publicacion-no-recuperable"),
    LOG_PUBLICACION_AGOTADA("app.infraestructura.mensajeria.log.publicacion-agotada"),
    LOG_EVENTO_A_DLQ("app.infraestructura.mensajeria.log.evento-a-dlq"),

    VALOR_CORRELACION_DESCONOCIDA("app.infraestructura.mensajeria.valor.correlacion-desconocida");

    private final String clave;

    MensajeriaKey(String clave) {
        this.clave = clave;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public String paquete() {
        return PaquetesMensajes.APP;
    }
}
