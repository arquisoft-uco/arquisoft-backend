package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesItemAsesorEstudianteQuery(
        UUID evaluacionCuantitativaAsesor,
        UUID estudiante,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarObservacionesItemAsesorEstudianteQuery crear(
            UUID evaluacionCuantitativaAsesor, UUID estudiante, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCuantitativaAsesor,
                EvaluacionesFields.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR_REQUERIDA, result);
        ValidatorObjeto.noNulo(estudiante,
                EvaluacionesFields.ObservacionItemAsesor.ESTUDIANTE,
                EvaluacionesCodes.ObservacionItemAsesor.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesItemAsesorEstudianteQuery(evaluacionCuantitativaAsesor, estudiante, criterio);
    }
}
