package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudianteProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNADOS_RECIBIDO("artefactos.infraestructura.estudianteproyectogrado.log.asignados-recibido", 4),
    LOG_DESTITUIDO_RECIBIDO("artefactos.infraestructura.estudianteproyectogrado.log.destituido-recibido", 4),
    LOG_VERIFICACION_AGREGAR("artefactos.aplicacion.estudianteproyectogrado.log.verificacion-agregar", 3),
    LOG_VERIFICACION_DESTITUIR("artefactos.aplicacion.estudianteproyectogrado.log.verificacion-destituir", 3),
    LOG_AGREGADOS("artefactos.aplicacion.estudianteproyectogrado.log.agregados", 5),
    LOG_DESTITUIDO("artefactos.aplicacion.estudianteproyectogrado.log.destituido", 2),
    LOG_LAPIDA("artefactos.aplicacion.estudianteproyectogrado.log.lapida", 2),
    LOG_DESTITUCION_DESCARTADA("artefactos.aplicacion.estudianteproyectogrado.log.destitucion-descartada", 3),
    LOG_GUARDADO("artefactos.infraestructura.estudianteproyectogrado.log.guardado", 1),
    LOG_GUARDADOS("artefactos.infraestructura.estudianteproyectogrado.log.guardados", 1);

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
