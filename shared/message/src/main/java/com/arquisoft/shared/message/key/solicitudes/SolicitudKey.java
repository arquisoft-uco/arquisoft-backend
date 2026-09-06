package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Solicitud. */
public enum SolicitudKey implements ClaveMensaje {

    ERROR_REMITENTE_NO_ENCONTRADO("solicitudes.dominio.solicitud.error.remitente-no-encontrado", 1),
    ERROR_DESTINATARIO_NO_ENCONTRADO("solicitudes.dominio.solicitud.error.destinatario-no-encontrado", 1),
    ERROR_DESTINATARIO_NO_ASIGNADO("solicitudes.dominio.solicitud.error.destinatario-no-asignado", 2),
    ERROR_SOLICITUD_DUPLICADA("solicitudes.dominio.solicitud.error.solicitud-duplicada", 0),
    LOG_ENVIADA("solicitudes.aplicacion.solicitud.log.enviada", 1),
    LOG_GUARDADA("solicitudes.infraestructura.solicitud.log.guardada", 1),
    LOG_ASIGNACION_NO_VERIFICADA("solicitudes.infraestructura.solicitud.log.asignacion-no-verificada", 2);

    private final String clave;
    private final int parametros;

    SolicitudKey(String clave, int parametros) {
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
