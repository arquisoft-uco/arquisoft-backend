package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarAsesoresAdministradorKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.asesor.log.consultando-administrador", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.asesor.log.consulta-administrador-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarAsesoresAdministradorKey(String clave, int parametros) {
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
