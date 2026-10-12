package com.arquisoft.shared.message.key.proyectos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de EstudianteProyectoGrado. */
public enum EstudianteProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNANDO("proyectos.aplicacion.estudianteproyectogrado.log.asignando", 2),
    LOG_VERIFICACION_ASIGNAR("proyectos.aplicacion.estudianteproyectogrado.log.verificacion-asignar", 5),
    LOG_ASIGNADOS("proyectos.aplicacion.estudianteproyectogrado.log.asignados", 2),
    LOG_CONSULTANDO("proyectos.aplicacion.estudianteproyectogrado.log.consultando", 2),
    LOG_CONSULTA_COMPLETADA("proyectos.aplicacion.estudianteproyectogrado.log.consulta-completada", 1),
    LOG_CONSULTANDO_COMPANEROS("proyectos.aplicacion.estudianteproyectogrado.log.consultando-companeros", 2),
    LOG_CONSULTA_COMPANEROS_COMPLETADA(
            "proyectos.aplicacion.estudianteproyectogrado.log.consulta-companeros-completada", 1),
    LOG_DESTITUYENDO("proyectos.aplicacion.estudianteproyectogrado.log.destituyendo", 2),
    LOG_VERIFICACION_DESTITUIR("proyectos.aplicacion.estudianteproyectogrado.log.verificacion-destituir", 3),
    LOG_DESTITUIDO("proyectos.aplicacion.estudianteproyectogrado.log.destituido", 2),
    LOG_GUARDADOS("proyectos.infraestructura.estudianteproyectogrado.log.guardados", 2),
    LOG_ELIMINADO("proyectos.infraestructura.estudianteproyectogrado.log.eliminado", 2),
    ERROR_ESTUDIANTES_NO_VIGENTES("proyectos.dominio.estudianteproyectogrado.error.estudiantes-no-vigentes", 1),
    ERROR_DUPLICADO("proyectos.dominio.estudianteproyectogrado.error.duplicado", 1),
    ERROR_CUPO_EXCEDIDO("proyectos.dominio.estudianteproyectogrado.error.cupo-excedido", 1),
    ERROR_YA_VINCULADO("proyectos.dominio.estudianteproyectogrado.error.ya-vinculado", 1),
    ERROR_NO_VINCULADO("proyectos.dominio.estudianteproyectogrado.error.no-vinculado", 2);

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
