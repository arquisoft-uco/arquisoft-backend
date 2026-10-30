package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ItemCualitativoAsesorKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA(
            "evaluaciones.aplicacion.itemcualitativoasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    ItemCualitativoAsesorKey(String clave, int parametros) {
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
