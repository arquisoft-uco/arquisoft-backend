package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesItemAsesorQuery(
        UUID evaluacionCuantitativaAsesor,
        UUID asesor,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarObservacionesItemAsesorQuery crear(
            UUID evaluacionCuantitativaAsesor, UUID asesor, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCuantitativaAsesor,
                EvaluacionesFields.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR_REQUERIDA, result);
        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.ObservacionItemAsesor.ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesItemAsesorQuery(evaluacionCuantitativaAsesor, asesor, criterio);
    }
}
