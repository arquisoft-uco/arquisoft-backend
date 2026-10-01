package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Adaptadores de salida que entregan la notificacion al proveedor. */
public enum EnvioNotificacionKey implements ClaveMensaje {

    ERROR_ENVIO_FALLIDO("notificaciones.infraestructura.envio.error.envio-fallido", 1),
    ERROR_PLANTILLA_CORREO("notificaciones.infraestructura.envio.error.plantilla-correo", 1),
    LOG_ENVIADO("notificaciones.infraestructura.envio.log.enviado", 2),
    LOG_ENVIO_RECHAZADO("notificaciones.infraestructura.envio.log.envio-rechazado", 2),
    LOG_ENVIO_SIMULADO("notificaciones.infraestructura.envio.log.envio-simulado", 2),
    LOG_PLANTILLA_ACTUALIZADA("notificaciones.infraestructura.envio.log.plantilla-actualizada", 1),
    LOG_PLANTILLA_NO_ACTUALIZADA("notificaciones.infraestructura.envio.log.plantilla-no-actualizada", 2);

    private final String clave;
    private final int parametros;

    EnvioNotificacionKey(String clave, int parametros) {
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
