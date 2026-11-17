package com.arquisoft.shared.message.key.biblioteca;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudianteProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNADOS_RECIBIDO("biblioteca.infraestructura.estudianteproyectogrado.log.asignados-recibido", 3),
    LOG_VERIFICACION_AGREGAR("biblioteca.aplicacion.estudianteproyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADOS("biblioteca.infraestructura.estudianteproyectogrado.log.agregados", 5),
    LOG_DESTITUIDO_RECIBIDO("biblioteca.infraestructura.estudianteproyectogrado.log.destituido-recibido", 3),
    LOG_VERIFICACION_REMOVER("biblioteca.aplicacion.estudianteproyectogrado.log.verificacion-remover", 2),
    LOG_REMOVIDO("biblioteca.infraestructura.estudianteproyectogrado.log.removido", 2),
    LOG_LAPIDA("biblioteca.infraestructura.estudianteproyectogrado.log.lapida", 2),
    LOG_REMOCION_DESCARTADA("biblioteca.infraestructura.estudianteproyectogrado.log.remocion-descartada", 3),
    LOG_GUARDADO("biblioteca.infraestructura.estudianteproyectogrado.log.guardado", 1),
    ERROR_SIN_AUTORES("biblioteca.dominio.estudianteproyectogrado.error.sin-autores", 0),
    ERROR_SIN_AUTORES_DETALLE("biblioteca.dominio.estudianteproyectogrado.error.sin-autores-detalle", 1),
    ERROR_AUTOR_NO_VIGENTE("biblioteca.dominio.estudianteproyectogrado.error.autor-no-vigente", 0),
    ERROR_AUTOR_NO_VIGENTE_DETALLE("biblioteca.dominio.estudianteproyectogrado.error.autor-no-vigente-detalle", 2);

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
