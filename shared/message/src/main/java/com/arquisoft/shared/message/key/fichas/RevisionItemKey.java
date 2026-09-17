package com.arquisoft.shared.message.key.fichas;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RevisionItem. */
public enum RevisionItemKey implements ClaveMensaje {

    ERROR_ESTADO_NO_ENCONTRADO("fichas.dominio.revisionitem.error.estado-no-encontrado", 1),
    ERROR_YA_EXISTE("fichas.dominio.revisionitem.error.ya-existe", 1),
    ERROR_NO_ENCONTRADA("fichas.dominio.revisionitem.error.no-encontrada", 1),
    ERROR_CERRADA("fichas.dominio.revisionitem.error.cerrada", 1),
    LOG_AGREGANDO("fichas.aplicacion.revisionitem.log.agregando", 2),
    LOG_VERIFICACION_AGREGAR("fichas.aplicacion.revisionitem.log.verificacion-agregar", 3),
    LOG_AGREGADO("fichas.aplicacion.revisionitem.log.agregado", 2),
    LOG_CONSULTANDO_ELABORADAS("fichas.aplicacion.revisionitem.log.consultando-elaboradas", 2),
    LOG_CONSULTA_ELABORADAS_COMPLETADA("fichas.aplicacion.revisionitem.log.consulta-elaboradas-completada", 1);

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
