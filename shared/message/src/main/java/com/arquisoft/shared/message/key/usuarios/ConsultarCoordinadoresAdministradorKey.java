package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ConsultarCoordinadoresAdministrador. */
public enum ConsultarCoordinadoresAdministradorKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.coordinador.log.consultando-administrador", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.coordinador.log.consulta-administrador-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarCoordinadoresAdministradorKey(String clave, int parametros) {
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
