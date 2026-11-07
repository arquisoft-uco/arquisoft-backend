package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesCualitativasAsesorEstudianteQuery(
        UUID evaluacionAsesor,
        UUID estudiante
) {

    public static ConsultarEvaluacionesCualitativasAsesorEstudianteQuery crear(UUID evaluacionAsesor, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                EvaluacionesFields.EvaluacionCualitativaAsesor.ESTUDIANTE,
                EvaluacionesCodes.EvaluacionCualitativaAsesor.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCualitativasAsesorEstudianteQuery(evaluacionAsesor, estudiante);
    }
}
