package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstadoProyectoGrado. */
public enum EstadoProyectoGradoKey implements ClaveMensaje {

    LOG_CREADO("proyectos.aplicacion.estadoproyectogrado.log.creado", 3),
    LOG_CONSULTANDO("proyectos.aplicacion.estadoproyectogrado.log.consultando", 2),
    LOG_CONSULTA_COMPLETADA("proyectos.aplicacion.estadoproyectogrado.log.consulta-completada", 1),
    LOG_GUARDADO("proyectos.infraestructura.estadoproyectogrado.log.guardado", 2),
    ERROR_NO_ENCONTRADO("proyectos.dominio.estadoproyectogrado.error.no-encontrado", 1);

    private final String clave;
    private final int parametros;

    EstadoProyectoGradoKey(String clave, int parametros) {
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
