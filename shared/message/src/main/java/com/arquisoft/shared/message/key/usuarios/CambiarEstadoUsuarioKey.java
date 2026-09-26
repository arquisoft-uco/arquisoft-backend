package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CambiarEstadoUsuarioKey implements ClaveMensaje {

    LOG_ESTADO_CAMBIADO("usuarios.aplicacion.usuario.log.estado-cambiado", 2);

    private final String clave;
    private final int parametros;

    CambiarEstadoUsuarioKey(String clave, int parametros) {
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
