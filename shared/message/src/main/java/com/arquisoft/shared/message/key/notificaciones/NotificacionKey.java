package com.arquisoft.shared.message.key.notificaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Reglas del agregado {@code NotificacionDomain}. */
public enum NotificacionKey implements ClaveMensaje {

    ERROR_TRANSICION_INVALIDA("notificaciones.dominio.notificacion.error.transicion-invalida", 1),
    ERROR_REINTENTO_NO_PERMITIDO("notificaciones.dominio.notificacion.error.reintento-no-permitido", 1),
    ERROR_TIPO_NO_ENCONTRADO("notificaciones.dominio.notificacion.error.tipo-no-encontrado", 1),
    ERROR_ESTADO_NO_ENCONTRADO("notificaciones.dominio.notificacion.error.estado-no-encontrado", 1),
    LOG_VERIFICACION_PREVIA("notificaciones.aplicacion.notificacion.log.verificacion-previa", 2),
    LOG_GUARDADA("notificaciones.infraestructura.notificacion.log.guardada", 2),
    LOG_REINTENTO_INICIADO("notificaciones.aplicacion.notificacion.log.reintento-iniciado", 1),
    LOG_REINTENTO_RESULTADO("notificaciones.aplicacion.notificacion.log.reintento-resultado", 3);

    private final String clave;
    private final int parametros;

    NotificacionKey(String clave, int parametros) {
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
