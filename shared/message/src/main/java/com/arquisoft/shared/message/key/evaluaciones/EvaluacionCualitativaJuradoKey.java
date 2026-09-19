package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionCualitativaJuradoKey implements ClaveMensaje {

    ERROR_EVALUACION_JURADO_NO_ENCONTRADA(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.evaluacion-jurado-no-encontrada", 1),
    ERROR_EVALUACION_JURADO_NO_PERTENECE_JURADO(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.evaluacion-jurado-no-pertenece-jurado", 0),
    ERROR_ITEMS_NO_ENCONTRADOS(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.items-no-encontrados", 1),
    ERROR_CRITERIOS_NO_ENCONTRADOS(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.criterios-no-encontrados", 1),
    ERROR_ITEMS_YA_REGISTRADOS(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.items-ya-registrados", 1),
    ERROR_PADRES_DISTINTOS(
            "evaluaciones.dominio.evaluacioncualitativajurado.error.padres-distintos", 0),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacioncualitativajurado.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacioncualitativajurado.log.consulta-completada", 1),
    LOG_REGISTRANDO_LOTE("evaluaciones.aplicacion.evaluacioncualitativajurado.log.registrando-lote", 2),
    LOG_VERIFICACION_LOTE("evaluaciones.aplicacion.evaluacioncualitativajurado.log.verificacion-lote", 3),
    LOG_LOTE_REGISTRADO("evaluaciones.aplicacion.evaluacioncualitativajurado.log.lote-registrado", 3),
    LOG_LOTE_GUARDADO("evaluaciones.infraestructura.evaluacioncualitativajurado.log.lote-guardado", 1);

    private final String clave;
    private final int parametros;

    EvaluacionCualitativaJuradoKey(String clave, int parametros) {
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
