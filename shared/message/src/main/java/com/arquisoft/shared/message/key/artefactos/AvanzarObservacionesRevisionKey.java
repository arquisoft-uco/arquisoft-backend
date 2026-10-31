package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AvanzarObservacionesRevisionKey implements ClaveMensaje {

    LOG_AVANZANDO("artefactos.aplicacion.avanzarobservacionesrevision.log.avanzando", 3);

    private final String clave;
    private final int parametros;

    AvanzarObservacionesRevisionKey(String clave, int parametros) {
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
