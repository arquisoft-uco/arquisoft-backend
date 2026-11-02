package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionAsesorQuery(UUID evaluacion) {

    public static ConsultarEvaluacionAsesorQuery crear(UUID evaluacion) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.EvaluacionAsesor.EVALUACION,
                EvaluacionesCodes.EvaluacionAsesor.EVALUACION_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionAsesorQuery(evaluacion);
    }
}
