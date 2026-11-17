package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverObservacionItemAsesorCommand(UUID observacion, UUID asesor) {

    public static RemoverObservacionItemAsesorCommand crear(UUID observacion, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(observacion,
                EvaluacionesFields.ObservacionItemAsesor.ID,
                EvaluacionesCodes.ObservacionItemAsesor.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.ObservacionItemAsesor.ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverObservacionItemAsesorCommand(observacion, asesor);
    }
}
