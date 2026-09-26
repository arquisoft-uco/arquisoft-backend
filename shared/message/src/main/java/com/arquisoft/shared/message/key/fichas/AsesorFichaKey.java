package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de AsesorFicha. */
public enum AsesorFichaKey implements ClaveMensaje {

    LOG_AGREGADO_RECIBIDO("fichas.infraestructura.asesorficha.log.agregado-recibido", 3),
    LOG_AGREGADO("fichas.infraestructura.asesorficha.log.agregado", 1),
    LOG_DUPLICADO("fichas.infraestructura.asesorficha.log.duplicado", 1),
    LOG_DESCARTADO("fichas.infraestructura.asesorficha.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.asesorficha.log.verificacion-agregar", 2),
    LOG_GUARDADO("fichas.infraestructura.asesorficha.log.guardado", 1),
    LOG_ACTUALIZADO("fichas.infraestructura.asesorficha.log.actualizado", 1),
    LOG_VERIFICACION_ACTUALIZAR("fichas.aplicacion.asesorficha.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("fichas.infraestructura.asesorficha.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("fichas.infraestructura.asesorficha.log.actualizacion-no-replicado", 1),
    LOG_VERIFICACION_REMOVER("fichas.aplicacion.asesorficha.log.verificacion-remover", 2),
    LOG_REMOVIDO_RECIBIDO("fichas.infraestructura.asesorficha.log.removido-recibido", 2),
    LOG_REMOVIDO("fichas.infraestructura.asesorficha.log.removido", 1),
    LOG_LAPIDA("fichas.infraestructura.asesorficha.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("fichas.infraestructura.asesorficha.log.remocion-descartada", 2),
    LOG_REACTIVADO("fichas.infraestructura.asesorficha.log.reactivado", 1);

    private final String clave;
    private final int parametros;

    AsesorFichaKey(String clave, int parametros) {
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
