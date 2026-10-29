package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

/** Claves del registro de una revisión del asesor. */
public enum RegistrarRevisionAsesorKey implements ClaveMensaje {

    LOG_REGISTRANDO("artefactos.aplicacion.registrarrevisionasesor.log.registrando", 2),
    LOG_VERIFICACION_REGISTRO("artefactos.aplicacion.registrarrevisionasesor.log.verificacion-registro", 4),
    LOG_REGISTRADA("artefactos.aplicacion.registrarrevisionasesor.log.registrada", 2);

    private final String clave;
    private final int parametros;

    RegistrarRevisionAsesorKey(String clave, int parametros) {
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
