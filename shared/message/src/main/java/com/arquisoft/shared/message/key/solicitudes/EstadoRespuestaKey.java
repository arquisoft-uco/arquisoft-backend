package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoRespuestaKey implements ClaveMensaje {

    ERROR_ESTADO_RESPUESTA_NO_ENCONTRADO("solicitudes.dominio.estadorespuesta.error.no-encontrado", 1);

    private final String clave;
    private final int parametros;

    EstadoRespuestaKey(String clave, int parametros) {
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
