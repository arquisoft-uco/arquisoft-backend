package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de RevisionAsesor. */
public enum RevisionAsesorKey implements ClaveMensaje {

    LOG_CONSULTANDO_ESTUDIANTE("artefactos.aplicacion.revisionasesor.log.consultando-estudiante", 2),
    LOG_CONSULTA_ESTUDIANTE_COMPLETADA("artefactos.aplicacion.revisionasesor.log.consulta-estudiante-completada", 1);

    private final String clave;
    private final int parametros;

    RevisionAsesorKey(String clave, int parametros) {
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
