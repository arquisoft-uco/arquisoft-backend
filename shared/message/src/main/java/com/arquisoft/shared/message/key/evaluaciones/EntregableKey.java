package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EntregableKey implements ClaveMensaje {

    LOG_DISPONIBLE_RECIBIDO("evaluaciones.infraestructura.entregable.log.disponible-recibido", 3),
    LOG_VERIFICACION_AGREGAR("evaluaciones.aplicacion.entregable.log.verificacion-agregar", 2),
    LOG_AGREGADO("evaluaciones.aplicacion.entregable.log.agregado", 1),
    LOG_DUPLICADO("evaluaciones.aplicacion.entregable.log.duplicado", 1),
    LOG_GUARDADO("evaluaciones.infraestructura.entregable.log.guardado", 1);

    private final String clave;
    private final int parametros;

    EntregableKey(String clave, int parametros) {
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
