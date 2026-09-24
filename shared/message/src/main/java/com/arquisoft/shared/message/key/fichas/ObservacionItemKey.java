package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ObservacionItem. */
public enum ObservacionItemKey implements ClaveMensaje {

    ERROR_OBSERVACION_ITEM_DUPLICADA("fichas.dominio.observacionitem.error.duplicada", 2),
    ERROR_ESTADO_NO_ENCONTRADO("fichas.dominio.estadoobservacionrevision.error.estado-no-encontrado", 1),
    LOG_AGREGANDO("fichas.aplicacion.observacionitem.log.agregando", 1),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.observacionitem.log.verificacion-agregar", 4),
    LOG_AGREGADA("fichas.aplicacion.observacionitem.log.agregada", 2),
    LOG_GUARDADA("fichas.infraestructura.observacionitem.log.guardada", 1);

    private final String clave;
    private final int parametros;

    ObservacionItemKey(String clave, int parametros) {
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
