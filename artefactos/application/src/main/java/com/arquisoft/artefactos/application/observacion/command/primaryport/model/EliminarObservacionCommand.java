package com.arquisoft.artefactos.application.observacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record EliminarObservacionCommand(
        UUID observacion,
        UUID asesor
) {

    public static EliminarObservacionCommand crear(UUID observacion, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(observacion,
                ArtefactosFields.Observacion.ID,
                ArtefactosCodes.Observacion.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Observacion.ASESOR,
                ArtefactosCodes.Observacion.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new EliminarObservacionCommand(observacion, asesor);
    }
}
