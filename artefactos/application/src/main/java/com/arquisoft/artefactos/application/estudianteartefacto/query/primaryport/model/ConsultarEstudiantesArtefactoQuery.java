package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstudiantesArtefactoQuery(UUID artefacto) {

    public static ConsultarEstudiantesArtefactoQuery crear(UUID artefacto) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstudiantesArtefactoQuery(artefacto);
    }
}
