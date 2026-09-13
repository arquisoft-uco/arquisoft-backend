package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Estudiante (réplica local en proyectos). */
public enum EstudianteProyectosKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("proyectos.infraestructura.estudiante.log.agregado-recibido", 3),
    LOG_AGREGADO("proyectos.aplicacion.estudiante.log.agregado", 1),
    LOG_DUPLICADO("proyectos.aplicacion.estudiante.log.duplicado", 1),
    LOG_DESCARTADO("proyectos.aplicacion.estudiante.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("proyectos.aplicacion.estudiante.log.verificacion-agregar", 2),
    LOG_GUARDADO("proyectos.infraestructura.estudiante.log.guardado", 1);

    private final String clave;
    private final int parametros;

    EstudianteProyectosKey(String clave, int parametros) {
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
