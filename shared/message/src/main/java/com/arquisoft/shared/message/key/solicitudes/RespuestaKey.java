package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Respuesta. */
public enum RespuestaKey implements ClaveMensaje {

    ERROR_SOLICITUD_YA_RESPONDIDA("solicitudes.dominio.respuesta.error.solicitud-ya-respondida", 0),
    LOG_RESPONDIENDO("solicitudes.aplicacion.respuesta.log.respondiendo", 3),
    LOG_VERIFICACION_RESPUESTA("solicitudes.aplicacion.respuesta.log.verificacion-respuesta", 4),
    LOG_RESPONDIDA("solicitudes.aplicacion.respuesta.log.respondida", 2),
    LOG_GUARDADA("solicitudes.infraestructura.respuesta.log.guardada", 1),
    ERROR_RESPUESTA_NO_ENCONTRADA("solicitudes.dominio.respuesta.error.no-encontrada", 0),
    ERROR_RESPUESTA_NO_EN_REVISION("solicitudes.dominio.respuesta.error.no-en-revision", 0),
    LOG_ELIMINANDO("solicitudes.aplicacion.respuesta.log.eliminando", 3),
    LOG_VERIFICACION_ELIMINACION("solicitudes.aplicacion.respuesta.log.verificacion-eliminacion", 2),
    LOG_ELIMINADA("solicitudes.aplicacion.respuesta.log.eliminada", 2),
    LOG_ELIMINADA_REGISTRO("solicitudes.infraestructura.respuesta.log.eliminada", 1),
    LOG_CONSULTANDO(
            "solicitudes.aplicacion.respuesta.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA(
            "solicitudes.aplicacion.respuesta.log.consulta-completada", 4),
    ERROR_ESTADO_RESPUESTA_NO_RESOLUTIVO(
            "solicitudes.dominio.respuesta.error.estado-no-resolutivo", 1),
    ERROR_ESTADO_RESPUESTA_NO_RESOLUTIVO_DETALLE(
            "solicitudes.dominio.respuesta.error.estado-no-resolutivo-detalle", 1),
    LOG_MODIFICANDO_ESTADO("solicitudes.aplicacion.respuesta.log.modificando-estado", 3),
    LOG_VERIFICACION_MODIFICACION_ESTADO(
            "solicitudes.aplicacion.respuesta.log.verificacion-modificacion-estado", 4),
    LOG_ESTADO_MODIFICADO("solicitudes.aplicacion.respuesta.log.estado-modificado", 3);

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
