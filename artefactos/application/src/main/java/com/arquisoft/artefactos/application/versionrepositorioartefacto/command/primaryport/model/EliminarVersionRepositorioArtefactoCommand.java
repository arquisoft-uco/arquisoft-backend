package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record EliminarVersionRepositorioArtefactoCommand(UUID version) {

    public static EliminarVersionRepositorioArtefactoCommand crear(UUID version) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(version,
                ArtefactosFields.VersionRepositorioArtefacto.ID,
                ArtefactosCodes.VersionRepositorioArtefacto.ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new EliminarVersionRepositorioArtefactoCommand(version);
    }
}
