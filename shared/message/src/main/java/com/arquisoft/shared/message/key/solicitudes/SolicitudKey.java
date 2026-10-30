package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Solicitud. */
public enum SolicitudKey implements ClaveMensaje {

    ERROR_REMITENTE_NO_ENCONTRADO("solicitudes.dominio.solicitud.error.remitente-no-encontrado", 0),
    ERROR_DESTINATARIO_NO_ENCONTRADO("solicitudes.dominio.solicitud.error.destinatario-no-encontrado", 0),
    ERROR_SOLICITUD_DUPLICADA("solicitudes.dominio.solicitud.error.solicitud-duplicada", 0),
    LOG_ENVIANDO("solicitudes.aplicacion.solicitud.log.enviando", 3),
    LOG_VERIFICACION_ENVIO("solicitudes.aplicacion.solicitud.log.verificacion-envio", 1),
    LOG_ENVIADA("solicitudes.aplicacion.solicitud.log.enviada", 2),
    LOG_VERIFICACION_REGISTRO_REMITENTE(
            "solicitudes.aplicacion.remitente.log.verificacion-registro", 2),
    LOG_VERIFICACION_REGISTRO_DESTINATARIO(
            "solicitudes.aplicacion.destinatario.log.verificacion-registro", 2),
    LOG_GUARDADA("solicitudes.infraestructura.solicitud.log.guardada", 1),
    ERROR_SOLICITUD_NO_ENCONTRADA("solicitudes.dominio.solicitud.error.no-encontrada", 0),
    ERROR_SOLICITUD_NO_PROPIA("solicitudes.dominio.solicitud.error.no-propia", 0),
    ERROR_SOLICITUD_TIPO_NO_COINCIDE("solicitudes.dominio.solicitud.error.tipo-no-coincide", 0),
    ERROR_SOLICITUD_CON_RESPUESTAS("solicitudes.dominio.solicitud.error.con-respuestas", 0),
    ERROR_SOLICITUD_NO_ES_DESTINATARIO("solicitudes.dominio.solicitud.error.no-es-destinatario", 0),
    LOG_ELIMINANDO("solicitudes.aplicacion.solicitud.log.eliminando", 3),
    LOG_VERIFICACION_ELIMINACION("solicitudes.aplicacion.solicitud.log.verificacion-eliminacion", 2),
    LOG_ELIMINADA("solicitudes.aplicacion.solicitud.log.eliminada", 2),
    LOG_ELIMINADA_REGISTRO("solicitudes.infraestructura.solicitud.log.eliminada", 1),
    ERROR_SOLICITUD_TIPO_NO_COINCIDE_ASESOR(
            "solicitudes.dominio.solicitud.error.tipo-no-coincide-asesor", 0),
    ERROR_SOLICITUD_TIPO_NO_COINCIDE_CAMBIO_ASESOR(
            "solicitudes.dominio.solicitud.error.tipo-no-coincide-cambio-asesor", 0),
    ERROR_SOLICITUD_TIPO_NO_COINCIDE_AMPLIACION_PLAZO(
            "solicitudes.dominio.solicitud.error.tipo-no-coincide-ampliacion-plazo", 0),
    ERROR_SOLICITUD_TIPO_NO_COINCIDE_REGISTRO_MODIFICACION_USUARIOS(
            "solicitudes.dominio.solicitud.error.tipo-no-coincide-registro-modificacion-usuarios", 0),
    LOG_CONSULTANDO(
            "solicitudes.aplicacion.solicitud.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA(
            "solicitudes.aplicacion.solicitud.log.consulta-completada", 4);

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
