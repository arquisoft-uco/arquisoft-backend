package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesCualitativasJuradoRealizadasQuery(
        UUID evaluacionJurado,
        UUID jurado
) {

    public static ConsultarEvaluacionesCualitativasJuradoRealizadasQuery crear(UUID evaluacionJurado, UUID jurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCualitativasJuradoRealizadasQuery(evaluacionJurado, jurado);
    }
}
