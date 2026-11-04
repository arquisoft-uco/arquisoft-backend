package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarVersionesArtefactoAsesorQuery(
        UUID asesor,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarVersionesArtefactoAsesorQuery crear(
            UUID asesor, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.VersionArtefacto.ASESOR,
                ArtefactosCodes.VersionArtefacto.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarVersionesArtefactoAsesorQuery(asesor, criterio);
    }
}
