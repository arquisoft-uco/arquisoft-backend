package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CriterioItemCualitativoJuradoKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.criterioitemcualitativojurado.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    CriterioItemCualitativoJuradoKey(String clave, int parametros) {
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
