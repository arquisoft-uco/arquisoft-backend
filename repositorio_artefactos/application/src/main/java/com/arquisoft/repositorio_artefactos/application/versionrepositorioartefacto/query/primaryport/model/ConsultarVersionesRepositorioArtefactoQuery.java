package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarVersionesRepositorioArtefactoQuery(UUID repositorioArtefacto) {

    public static ConsultarVersionesRepositorioArtefactoQuery crear(UUID repositorioArtefacto) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                repositorioArtefacto,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_REQUERIDO,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();

        return new ConsultarVersionesRepositorioArtefactoQuery(repositorioArtefacto);
    }
}
