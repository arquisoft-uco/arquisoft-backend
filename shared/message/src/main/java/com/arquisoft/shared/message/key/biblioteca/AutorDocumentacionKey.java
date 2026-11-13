package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AutorDocumentacionKey implements ClaveMensaje {

    LOG_CONSULTANDO("biblioteca.aplicacion.autordocumentacion.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("biblioteca.aplicacion.autordocumentacion.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    AutorDocumentacionKey(String clave, int parametros) {
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
