package com.arquisoft.shared.message.key.entregables;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AsesorKey implements ClaveMensaje {

    ERROR_NO_VIGENTE("entregables.dominio.asesor.error.no-vigente", 0),
    ERROR_NO_VIGENTE_DETALLE("entregables.dominio.asesor.error.no-vigente-detalle", 1),
    LOG_AGREGADO_RECIBIDO("entregables.infraestructura.asesor.log.agregado-recibido", 3),
    LOG_AGREGADO("entregables.aplicacion.asesor.log.agregado", 1),
    LOG_DUPLICADO("entregables.aplicacion.asesor.log.duplicado", 1),
    LOG_DESCARTADO("entregables.aplicacion.asesor.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("entregables.aplicacion.asesor.log.verificacion-agregar", 2),
    LOG_GUARDADO("entregables.infraestructura.asesor.log.guardado", 1),
    LOG_ACTUALIZADO("entregables.infraestructura.asesor.log.actualizado", 1),
    LOG_REMOVIDO_RECIBIDO("entregables.infraestructura.asesor.log.removido-recibido", 2),
    LOG_VERIFICACION_REMOVER("entregables.aplicacion.asesor.log.verificacion-remover", 2),
    LOG_REMOVIDO("entregables.aplicacion.asesor.log.removido", 1),
    LOG_LAPIDA("entregables.aplicacion.asesor.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("entregables.aplicacion.asesor.log.remocion-descartada", 2),
    LOG_REACTIVADO("entregables.aplicacion.asesor.log.reactivado", 1);

    private final String clave;
    private final int parametros;

    AsesorKey(String clave, int parametros) {
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
