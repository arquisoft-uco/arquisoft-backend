package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de TipoItem. */
public enum TipoItemKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("fichas.aplicacion.tipoitem.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    TipoItemKey(String clave, int parametros) {
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
