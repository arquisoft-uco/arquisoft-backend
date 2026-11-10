package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstadoEntregableProyectoGradoKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("entregables.dominio.estadoentregableproyectogrado.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE(
            "entregables.dominio.estadoentregableproyectogrado.error.no-encontrado-detalle", 1),
    LOG_GUARDADO("entregables.infraestructura.estadoentregableproyectogrado.log.guardado", 3);

    private final String clave;
    private final int parametros;

    EstadoEntregableProyectoGradoKey(String clave, int parametros) {
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
