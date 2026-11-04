package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record CambiarAsesorArtefactoCommand(UUID artefacto, UUID asesor) {

    public static CambiarAsesorArtefactoCommand crear(UUID artefacto, String asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.Artefacto.ID,
                ArtefactosCodes.Artefacto.ID_REQUERIDO, result);
        ValidatorUUID.uuidValido(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarAsesorArtefactoCommand(artefacto, UtilUUID.generarUUIDDesdeTexto(asesor));
    }
}
