package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery(
        UUID evaluacionJurado
) {

    public static ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery crear(UUID evaluacionJurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionJurado,
                EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery(evaluacionJurado);
    }
}
