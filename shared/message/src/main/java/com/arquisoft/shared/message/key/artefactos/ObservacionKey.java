package com.arquisoft.shared.message.key.artefactos;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ObservacionKey implements ClaveMensaje {

    LOG_CONSULTANDO("artefactos.aplicacion.observacion.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("artefactos.aplicacion.observacion.log.consulta-completada", 1),
    LOG_GUARDADA("artefactos.infraestructura.observacion.log.guardada", 1),
    LOG_ELIMINADA("artefactos.infraestructura.observacion.log.eliminada", 1),
    ERROR_NO_ENCONTRADA("artefactos.dominio.observacion.error.no-encontrada", 0),
    ERROR_NO_ENCONTRADA_DETALLE("artefactos.dominio.observacion.error.no-encontrada-detalle", 1),
    LOG_ESTADOS_ACTUALIZADOS("artefactos.infraestructura.observacion.log.estados-actualizados", 2),
    ERROR_DUPLICADA("artefactos.dominio.observacion.error.duplicada", 0),
    ERROR_DUPLICADA_DETALLE("artefactos.dominio.observacion.error.duplicada-detalle", 1),
    ERROR_ESTADO_INVALIDO("artefactos.dominio.observacion.error.estado-invalido", 1),
    LOG_ESTADO_ACTUALIZADO("artefactos.infraestructura.observacion.log.estado-actualizado", 1),
    ERROR_NO_SOLUCIONADA("artefactos.dominio.observacion.error.no-solucionada", 0),
    ERROR_NO_SOLUCIONADA_DETALLE("artefactos.dominio.observacion.error.no-solucionada-detalle", 2),
    ERROR_NO_REABRIBLE("artefactos.dominio.observacion.error.no-reabrible", 0),
    ERROR_NO_REABRIBLE_DETALLE("artefactos.dominio.observacion.error.no-reabrible-detalle", 4),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO("artefactos.dominio.observacion.error.estado-destino-no-permitido", 1),
    ERROR_ESTADO_DESTINO_NO_PERMITIDO_DETALLE(
            "artefactos.dominio.observacion.error.estado-destino-no-permitido-detalle", 2);

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
