package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoFicha. */
public enum EstadoFichaKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.estadoficha.error.no-encontrado", 1),
    LOG_CONSULTA_COMPLETADA("fichas.aplicacion.estadoficha.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EstadoFichaKey(String clave, int parametros) {
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
