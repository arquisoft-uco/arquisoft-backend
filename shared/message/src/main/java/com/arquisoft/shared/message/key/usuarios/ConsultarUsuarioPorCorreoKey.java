package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarUsuarioPorCorreoKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.usuario.log.consultando-por-correo", 1),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.usuario.log.consulta-por-correo-completada", 0);

    private final String clave;
    private final int parametros;

    ConsultarUsuarioPorCorreoKey(String clave, int parametros) {
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
