package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Respuesta. */
public enum RespuestaKey implements ClaveMensaje {

    ERROR_SOLICITUD_YA_RESPONDIDA("solicitudes.dominio.respuesta.error.solicitud-ya-respondida", 1),
    ERROR_SOLICITUD_NO_ES_DESTINATARIO("solicitudes.dominio.solicitud.error.no-es-destinatario", 1),
    ERROR_ESTADO_RESPUESTA_NO_ENCONTRADO("solicitudes.dominio.estadorespuesta.error.no-encontrado", 1),
    LOG_RESPONDIENDO("solicitudes.aplicacion.respuesta.log.respondiendo", 2),
    LOG_VERIFICACION_RESPUESTA("solicitudes.aplicacion.respuesta.log.verificacion-respuesta", 2),
    LOG_RESPONDIDA("solicitudes.aplicacion.respuesta.log.respondida", 1),
    LOG_GUARDADA("solicitudes.infraestructura.respuesta.log.guardada", 1),
    ERROR_RESPUESTA_NO_ENCONTRADA("solicitudes.dominio.respuesta.error.no-encontrada", 1),
    ERROR_RESPUESTA_NO_EN_REVISION("solicitudes.dominio.respuesta.error.no-en-revision", 1),
    LOG_ELIMINANDO("solicitudes.aplicacion.respuesta.log.eliminando", 2),
    LOG_VERIFICACION_ELIMINACION("solicitudes.aplicacion.respuesta.log.verificacion-eliminacion", 2),
    LOG_ELIMINADA("solicitudes.aplicacion.respuesta.log.eliminada", 1),
    LOG_ELIMINANDO_NOVEDAD_ASESOR("solicitudes.aplicacion.respuesta.log.eliminando-novedad-asesor", 2),
    LOG_ELIMINADA_NOVEDAD_ASESOR("solicitudes.aplicacion.respuesta.log.eliminada-novedad-asesor", 1),
    LOG_ELIMINADA_REGISTRO("solicitudes.infraestructura.respuesta.log.eliminada", 1);

    private final String clave;
    private final int parametros;

    RespuestaKey(String clave, int parametros) {
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
