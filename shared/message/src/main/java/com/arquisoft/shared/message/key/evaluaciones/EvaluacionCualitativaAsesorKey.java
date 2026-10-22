package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionCualitativaAsesorKey implements ClaveMensaje {

    ERROR_EVALUACION_ASESOR_NO_ENCONTRADA(
            "evaluaciones.dominio.evaluacioncualitativaasesor.error.evaluacion-asesor-no-encontrada", 1),
    ERROR_NO_PERTENECE_ASESOR(
            "evaluaciones.dominio.evaluacioncualitativaasesor.error.no-pertenece-asesor", 1),
    ERROR_ITEMS_NO_ENCONTRADOS(
            "evaluaciones.dominio.evaluacioncualitativaasesor.error.items-no-encontrados", 1),
    ERROR_CRITERIOS_NO_ENCONTRADOS(
            "evaluaciones.dominio.evaluacioncualitativaasesor.error.criterios-no-encontrados", 1),
    ERROR_ITEMS_YA_REGISTRADOS(
            "evaluaciones.dominio.evaluacioncualitativaasesor.error.items-ya-registrados", 1),
    LOG_REGISTRANDO_LOTE("evaluaciones.aplicacion.evaluacioncualitativaasesor.log.registrando-lote", 2),
    LOG_VERIFICACION_LOTE("evaluaciones.aplicacion.evaluacioncualitativaasesor.log.verificacion-lote", 5),
    LOG_LOTE_REGISTRADO("evaluaciones.aplicacion.evaluacioncualitativaasesor.log.lote-registrado", 3),
    LOG_LOTE_GUARDADO("evaluaciones.infraestructura.evaluacioncualitativaasesor.log.lote-guardado", 1),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacioncualitativaasesor.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacioncualitativaasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EvaluacionCualitativaAsesorKey(String clave, int parametros) {
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
