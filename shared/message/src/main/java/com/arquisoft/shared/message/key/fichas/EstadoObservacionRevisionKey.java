package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoObservacionRevisionKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("fichas.aplicacion.estadoobservacionrevision.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EstadoObservacionRevisionKey(String clave, int parametros) {
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
