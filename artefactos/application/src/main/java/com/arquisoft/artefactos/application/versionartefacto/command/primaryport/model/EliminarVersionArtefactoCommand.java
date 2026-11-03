package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record EliminarVersionArtefactoCommand(UUID versionArtefacto, UUID solicitante) {

    public static EliminarVersionArtefactoCommand crear(UUID versionArtefacto, UUID solicitante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(versionArtefacto,
                ArtefactosFields.VersionArtefacto.VERSION_ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.VERSION_ARTEFACTO_REQUERIDA, result);
        ValidatorObjeto.noNulo(solicitante,
                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new EliminarVersionArtefactoCommand(versionArtefacto, solicitante);
    }
}
