package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CerrarRevisionAsesorKey implements ClaveMensaje {

    LOG_CERRANDO("artefactos.aplicacion.cerrarrevisionasesor.log.cerrando", 2),
    LOG_VERIFICACION_CIERRE("artefactos.aplicacion.cerrarrevisionasesor.log.verificacion-cierre", 8),
    LOG_CERRADA("artefactos.aplicacion.cerrarrevisionasesor.log.cerrada", 2);

    private final String clave;
    private final int parametros;

    CerrarRevisionAsesorKey(String clave, int parametros) {
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
