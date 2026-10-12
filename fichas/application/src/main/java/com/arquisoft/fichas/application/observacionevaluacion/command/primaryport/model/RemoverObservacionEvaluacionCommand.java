package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverObservacionEvaluacionCommand(UUID observacionEvaluacion, UUID representanteComite) {

    public static RemoverObservacionEvaluacionCommand crear(UUID observacionEvaluacion, UUID representanteComite) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(observacionEvaluacion,
                FichasFields.ObservacionEvaluacion.OBSERVACION_EVALUACION,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_REQUERIDA, result);
        ValidatorObjeto.noNulo(representanteComite,
                FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE,
                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverObservacionEvaluacionCommand(observacionEvaluacion, representanteComite);
    }
}
