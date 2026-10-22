package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEvaluacionesAsignadasJuradoQuery(UUID jurado) {

    public static ConsultarEvaluacionesAsignadasJuradoQuery crear(UUID jurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.EvaluacionJurado.JURADO,
                EvaluacionesCodes.EvaluacionJurado.JURADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEvaluacionesAsignadasJuradoQuery(jurado);
    }
}
