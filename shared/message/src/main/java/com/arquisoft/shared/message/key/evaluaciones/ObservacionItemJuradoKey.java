package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ObservacionItemJuradoKey implements ClaveMensaje {

    LOG_REGISTRANDO("evaluaciones.aplicacion.observacionitemjurado.log.registrando", 1),
    LOG_VERIFICACION_REGISTRAR("evaluaciones.aplicacion.observacionitemjurado.log.verificacion-registrar", 3),
    LOG_REGISTRADO("evaluaciones.aplicacion.observacionitemjurado.log.registrado", 1),
    LOG_GUARDADA("evaluaciones.infraestructura.observacionitemjurado.log.guardada", 1),
    ERROR_EVALUACION_CUANTITATIVA_JURADO_NO_ENCONTRADA(
            "evaluaciones.dominio.observacionitemjurado.error.evaluacion-no-encontrada", 1),
    ERROR_EVALUACION_JURADO_FINALIZADA(
            "evaluaciones.dominio.observacionitemjurado.error.evaluacion-finalizada", 1),
    ERROR_DESCRIPCION_DUPLICADA(
            "evaluaciones.dominio.observacionitemjurado.error.descripcion-duplicada", 1);

    private final String clave;
    private final int parametros;

    ObservacionItemJuradoKey(String clave, int parametros) {
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
