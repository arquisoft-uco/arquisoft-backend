package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EliminarObservacionKey implements ClaveMensaje {

    LOG_ELIMINANDO("artefactos.aplicacion.eliminarobservacion.log.eliminando", 2),
    LOG_VERIFICACION_ELIMINAR("artefactos.aplicacion.eliminarobservacion.log.verificacion-eliminar", 5),
    LOG_ELIMINADA("artefactos.aplicacion.eliminarobservacion.log.eliminada", 1);

    private final String clave;
    private final int parametros;

    EliminarObservacionKey(String clave, int parametros) {
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
