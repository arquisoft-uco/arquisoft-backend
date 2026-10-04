package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarBibliotecariosAdministradorKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.bibliotecario.log.consultando-administrador", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.bibliotecario.log.consulta-administrador-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarBibliotecariosAdministradorKey(String clave, int parametros) {
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
