package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoEntregableKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("entregables.aplicacion.estadoentregable.log.consulta-completada", 1),
    ERROR_NO_ENCONTRADO("entregables.dominio.estadoentregable.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("entregables.dominio.estadoentregable.error.no-encontrado-detalle", 1);

    private final String clave;
    private final int parametros;

    EstadoEntregableKey(String clave, int parametros) {
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
