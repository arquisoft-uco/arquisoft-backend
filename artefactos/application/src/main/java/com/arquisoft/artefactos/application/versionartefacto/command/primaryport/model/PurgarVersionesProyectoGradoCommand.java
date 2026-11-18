package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record PurgarVersionesProyectoGradoCommand(UUID proyectoGrado) {

    public static PurgarVersionesProyectoGradoCommand crear(String proyectoGrado) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new PurgarVersionesProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado));
    }
}
