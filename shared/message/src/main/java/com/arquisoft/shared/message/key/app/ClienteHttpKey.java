package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ClienteHttpKey implements ClaveMensaje {

    ERROR_TRANSPORTE("app.infraestructura.cliente-http.error.transporte", 0);

    private final String clave;
    private final int parametros;

    ClienteHttpKey(String clave, int parametros) {
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
