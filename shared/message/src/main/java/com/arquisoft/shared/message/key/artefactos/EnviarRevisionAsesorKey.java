package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EnviarRevisionAsesorKey implements ClaveMensaje {

    LOG_ENVIANDO("artefactos.aplicacion.enviarrevisionasesor.log.enviando", 2),
    LOG_VERIFICACION_ENVIO("artefactos.aplicacion.enviarrevisionasesor.log.verificacion-envio", 7),
    LOG_ENVIADA("artefactos.aplicacion.enviarrevisionasesor.log.enviada", 2);

    private final String clave;
    private final int parametros;

    EnviarRevisionAsesorKey(String clave, int parametros) {
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
