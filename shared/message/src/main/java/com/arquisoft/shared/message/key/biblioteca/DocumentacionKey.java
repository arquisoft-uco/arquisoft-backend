package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum DocumentacionKey implements ClaveMensaje {

    LOG_CONSULTANDO("biblioteca.aplicacion.documentacion.log.consultando", 4),
    LOG_CONSULTA_COMPLETADA("biblioteca.aplicacion.documentacion.log.consulta-completada", 3);

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
