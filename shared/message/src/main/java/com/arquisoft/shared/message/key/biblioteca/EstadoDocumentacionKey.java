package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoDocumentacionKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("biblioteca.aplicacion.estadodocumentacion.log.consulta-completada", 1),
    ERROR_NO_ENCONTRADO("biblioteca.dominio.estadodocumentacion.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("biblioteca.dominio.estadodocumentacion.error.no-encontrado-detalle", 1);

    private final String clave;
    private final int parametros;

    EstadoDocumentacionKey(String clave, int parametros) {
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
