package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionAsesorRealizadaQuery(UUID evaluacionAsesor, UUID asesor) {

    public static ConsultarEvaluacionAsesorRealizadaQuery crear(UUID evaluacionAsesor, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionAsesor.EVALUACION_ASESOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionAsesorRealizadaQuery(evaluacionAsesor, asesor);
    }
}
