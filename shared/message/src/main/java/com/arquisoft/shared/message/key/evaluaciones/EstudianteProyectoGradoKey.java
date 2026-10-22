package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de la réplica estudiante_proyecto_grado en evaluaciones. */
public enum EstudianteProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNADOS_RECIBIDO("evaluaciones.infraestructura.estudianteproyectogrado.log.asignados-recibido", 3),
    LOG_VERIFICACION_AGREGAR("evaluaciones.aplicacion.estudianteproyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADOS("evaluaciones.infraestructura.estudianteproyectogrado.log.agregados", 5),
    LOG_GUARDADOS("evaluaciones.infraestructura.estudianteproyectogrado.log.guardados", 1),
    LOG_DESTITUIDO_RECIBIDO("evaluaciones.infraestructura.estudianteproyectogrado.log.destituido-recibido", 3),
    LOG_VERIFICACION_REMOVER("evaluaciones.aplicacion.estudianteproyectogrado.log.verificacion-remover", 2),
    LOG_REMOVIDO("evaluaciones.infraestructura.estudianteproyectogrado.log.removido", 2),
    LOG_LAPIDA("evaluaciones.infraestructura.estudianteproyectogrado.log.lapida", 2),
    LOG_REMOCION_DESCARTADA("evaluaciones.infraestructura.estudianteproyectogrado.log.remocion-descartada", 3);

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
