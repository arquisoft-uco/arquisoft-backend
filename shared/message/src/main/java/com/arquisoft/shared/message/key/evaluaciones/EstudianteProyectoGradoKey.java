package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de la réplica estudiante_proyecto_grado en evaluaciones. */
public enum EstudianteProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNADOS_RECIBIDO("evaluaciones.infraestructura.estudianteproyectogrado.log.asignados-recibido", 3),
    LOG_VERIFICACION_AGREGAR("evaluaciones.aplicacion.estudianteproyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADOS("evaluaciones.infraestructura.estudianteproyectogrado.log.agregados", 5),
    LOG_GUARDADOS("evaluaciones.infraestructura.estudianteproyectogrado.log.guardados", 1);

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
