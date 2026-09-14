package com.arquisoft.shared.message.key.evaluaciones;

import com.arquisoft.shared.message.ClaveMensaje;

public enum EvaluacionCuantitativaJuradoKey implements ClaveMensaje {

    LOG_CONSULTANDO("evaluaciones.aplicacion.evaluacioncuantitativajurado.log.consultando", 2),
    LOG_CONSULTA_COMPLETADA("evaluaciones.aplicacion.evaluacioncuantitativajurado.log.consulta-completada", 1),
    LOG_CAMBIANDO_PUNTAJE("evaluaciones.aplicacion.evaluacioncuantitativajurado.log.cambiando-puntaje", 2),
    LOG_VERIFICACION_CAMBIAR_PUNTAJE(
            "evaluaciones.aplicacion.evaluacioncuantitativajurado.log.verificacion-cambiar-puntaje", 3),
    LOG_PUNTAJE_CAMBIADO("evaluaciones.aplicacion.evaluacioncuantitativajurado.log.puntaje-cambiado", 1),
    LOG_GUARDADO("evaluaciones.infraestructura.evaluacioncuantitativajurado.log.guardado", 1),
    ERROR_NO_ENCONTRADA("evaluaciones.dominio.evaluacioncuantitativajurado.error.no-encontrada", 1),
    ERROR_NO_PERTENECE_JURADO(
            "evaluaciones.dominio.evaluacioncuantitativajurado.error.no-pertenece-jurado", 1),
    ERROR_EVALUACION_FINALIZADA(
            "evaluaciones.dominio.evaluacioncuantitativajurado.error.evaluacion-finalizada", 1),
    ERROR_PUNTAJE_EXCEDE_VALOR_ITEM(
            "evaluaciones.dominio.evaluacioncuantitativajurado.error.puntaje-excede-valor-item", 2);

    private final String clave;
    private final int parametros;

    EvaluacionCuantitativaJuradoKey(String clave, int parametros) {
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
