package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RegistrarRevisionAsesorCommand(
        UUID versionArtefacto,
        UUID asesor
) {

    public static RegistrarRevisionAsesorCommand crear(UUID versionArtefacto, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(versionArtefacto,
                ArtefactosFields.RevisionAsesor.VERSION_ARTEFACTO,
                ArtefactosCodes.RevisionAsesor.VERSION_ARTEFACTO_REQUERIDA, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.RevisionAsesor.ASESOR,
                ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarRevisionAsesorCommand(versionArtefacto, asesor);
    }
}
