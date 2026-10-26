package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum VersionArtefactoKey implements ClaveMensaje {

    LOG_CONSULTANDO_ASESOR("artefactos.aplicacion.versionartefacto.log.consultando-asesor", 4),
    LOG_CONSULTA_ASESOR_COMPLETADA("artefactos.aplicacion.versionartefacto.log.consulta-asesor-completada", 1);

    private final String clave;
    private final int parametros;

    VersionArtefactoKey(String clave, int parametros) {
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
