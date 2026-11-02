package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CambiarEstadoEstudianteObservacionKey implements ClaveMensaje {

    LOG_CAMBIANDO_ESTADO("artefactos.aplicacion.cambiarestadoestudianteobservacion.log.cambiando-estado", 3),
    LOG_VERIFICACION_CAMBIO_ESTADO(
            "artefactos.aplicacion.cambiarestadoestudianteobservacion.log.verificacion-cambio-estado", 4),
    LOG_ESTADO_CAMBIADO("artefactos.aplicacion.cambiarestadoestudianteobservacion.log.estado-cambiado", 2),
    LOG_ESTADO_SIN_CAMBIO("artefactos.aplicacion.cambiarestadoestudianteobservacion.log.estado-sin-cambio", 2);

    private final String clave;
    private final int parametros;

    CambiarEstadoEstudianteObservacionKey(String clave, int parametros) {
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
