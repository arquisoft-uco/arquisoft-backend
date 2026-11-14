package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de la réplica de EntregableProyectoGrado en proyectos. */
public enum EntregableProyectoGradoKey implements ClaveMensaje {

    LOG_EVENTO_RECIBIDO("proyectos.infraestructura.entregableproyectogrado.log.evento-recibido", 3),
    LOG_VERIFICACION_ACTUALIZAR("proyectos.aplicacion.entregableproyectogrado.log.verificacion-actualizar", 2),
    LOG_ACTUALIZADO("proyectos.infraestructura.entregableproyectogrado.log.actualizado", 2),
    LOG_DESCARTADO("proyectos.infraestructura.entregableproyectogrado.log.descartado", 2),
    LOG_GUARDADO("proyectos.infraestructura.entregableproyectogrado.log.guardado", 2);

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
