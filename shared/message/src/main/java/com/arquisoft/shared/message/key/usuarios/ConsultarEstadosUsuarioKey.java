package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarEstadosUsuarioKey implements ClaveMensaje {

    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.estadousuario.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    ConsultarEstadosUsuarioKey(String clave, int parametros) {
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
