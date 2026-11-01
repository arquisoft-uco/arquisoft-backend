package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverEstudianteArtefactoCommand(UUID artefacto, UUID asesor, UUID estudiante) {

    public static RemoverEstudianteArtefactoCommand crear(UUID artefacto, UUID asesor, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.EstudianteArtefacto.ASESOR,
                ArtefactosCodes.EstudianteArtefacto.ASESOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                ArtefactosFields.EstudianteArtefacto.ESTUDIANTE,
                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverEstudianteArtefactoCommand(artefacto, asesor, estudiante);
    }
}
