package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Jurado (réplica local en evaluaciones). */
public enum JuradoKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("evaluaciones.infraestructura.jurado.log.agregado-recibido", 3),
    LOG_VERIFICACION_AGREGAR("evaluaciones.aplicacion.jurado.log.verificacion-agregar", 2),
    LOG_AGREGADO("evaluaciones.aplicacion.jurado.log.agregado", 1),
    LOG_REACTIVADO("evaluaciones.aplicacion.jurado.log.reactivado", 1),
    LOG_DUPLICADO("evaluaciones.aplicacion.jurado.log.duplicado", 1),
    LOG_DESCARTADO("evaluaciones.aplicacion.jurado.log.descartado", 2),
    LOG_GUARDADO("evaluaciones.infraestructura.jurado.log.guardado", 1),
    LOG_ACTUALIZADO("evaluaciones.infraestructura.jurado.log.actualizado", 1);

    private final String clave;
    private final int parametros;

    JuradoKey(String clave, int parametros) {
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
