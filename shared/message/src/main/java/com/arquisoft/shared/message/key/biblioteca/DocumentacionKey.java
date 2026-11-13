package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum DocumentacionKey implements ClaveMensaje {

    LOG_CONSULTANDO("biblioteca.aplicacion.documentacion.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("biblioteca.aplicacion.documentacion.log.consulta-completada", 3),
    LOG_CONSULTANDO_ENLACE("biblioteca.aplicacion.documentacion.log.consultando-enlace", 1),
    LOG_VERIFICACION_ENLACE("biblioteca.aplicacion.documentacion.log.verificacion-enlace", 2),
    LOG_ENLACE_GENERADO("biblioteca.aplicacion.documentacion.log.enlace-generado", 1),
    ERROR_NO_COMPARTIBLE("biblioteca.dominio.documentacion.error.no-compartible", 0),
    ERROR_NO_COMPARTIBLE_DETALLE("biblioteca.dominio.documentacion.error.no-compartible-detalle", 1),
    ERROR_NO_DISPONIBLE("biblioteca.infraestructura.documentacion.error.no-disponible", 0),
    ERROR_NO_DISPONIBLE_DETALLE("biblioteca.infraestructura.documentacion.error.no-disponible-detalle", 1);

    private final String clave;
    private final int parametros;

    DocumentacionKey(String clave, int parametros) {
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
