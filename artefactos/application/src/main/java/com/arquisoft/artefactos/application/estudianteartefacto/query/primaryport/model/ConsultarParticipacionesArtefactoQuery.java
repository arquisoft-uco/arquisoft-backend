package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarParticipacionesArtefactoQuery(UUID estudiante) {

    public static ConsultarParticipacionesArtefactoQuery crear(UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTE,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarParticipacionesArtefactoQuery(estudiante);
    }
}
