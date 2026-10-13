package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ConsultarEstudiantesVigentes. */
public enum ConsultarEstudiantesVigentesKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.estudiante.log.consultando-vigentes", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.estudiante.log.consulta-vigentes-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarEstudiantesVigentesKey(String clave, int parametros) {
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
