package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarRevisionesAsesorAsesorQuery(
        UUID asesor,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarRevisionesAsesorAsesorQuery crear(
            UUID asesor, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.RevisionAsesor.ASESOR,
                ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarRevisionesAsesorAsesorQuery(asesor, criterio);
    }
}
