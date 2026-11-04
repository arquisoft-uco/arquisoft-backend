package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum PlanRealKey implements ClaveMensaje {

    LOG_CONSULTANDO("mapas_ruta.aplicacion.planreal.log.consultando", 5),
    LOG_CONSULTA_COMPLETADA("mapas_ruta.aplicacion.planreal.log.consulta-completada", 3);

    private final String clave;
    private final int parametros;

    PlanRealKey(String clave, int parametros) {
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
