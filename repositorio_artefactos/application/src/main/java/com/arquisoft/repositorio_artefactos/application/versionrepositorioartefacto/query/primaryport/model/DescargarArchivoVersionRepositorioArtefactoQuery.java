package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record DescargarArchivoVersionRepositorioArtefactoQuery(UUID version) {

    public static DescargarArchivoVersionRepositorioArtefactoQuery crear(UUID version) {
        var resultado = new ValidationResult();

        ValidatorObjeto.noNulo(
                version,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.VERSION,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.VERSION_REQUERIDA,
                resultado);

        resultado.lanzarSiTieneErroresDeEntrada();

        return new DescargarArchivoVersionRepositorioArtefactoQuery(version);
    }
}
