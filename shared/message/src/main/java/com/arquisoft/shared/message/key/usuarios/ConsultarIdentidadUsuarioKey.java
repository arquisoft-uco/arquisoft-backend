package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarIdentidadUsuarioKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.usuario.log.consultando-identidad", 1),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.usuario.log.consulta-identidad-completada", 1);

    private final String clave;
    private final int parametros;

    ConsultarIdentidadUsuarioKey(String clave, int parametros) {
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
