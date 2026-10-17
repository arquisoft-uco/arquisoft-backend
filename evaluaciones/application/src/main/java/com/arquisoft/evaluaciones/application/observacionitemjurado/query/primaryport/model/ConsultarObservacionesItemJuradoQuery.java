package com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarObservacionesItemJuradoQuery(
        UUID evaluacionCuantitativaJurado,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarObservacionesItemJuradoQuery crear(
            UUID evaluacionCuantitativaJurado, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(evaluacionCuantitativaJurado,
                EvaluacionesFields.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO,
                EvaluacionesCodes.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarObservacionesItemJuradoQuery(evaluacionCuantitativaJurado, criterio);
    }
}
