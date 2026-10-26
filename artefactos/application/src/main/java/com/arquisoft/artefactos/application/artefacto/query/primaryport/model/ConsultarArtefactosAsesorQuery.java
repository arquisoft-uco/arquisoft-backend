package com.arquisoft.artefactos.application.artefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarArtefactosAsesorQuery(
        UUID asesor,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarArtefactosAsesorQuery crear(UUID asesor, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarArtefactosAsesorQuery(asesor, criterio);
    }
}
