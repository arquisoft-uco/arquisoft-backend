package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record EliminarVersionRepositorioArtefactoCommand(UUID version) {

    public static EliminarVersionRepositorioArtefactoCommand crear(String version) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(version,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.VERSION,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.VERSION_REQUERIDA, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new EliminarVersionRepositorioArtefactoCommand(UtilUUID.generarUUIDDesdeTexto(version));
    }
}
