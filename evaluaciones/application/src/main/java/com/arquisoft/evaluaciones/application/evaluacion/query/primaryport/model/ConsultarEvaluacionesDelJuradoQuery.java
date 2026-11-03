package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesDelJuradoQuery(
        UUID jurado,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarEvaluacionesDelJuradoQuery crear(UUID jurado, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.Evaluacion.JURADO,
                EvaluacionesCodes.Evaluacion.JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesDelJuradoQuery(jurado, criterio);
    }
}
