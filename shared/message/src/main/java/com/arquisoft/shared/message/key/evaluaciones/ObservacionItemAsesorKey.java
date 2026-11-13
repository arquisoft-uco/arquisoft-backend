package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ObservacionItemAsesorKey implements ClaveMensaje {

    LOG_REGISTRANDO("evaluaciones.aplicacion.observacionitemasesor.log.registrando", 1),
    LOG_VERIFICACION_REGISTRAR("evaluaciones.aplicacion.observacionitemasesor.log.verificacion-registrar", 2),
    LOG_REGISTRADO("evaluaciones.aplicacion.observacionitemasesor.log.registrado", 1),
    LOG_GUARDADA("evaluaciones.infraestructura.observacionitemasesor.log.guardada", 1),
    ERROR_EVALUACION_NO_ENCONTRADA(
            "evaluaciones.dominio.observacionitemasesor.error.evaluacion-no-encontrada", 0),
    ERROR_EVALUACION_NO_ENCONTRADA_DETALLE(
            "evaluaciones.dominio.observacionitemasesor.error.evaluacion-no-encontrada-detalle", 1),
    ERROR_DESCRIPCION_DUPLICADA(
            "evaluaciones.dominio.observacionitemasesor.error.descripcion-duplicada", 0),
    ERROR_DESCRIPCION_DUPLICADA_DETALLE(
            "evaluaciones.dominio.observacionitemasesor.error.descripcion-duplicada-detalle", 1);

    private final String clave;
    private final int parametros;

    ObservacionItemAsesorKey(String clave, int parametros) {
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
