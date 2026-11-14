package com.arquisoft.entregables.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record PurgarArtefactosProyectoGradoCommand(UUID proyectoGrado) {

    public static PurgarArtefactosProyectoGradoCommand crear(String proyectoGrado) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new PurgarArtefactosProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado));
    }
}
