package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AsesorProyectoGrado (réplica local en artefactos). */
public enum AsesorProyectoGradoKey implements ClaveMensaje {

    LOG_ASIGNADO_RECIBIDO("artefactos.infraestructura.asesorproyectogrado.log.asignado-recibido", 4),
    LOG_FINALIZADO_RECIBIDO("artefactos.infraestructura.asesorproyectogrado.log.finalizado-recibido", 4),
    LOG_VERIFICACION_AGREGAR("artefactos.aplicacion.asesorproyectogrado.log.verificacion-agregar", 4),
    LOG_VERIFICACION_FINALIZAR("artefactos.aplicacion.asesorproyectogrado.log.verificacion-finalizar", 3),
    LOG_AGREGADO("artefactos.aplicacion.asesorproyectogrado.log.agregado", 2),
    LOG_REACTIVADO("artefactos.aplicacion.asesorproyectogrado.log.reactivado", 2),
    LOG_DUPLICADO("artefactos.aplicacion.asesorproyectogrado.log.duplicado", 2),
    LOG_DESCARTADO("artefactos.aplicacion.asesorproyectogrado.log.descartado", 3),
    LOG_FINALIZADO("artefactos.aplicacion.asesorproyectogrado.log.finalizado", 2),
    LOG_LAPIDA("artefactos.aplicacion.asesorproyectogrado.log.lapida", 2),
    LOG_FINALIZACION_DESCARTADA("artefactos.aplicacion.asesorproyectogrado.log.finalizacion-descartada", 3),
    LOG_GUARDADO("artefactos.infraestructura.asesorproyectogrado.log.guardado", 1),
    ERROR_NO_ASIGNADO("artefactos.dominio.asesorproyectogrado.error.no-asignado", 2);

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
