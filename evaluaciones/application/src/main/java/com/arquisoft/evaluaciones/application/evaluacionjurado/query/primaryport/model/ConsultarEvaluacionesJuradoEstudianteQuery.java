package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesJuradoEstudianteQuery(
        UUID evaluacion,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarEvaluacionesJuradoEstudianteQuery crear(
            UUID evaluacion, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.EvaluacionJurado.EVALUACION,
                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesJuradoEstudianteQuery(evaluacion, criterio);
    }
}
