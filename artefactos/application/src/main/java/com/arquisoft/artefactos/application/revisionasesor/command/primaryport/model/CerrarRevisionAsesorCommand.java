package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record CerrarRevisionAsesorCommand(
        UUID revisionAsesor,
        UUID asesor
) {

    public static CerrarRevisionAsesorCommand crear(UUID revisionAsesor, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.RevisionAsesor.ID,
                ArtefactosCodes.RevisionAsesor.ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.RevisionAsesor.ASESOR,
                ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CerrarRevisionAsesorCommand(revisionAsesor, asesor);
    }
}
