package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RevisionItem. */
public enum RevisionItemKey implements ClaveMensaje {

    ERROR_ESTADO_NO_ENCONTRADO("fichas.dominio.revisionitem.error.estado-no-encontrado", 1),
    ERROR_YA_EXISTE("fichas.dominio.revisionitem.error.ya-existe", 1),
    ERROR_NO_ENCONTRADA("fichas.dominio.revisionitem.error.no-encontrada", 1),
    LOG_AGREGADO("fichas.aplicacion.revisionitem.log.agregado", 2),
    LOG_MODIFICADO("fichas.aplicacion.revisionitem.log.modificado", 1);

    private final String clave;
    private final int parametros;

    RevisionItemKey(String clave, int parametros) {
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
