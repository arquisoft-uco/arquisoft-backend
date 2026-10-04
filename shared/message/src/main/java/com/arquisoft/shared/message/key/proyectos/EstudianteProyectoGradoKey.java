package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstudianteProyectoGrado. */
public enum EstudianteProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNANDO("proyectos.aplicacion.estudianteproyectogrado.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("proyectos.aplicacion.estudianteproyectogrado.log.verificacion-asignar", 3),
    LOG_ASIGNADOS("proyectos.aplicacion.estudianteproyectogrado.log.asignados", 2),
    LOG_GUARDADOS("proyectos.infraestructura.estudianteproyectogrado.log.guardados", 2),
    ERROR_ESTUDIANTES_NO_VIGENTES("proyectos.dominio.estudianteproyectogrado.error.estudiantes-no-vigentes", 1),
    ERROR_DUPLICADO("proyectos.dominio.estudianteproyectogrado.error.duplicado", 1),
    ERROR_CUPO_EXCEDIDO("proyectos.dominio.estudianteproyectogrado.error.cupo-excedido", 1);

    private final String clave;
    private final int parametros;

    EstudianteProyectoGradoKey(String clave, int parametros) {
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
