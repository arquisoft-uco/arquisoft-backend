package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery(
        UUID evaluacionAsesor,
        UUID estudiante
) {

    public static ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery crear(
            UUID evaluacionAsesor, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                EvaluacionesFields.EvaluacionCuantitativaAsesor.ESTUDIANTE,
                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery(evaluacionAsesor, estudiante);
    }
}
