package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudianteProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNADOS_RECIBIDO("entregables.infraestructura.estudianteproyectogrado.log.asignados-recibido", 3),
    LOG_VERIFICACION_AGREGAR("entregables.aplicacion.estudianteproyectogrado.log.verificacion-agregar", 2),
    LOG_AGREGADOS("entregables.infraestructura.estudianteproyectogrado.log.agregados", 5),
    LOG_GUARDADOS("entregables.infraestructura.estudianteproyectogrado.log.guardados", 1),
    LOG_DESTITUIDO_RECIBIDO("entregables.infraestructura.estudianteproyectogrado.log.destituido-recibido", 3),
    LOG_VERIFICACION_REMOVER("entregables.aplicacion.estudianteproyectogrado.log.verificacion-remover", 2),
    LOG_REMOVIDO("entregables.infraestructura.estudianteproyectogrado.log.removido", 2),
    LOG_LAPIDA("entregables.infraestructura.estudianteproyectogrado.log.lapida", 2),
    LOG_REMOCION_DESCARTADA("entregables.infraestructura.estudianteproyectogrado.log.remocion-descartada", 3);

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
