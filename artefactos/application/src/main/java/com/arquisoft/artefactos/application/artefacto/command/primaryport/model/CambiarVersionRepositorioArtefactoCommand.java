package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record CambiarVersionRepositorioArtefactoCommand(UUID artefacto, UUID asesor, UUID versionRepositorioArtefacto) {

    public static CambiarVersionRepositorioArtefactoCommand crear(
            UUID artefacto, UUID asesor, String versionRepositorioArtefacto) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.Artefacto.ID,
                ArtefactosCodes.Artefacto.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result);
        ValidatorUUID.uuidValido(versionRepositorioArtefacto,
                ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarVersionRepositorioArtefactoCommand(
                artefacto, asesor, UtilUUID.generarUUIDDesdeTexto(versionRepositorioArtefacto));
    }
}
