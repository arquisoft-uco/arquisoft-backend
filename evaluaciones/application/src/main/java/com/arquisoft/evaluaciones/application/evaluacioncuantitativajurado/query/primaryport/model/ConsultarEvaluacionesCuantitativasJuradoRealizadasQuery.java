package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery(
        UUID evaluacionJurado,
        UUID jurado
) {

    public static ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery crear(UUID evaluacionJurado, UUID jurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery(evaluacionJurado, jurado);
    }
}
