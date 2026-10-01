package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarUsuariosAdministradorKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.usuario.log.consultando-administrador", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.usuario.log.consulta-administrador-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarUsuariosAdministradorKey(String clave, int parametros) {
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
