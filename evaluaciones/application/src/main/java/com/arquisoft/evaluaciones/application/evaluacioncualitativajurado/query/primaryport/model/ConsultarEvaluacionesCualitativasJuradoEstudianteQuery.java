package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesCualitativasJuradoEstudianteQuery(
        UUID evaluacionJurado
) {

    public static ConsultarEvaluacionesCualitativasJuradoEstudianteQuery crear(UUID evaluacionJurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCualitativasJuradoEstudianteQuery(evaluacionJurado);
    }
}
