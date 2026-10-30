package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum RegistrarObservacionKey implements ClaveMensaje {

    LOG_REGISTRANDO("artefactos.aplicacion.registrarobservacion.log.registrando", 2),
    LOG_VERIFICACION_REGISTRO("artefactos.aplicacion.registrarobservacion.log.verificacion-registro", 3),
    LOG_REGISTRADA("artefactos.aplicacion.registrarobservacion.log.registrada", 2);

    private final String clave;
    private final int parametros;

    RegistrarObservacionKey(String clave, int parametros) {
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
