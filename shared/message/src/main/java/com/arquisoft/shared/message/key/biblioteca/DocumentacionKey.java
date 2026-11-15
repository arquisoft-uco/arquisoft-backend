package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum DocumentacionKey implements ClaveMensaje {

    LOG_CONSULTANDO("biblioteca.aplicacion.documentacion.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("biblioteca.aplicacion.documentacion.log.consulta-completada", 3),
    LOG_DESCARGANDO("biblioteca.aplicacion.documentacion.log.descargando", 2),
    LOG_VERIFICACION_DESCARGA("biblioteca.aplicacion.documentacion.log.verificacion-descarga", 2),
    LOG_DESCARGA_GENERADA("biblioteca.aplicacion.documentacion.log.descarga-generada", 2),
    ERROR_NO_DESCARGABLE("biblioteca.dominio.documentacion.error.no-descargable", 0),
    ERROR_NO_DESCARGABLE_DETALLE("biblioteca.dominio.documentacion.error.no-descargable-detalle", 1),
    ERROR_ARCHIVO_NO_DISPONIBLE("biblioteca.dominio.documentacion.error.archivo-no-disponible", 0),
    ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE("biblioteca.dominio.documentacion.error.archivo-no-disponible-detalle", 1),
    ERROR_ARCHIVO_AUSENTE("biblioteca.infraestructura.documentacion.error.archivo-ausente", 0),
    ERROR_ARCHIVO_AUSENTE_DETALLE("biblioteca.infraestructura.documentacion.error.archivo-ausente-detalle", 3);

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
