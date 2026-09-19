package com.arquisoft.shared.message.key.solicitudes;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de TipoSolicitud (catálogo). */
public enum TipoSolicitudKey implements ClaveMensaje {

    ERROR_TIPO_NO_ENCONTRADO("solicitudes.dominio.tiposolicitud.error.no-encontrado", 1);

    private final String clave;
    private final int parametros;

    TipoSolicitudKey(String clave, int parametros) {
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
