package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de TipoSolicitud (catálogo). */
public enum TipoSolicitudKey implements ClaveMensaje {

    ERROR_TIPO_NO_ENCONTRADO("solicitudes.dominio.tiposolicitud.error.no-encontrado", 0),
    ERROR_TIPO_NO_ENCONTRADO_DETALLE("solicitudes.dominio.tiposolicitud.error.no-encontrado-detalle", 1),
    LOG_CONSULTA_COMPLETADA("solicitudes.aplicacion.tiposolicitud.log.consulta-completada", 1),
    LOG_CONSULTANDO("solicitudes.aplicacion.tiposolicitud.log.consultando", 2),
    LOG_CONSULTA_PARA_RECIBIR_COMPLETADA("solicitudes.aplicacion.tiposolicitud.log.consulta-para-recibir-completada", 1),
    LOG_CONSULTANDO_PARA_RECIBIR("solicitudes.aplicacion.tiposolicitud.log.consultando-para-recibir", 2);

    private final String clave;
    private final int parametros;

    TipoSolicitudKey(String clave, int parametros) {
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
