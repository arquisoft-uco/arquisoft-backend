package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionCuantitativaAsesorKey implements ClaveMensaje {

    ERROR_ITEMS_NO_ENCONTRADOS(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.items-no-encontrados", 0),
    ERROR_ITEMS_NO_ENCONTRADOS_DETALLE(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.items-no-encontrados-detalle", 1),
    ERROR_PUNTAJES_EXCEDEN_VALOR_ITEM(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.puntajes-exceden-valor-item", 0),
    ERROR_PUNTAJES_EXCEDEN_VALOR_ITEM_DETALLE(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.puntajes-exceden-valor-item-detalle", 1),
    ERROR_ITEMS_YA_REGISTRADOS(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.items-ya-registrados", 0),
    ERROR_ITEMS_YA_REGISTRADOS_DETALLE(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.items-ya-registrados-detalle", 1),
    ERROR_OMISION_EVALUACION_ASESOR_FINALIZADA(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.omision-finalizada", 0),
    ERROR_OMISION_EVALUACION_ASESOR_FINALIZADA_DETALLE(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.omision-finalizada-detalle", 1),
    ERROR_EVALUACIONES_NO_REGISTRADAS(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.no-registradas", 0),
    ERROR_EVALUACIONES_NO_REGISTRADAS_DETALLE(
            "evaluaciones.dominio.evaluacioncuantitativaasesor.error.no-registradas-detalle", 2),
    LOG_OMITIENDO_LOTE("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.omitiendo-lote", 2),
    LOG_VERIFICACION_OMISION("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.verificacion-omision", 4),
    LOG_LOTE_OMITIDO("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.lote-omitido", 2),
    LOG_LOTE_ELIMINADO("evaluaciones.infraestructura.evaluacioncuantitativaasesor.log.lote-eliminado", 1),
    LOG_REGISTRANDO_LOTE("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.registrando-lote", 2),
    LOG_VERIFICACION_LOTE("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.verificacion-lote", 4),
    LOG_LOTE_REGISTRADO("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.lote-registrado", 3),
    LOG_LOTE_GUARDADO("evaluaciones.infraestructura.evaluacioncuantitativaasesor.log.lote-guardado", 1),
    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.consultando", 1),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacioncuantitativaasesor.log.consulta-completada", 1);

    private final String clave;
    private final int parametros;

    EvaluacionCuantitativaAsesorKey(String clave, int parametros) {
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
