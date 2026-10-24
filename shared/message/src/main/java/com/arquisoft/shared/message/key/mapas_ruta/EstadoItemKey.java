package com.arquisoft.shared.message.key.mapas_ruta;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoItemKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("mapas_ruta.aplicacion.estadoitem.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EstadoItemKey(String clave, int parametros) {
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
