package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Solicitud. */
public enum SolicitudKey implements ClaveMensaje {

    ERROR_REMITENTE_NO_ENCONTRADO("solicitudes.dominio.solicitud.error.remitente-no-encontrado", 1),
    ERROR_DESTINATARIO_NO_ENCONTRADO("solicitudes.dominio.solicitud.error.destinatario-no-encontrado", 1),
    ERROR_DESTINATARIO_NO_ASIGNADO("solicitudes.dominio.solicitud.error.destinatario-no-asignado", 2),
    ERROR_SOLICITUD_DUPLICADA("solicitudes.dominio.solicitud.error.solicitud-duplicada", 0),
    LOG_ENVIADA("solicitudes.aplicacion.solicitud.log.enviada", 1),
    LOG_ENVIANDO_ASESOR("solicitudes.aplicacion.solicitud.log.enviando-asesor", 2),
    LOG_VERIFICACION_ENVIO_ASESOR("solicitudes.aplicacion.solicitud.log.verificacion-envio-asesor", 2),
    LOG_ENVIADA_ASESOR("solicitudes.aplicacion.solicitud.log.enviada-asesor", 1),
    LOG_ENVIANDO_CAMBIO_ASESOR("solicitudes.aplicacion.solicitud.log.enviando-cambio-asesor", 2),
    LOG_VERIFICACION_ENVIO_CAMBIO_ASESOR(
            "solicitudes.aplicacion.solicitud.log.verificacion-envio-cambio-asesor", 2),
    LOG_ENVIADA_CAMBIO_ASESOR("solicitudes.aplicacion.solicitud.log.enviada-cambio-asesor", 1),
    LOG_ENVIANDO_AMPLIACION_PLAZO("solicitudes.aplicacion.solicitud.log.enviando-ampliacion-plazo", 2),
    LOG_VERIFICACION_ENVIO_AMPLIACION_PLAZO(
            "solicitudes.aplicacion.solicitud.log.verificacion-envio-ampliacion-plazo", 2),
    LOG_ENVIADA_AMPLIACION_PLAZO("solicitudes.aplicacion.solicitud.log.enviada-ampliacion-plazo", 1),
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
