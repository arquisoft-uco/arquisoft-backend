package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Coordinador (réplica local en proyectos). */
public enum CoordinadorKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("proyectos.infraestructura.coordinador.log.agregado-recibido", 3),
    LOG_AGREGADO("proyectos.aplicacion.coordinador.log.agregado", 1),
    LOG_DUPLICADO("proyectos.aplicacion.coordinador.log.duplicado", 1),
    LOG_DESCARTADO("proyectos.aplicacion.coordinador.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("proyectos.aplicacion.coordinador.log.verificacion-agregar", 2),
    LOG_GUARDADO("proyectos.infraestructura.coordinador.log.guardado", 1),
    LOG_ACTUALIZADO("proyectos.infraestructura.coordinador.log.actualizado", 1),
    LOG_VERIFICACION_ACTUALIZAR("proyectos.aplicacion.coordinador.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("proyectos.infraestructura.coordinador.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("proyectos.infraestructura.coordinador.log.actualizacion-no-replicado", 1);

    private final String clave;
    private final int parametros;

    CoordinadorKey(String clave, int parametros) {
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
