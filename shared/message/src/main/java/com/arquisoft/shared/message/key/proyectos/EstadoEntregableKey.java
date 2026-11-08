package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoEntregable (réplica del catálogo en proyectos). */
public enum EstadoEntregableKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("proyectos.dominio.estadoentregable.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("proyectos.dominio.estadoentregable.error.no-encontrado-detalle", 1);

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
