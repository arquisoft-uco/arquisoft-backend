package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum AsesorKey implements ClaveMensaje {

    ERROR_DE_REVISION_NO_ENCONTRADO("artefactos.dominio.asesor.error.de-revision-no-encontrado", 1);

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
