package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum CambiarEstadoRevisionAsesorKey implements ClaveMensaje {

    LOG_CAMBIANDO_ESTADO("artefactos.aplicacion.cambiarestadorevisionasesor.log.cambiando-estado", 2),
    LOG_VERIFICACION_CAMBIO_ESTADO(
            "artefactos.aplicacion.cambiarestadorevisionasesor.log.verificacion-cambio-estado", 4),
    LOG_ESTADO_CAMBIADO("artefactos.aplicacion.cambiarestadorevisionasesor.log.estado-cambiado", 2),
    LOG_ESTADO_SIN_CAMBIO("artefactos.aplicacion.cambiarestadorevisionasesor.log.estado-sin-cambio", 2),
    LOG_ESTADO_ACTUALIZADO(
            "artefactos.infraestructura.cambiarestadorevisionasesor.log.estado-actualizado", 1);

    private final String clave;
    private final int parametros;

    CambiarEstadoRevisionAsesorKey(String clave, int parametros) {
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
