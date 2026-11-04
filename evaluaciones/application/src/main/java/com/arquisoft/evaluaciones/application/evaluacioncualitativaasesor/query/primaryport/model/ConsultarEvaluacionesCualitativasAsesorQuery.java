package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesCualitativasAsesorQuery(
        UUID evaluacionAsesor,
        UUID asesor
) {

    public static ConsultarEvaluacionesCualitativasAsesorQuery crear(UUID evaluacionAsesor, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCualitativasAsesorQuery(evaluacionAsesor, asesor);
    }
}
