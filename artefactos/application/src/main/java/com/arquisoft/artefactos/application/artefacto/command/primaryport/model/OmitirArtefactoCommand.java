package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record OmitirArtefactoCommand(UUID artefacto, UUID asesor) {

    public static OmitirArtefactoCommand crear(UUID artefacto, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.Artefacto.ID,
                ArtefactosCodes.Artefacto.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new OmitirArtefactoCommand(artefacto, asesor);
    }
}
