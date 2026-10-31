package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CambiarEstadoAsesorObservacionKey implements ClaveMensaje {

    LOG_CAMBIANDO_ESTADO("artefactos.aplicacion.cambiarestadoasesorobservacion.log.cambiando-estado", 3),
    LOG_VERIFICACION_CAMBIO_ESTADO(
            "artefactos.aplicacion.cambiarestadoasesorobservacion.log.verificacion-cambio-estado", 10),
    LOG_ESTADO_CAMBIADO("artefactos.aplicacion.cambiarestadoasesorobservacion.log.estado-cambiado", 2);

    private final String clave;
    private final int parametros;

    CambiarEstadoAsesorObservacionKey(String clave, int parametros) {
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
