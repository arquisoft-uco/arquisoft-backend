package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Asesor (réplica local en proyectos). */
public enum AsesorKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("proyectos.infraestructura.asesor.log.agregado-recibido", 3),
    LOG_AGREGADO("proyectos.aplicacion.asesor.log.agregado", 1),
    LOG_DUPLICADO("proyectos.aplicacion.asesor.log.duplicado", 1),
    LOG_DESCARTADO("proyectos.aplicacion.asesor.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("proyectos.aplicacion.asesor.log.verificacion-agregar", 2),
    LOG_GUARDADO("proyectos.infraestructura.asesor.log.guardado", 1);

    private final String clave;
    private final int parametros;

    AsesorKey(String clave, int parametros) {
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
