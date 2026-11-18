package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverObservacionItemJuradoCommand(UUID observacionItemJurado) {

    public static RemoverObservacionItemJuradoCommand crear(UUID observacionItemJurado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(observacionItemJurado,
                EvaluacionesFields.ObservacionItemJurado.ID,
                EvaluacionesCodes.ObservacionItemJurado.ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();
        return new RemoverObservacionItemJuradoCommand(observacionItemJurado);
    }
}
