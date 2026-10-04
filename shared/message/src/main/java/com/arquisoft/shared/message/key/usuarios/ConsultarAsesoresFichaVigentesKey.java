package com.arquisoft.shared.message.key.usuarios;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultarAsesoresFichaVigentesKey implements ClaveMensaje {

    LOG_CONSULTANDO("usuarios.aplicacion.asesorficha.log.consultando-vigentes", 4),
    LOG_CONSULTA_COMPLETADA("usuarios.aplicacion.asesorficha.log.consulta-vigentes-completada", 3);

    private final String clave;
    private final int parametros;

    ConsultarAsesoresFichaVigentesKey(String clave, int parametros) {
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
