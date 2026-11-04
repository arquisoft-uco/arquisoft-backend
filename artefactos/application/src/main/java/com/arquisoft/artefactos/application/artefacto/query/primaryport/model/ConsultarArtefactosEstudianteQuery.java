package com.arquisoft.artefactos.application.artefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarArtefactosEstudianteQuery(
        UUID estudiante,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarArtefactosEstudianteQuery crear(UUID estudiante, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.Artefacto.ESTUDIANTE,
                ArtefactosCodes.Artefacto.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarArtefactosEstudianteQuery(estudiante, criterio);
    }
}
