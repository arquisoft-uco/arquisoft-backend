package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AsesorProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNADO_RECIBIDO("entregables.infraestructura.asesorproyectogrado.log.asignado-recibido", 4),
    LOG_FINALIZADO_RECIBIDO("entregables.infraestructura.asesorproyectogrado.log.finalizado-recibido", 4),
    LOG_VERIFICACION_AGREGAR("entregables.aplicacion.asesorproyectogrado.log.verificacion-agregar", 3),
    LOG_VERIFICACION_FINALIZAR("entregables.aplicacion.asesorproyectogrado.log.verificacion-finalizar", 3),
    LOG_AGREGADO("entregables.infraestructura.asesorproyectogrado.log.agregado", 2),
    LOG_REACTIVADO("entregables.infraestructura.asesorproyectogrado.log.reactivado", 2),
    LOG_DUPLICADO("entregables.infraestructura.asesorproyectogrado.log.duplicado", 2),
    LOG_DESCARTADO("entregables.infraestructura.asesorproyectogrado.log.descartado", 3),
    LOG_FINALIZADO("entregables.infraestructura.asesorproyectogrado.log.finalizado", 2),
    LOG_LAPIDA("entregables.infraestructura.asesorproyectogrado.log.lapida", 2),
    LOG_FINALIZACION_DESCARTADA("entregables.infraestructura.asesorproyectogrado.log.finalizacion-descartada", 3),
    LOG_GUARDADO("entregables.infraestructura.asesorproyectogrado.log.guardado", 1),
    ERROR_NO_ASIGNADO("entregables.dominio.asesorproyectogrado.error.no-asignado", 0),
    ERROR_NO_ASIGNADO_DETALLE("entregables.dominio.asesorproyectogrado.error.no-asignado-detalle", 2);

    private final String clave;
    private final int parametros;

    AsesorProyectoGradoKey(String clave, int parametros) {
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
