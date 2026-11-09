package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoProyectoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("entregables.dominio.estadoproyecto.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("entregables.dominio.estadoproyecto.error.no-encontrado-detalle", 1);

    private final String clave;
    private final int parametros;

    EstadoProyectoKey(String clave, int parametros) {
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
