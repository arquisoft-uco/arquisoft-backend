package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ObservacionKey implements ClaveMensaje {

    LOG_CONSULTANDO("artefactos.aplicacion.observacion.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("artefactos.aplicacion.observacion.log.consulta-completada", 1),
    LOG_GUARDADA("artefactos.infraestructura.observacion.log.guardada", 1),
    ERROR_DUPLICADA("artefactos.dominio.observacion.error.duplicada", 0),
    ERROR_DUPLICADA_DETALLE("artefactos.dominio.observacion.error.duplicada-detalle", 1),
    ERROR_ESTADO_INVALIDO("artefactos.dominio.observacion.error.estado-invalido", 1);

    private final String clave;
    private final int parametros;

    ObservacionKey(String clave, int parametros) {
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
