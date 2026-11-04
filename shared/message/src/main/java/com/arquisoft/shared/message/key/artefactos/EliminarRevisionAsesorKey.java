package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EliminarRevisionAsesorKey implements ClaveMensaje {

    LOG_ELIMINANDO("artefactos.aplicacion.eliminarrevisionasesor.log.eliminando", 2),
    LOG_VERIFICACION_ELIMINAR("artefactos.aplicacion.eliminarrevisionasesor.log.verificacion-eliminar", 3),
    LOG_ELIMINADA("artefactos.aplicacion.eliminarrevisionasesor.log.eliminada", 1);

    private final String clave;
    private final int parametros;

    EliminarRevisionAsesorKey(String clave, int parametros) {
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
