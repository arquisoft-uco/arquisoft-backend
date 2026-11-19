package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de Asesor (réplica local en evaluaciones). */
public enum AsesorKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("evaluaciones.dominio.asesor.error.no-encontrado", 0),
    ERROR_NO_ENCONTRADO_DETALLE("evaluaciones.dominio.asesor.error.no-encontrado-detalle", 1),
    LOG_AGREGADO_RECIBIDO("evaluaciones.infraestructura.asesor.log.agregado-recibido", 3),
    LOG_VERIFICACION_AGREGAR("evaluaciones.aplicacion.asesor.log.verificacion-agregar", 2),
    LOG_AGREGADO("evaluaciones.aplicacion.asesor.log.agregado", 1),
    LOG_REACTIVADO("evaluaciones.aplicacion.asesor.log.reactivado", 1),
    LOG_DUPLICADO("evaluaciones.aplicacion.asesor.log.duplicado", 1),
    LOG_DESCARTADO("evaluaciones.aplicacion.asesor.log.descartado", 2),
    LOG_GUARDADO("evaluaciones.infraestructura.asesor.log.guardado", 1),
    LOG_ACTUALIZADO("evaluaciones.infraestructura.asesor.log.actualizado", 1),
    LOG_REMOVIDO_RECIBIDO("evaluaciones.infraestructura.asesor.log.removido-recibido", 2),
    LOG_VERIFICACION_REMOVER("evaluaciones.aplicacion.asesor.log.verificacion-remover", 2),
    LOG_REMOVIDO("evaluaciones.aplicacion.asesor.log.removido", 1),
    LOG_LAPIDA("evaluaciones.aplicacion.asesor.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("evaluaciones.aplicacion.asesor.log.remocion-descartada", 2);

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
