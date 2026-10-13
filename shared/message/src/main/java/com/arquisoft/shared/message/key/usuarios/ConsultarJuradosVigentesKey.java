package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves de ConsultarJuradosVigentes. */
public enum ConsultarJuradosVigentesKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.jurado.log.consultando-vigentes", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.jurado.log.consulta-vigentes-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarJuradosVigentesKey(String clave, int parametros) {
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
