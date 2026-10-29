package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EntregableProyectoGradoKey implements ClaveMensaje {

    LOG_GENERANDO("entregables.aplicacion.entregableproyectogrado.log.generando", 2),
    LOG_VERIFICACION_GENERAR("entregables.aplicacion.entregableproyectogrado.log.verificacion-generar", 3),
    LOG_GENERADO("entregables.aplicacion.entregableproyectogrado.log.generado", 3),
    LOG_GUARDADO("entregables.infraestructura.entregableproyectogrado.log.guardado", 1);

    private final String clave;
    private final int parametros;

    EntregableProyectoGradoKey(String clave, int parametros) {
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
