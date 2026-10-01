package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Estudiante (réplica local en proyectos). */
public enum EstudianteProyectosKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("proyectos.infraestructura.estudiante.log.agregado-recibido", 3),
    LOG_AGREGADO("proyectos.aplicacion.estudiante.log.agregado", 1),
    LOG_DUPLICADO("proyectos.aplicacion.estudiante.log.duplicado", 1),
    LOG_DESCARTADO("proyectos.aplicacion.estudiante.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("proyectos.aplicacion.estudiante.log.verificacion-agregar", 2),
    LOG_GUARDADO("proyectos.infraestructura.estudiante.log.guardado", 1),
    LOG_REMOVIDO_RECIBIDO("proyectos.infraestructura.estudiante.log.removido-recibido", 2),
    LOG_REMOVIDO("proyectos.aplicacion.estudiante.log.removido", 1),
    LOG_LAPIDA("proyectos.aplicacion.estudiante.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("proyectos.aplicacion.estudiante.log.remocion-descartada", 2),
    LOG_REACTIVADO("proyectos.aplicacion.estudiante.log.reactivado", 1),
    LOG_VERIFICACION_REMOVER("proyectos.aplicacion.estudiante.log.verificacion-remover", 2),
    LOG_ACTUALIZADO("proyectos.infraestructura.estudiante.log.actualizado", 1),
    LOG_USUARIO_MODIFICADO_RECIBIDO("proyectos.infraestructura.estudiante.log.usuario-modificado-recibido", 2),
    LOG_VERIFICACION_ACTUALIZAR("proyectos.aplicacion.estudiante.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("proyectos.infraestructura.estudiante.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("proyectos.infraestructura.estudiante.log.actualizacion-no-replicado", 1);

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
