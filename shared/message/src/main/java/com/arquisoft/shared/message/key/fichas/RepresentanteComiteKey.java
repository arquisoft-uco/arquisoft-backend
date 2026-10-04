package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RepresentanteComite. */
public enum RepresentanteComiteKey implements ClaveMensaje {

    ERROR_NO_ENCONTRADO("fichas.dominio.representantecomite.error.no-encontrado", 1),
    LOG_AGREGADO_RECIBIDO("fichas.infraestructura.representantecomite.log.agregado-recibido", 3),
    LOG_AGREGADO("fichas.infraestructura.representantecomite.log.agregado", 1),
    LOG_REACTIVADO("fichas.infraestructura.representantecomite.log.reactivado", 1),
    LOG_DUPLICADO("fichas.infraestructura.representantecomite.log.duplicado", 1),
    LOG_DESCARTADO("fichas.infraestructura.representantecomite.log.descartado", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.representantecomite.log.verificacion-agregar", 2),
    LOG_GUARDADO("fichas.infraestructura.representantecomite.log.guardado", 1),
    LOG_ACTUALIZADO("fichas.infraestructura.representantecomite.log.actualizado", 1),
    LOG_VERIFICACION_ACTUALIZAR("fichas.aplicacion.representantecomite.log.verificacion-actualizar", 2),
    LOG_ACTUALIZACION_DESCARTADA("fichas.infraestructura.representantecomite.log.actualizacion-descartada", 3),
    LOG_ACTUALIZACION_NO_REPLICADO("fichas.infraestructura.representantecomite.log.actualizacion-no-replicado", 1),
    LOG_VERIFICACION_REMOVER("fichas.aplicacion.representantecomite.log.verificacion-remover", 2),
    LOG_REMOVIDO_RECIBIDO("fichas.infraestructura.representantecomite.log.removido-recibido", 2),
    LOG_REMOVIDO("fichas.infraestructura.representantecomite.log.removido", 1),
    LOG_LAPIDA("fichas.infraestructura.representantecomite.log.lapida", 1),
    LOG_REMOCION_DESCARTADA("fichas.infraestructura.representantecomite.log.remocion-descartada", 2);

    private final String clave;
    private final int parametros;

    RepresentanteComiteKey(String clave, int parametros) {
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
