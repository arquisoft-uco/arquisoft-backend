package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ConsultarEstudiantesAdministrador. */
public enum ConsultarEstudiantesAdministradorKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.estudiante.log.consultando-administrador", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.estudiante.log.consulta-administrador-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarEstudiantesAdministradorKey(String clave, int parametros) {
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
