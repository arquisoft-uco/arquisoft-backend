package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesEstudianteQuery(
        UUID estudiante,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarEvaluacionesEstudianteQuery crear(UUID estudiante, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                EvaluacionesFields.Evaluacion.ESTUDIANTE,
                EvaluacionesCodes.Evaluacion.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesEstudianteQuery(estudiante, criterio);
    }
}
