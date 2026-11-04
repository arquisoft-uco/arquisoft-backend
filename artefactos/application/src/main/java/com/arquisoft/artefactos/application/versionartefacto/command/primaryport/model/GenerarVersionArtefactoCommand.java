package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record GenerarVersionArtefactoCommand(UUID carga, UUID artefacto, UUID solicitante) {

    public static GenerarVersionArtefactoCommand crear(String carga, UUID artefacto, UUID solicitante) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(carga,
                ArtefactosFields.VersionArtefacto.CARGA,
                ArtefactosCodes.VersionArtefacto.CARGA_REQUERIDA, result);

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.ARTEFACTO_REQUERIDO, result);

        ValidatorObjeto.noNulo(solicitante,
                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new GenerarVersionArtefactoCommand(UtilUUID.generarUUIDDesdeTexto(carga), artefacto, solicitante);
    }
}
