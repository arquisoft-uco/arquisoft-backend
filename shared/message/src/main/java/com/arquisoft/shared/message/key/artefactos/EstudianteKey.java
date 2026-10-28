package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EstudianteKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("artefactos.infraestructura.estudiante.log.agregado-recibido", 3),
    LOG_AGREGADO("artefactos.aplicacion.estudiante.log.agregado", 1),
    LOG_DUPLICADO("artefactos.aplicacion.estudiante.log.duplicado", 1),
    LOG_DESCARTADO("artefactos.aplicacion.estudiante.log.descartado", 2),
    LOG_REACTIVADO("artefactos.aplicacion.estudiante.log.reactivado", 1),
    LOG_VERIFICACION_AGREGAR("artefactos.aplicacion.estudiante.log.verificacion-agregar", 2),
    LOG_GUARDADO("artefactos.infraestructura.estudiante.log.guardado", 1),
    LOG_REMOVIDO_RECIBIDO("artefactos.infraestructura.estudiante.log.removido-recibido", 2),
    LOG_VERIFICACION_REMOVER("artefactos.aplicacion.estudiante.log.verificacion-remover", 2),
    LOG_REMOVIDO("artefactos.aplicacion.estudiante.log.removido", 1),
    LOG_LAPIDA("artefactos.aplicacion.estudiante.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("artefactos.aplicacion.estudiante.log.remocion-descartada", 2),
    LOG_ACTUALIZADO("artefactos.infraestructura.estudiante.log.actualizado", 1),
    LOG_VERIFICACION_ACTUALIZAR("artefactos.aplicacion.estudiante.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("artefactos.infraestructura.estudiante.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("artefactos.infraestructura.estudiante.log.actualizacion-no-replicado", 1),
    ERROR_NO_ENCONTRADO("artefactos.dominio.estudiante.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("artefactos.dominio.estudiante.error.no-encontrado-detalle", 1),
    ERROR_NO_VIGENTE("artefactos.dominio.estudiante.error.no-vigente", 1),
    ERROR_NO_VIGENTE_DETALLE("artefactos.dominio.estudiante.error.no-vigente-detalle", 1);

    private final String clave;
    private final int parametros;

    EstudianteKey(String clave, int parametros) {
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
