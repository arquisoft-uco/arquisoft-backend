package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarRevisionesAsesorEstudianteQuery(
        UUID estudiante,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarRevisionesAsesorEstudianteQuery crear(
            UUID estudiante, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.RevisionAsesor.ESTUDIANTE,
                ArtefactosCodes.RevisionAsesor.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarRevisionesAsesorEstudianteQuery(estudiante, criterio);
    }
}
