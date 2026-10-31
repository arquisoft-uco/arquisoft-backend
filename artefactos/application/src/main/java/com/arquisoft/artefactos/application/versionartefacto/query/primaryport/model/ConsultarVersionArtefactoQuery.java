package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public record ConsultarVersionArtefactoQuery(
        UUID versionArtefacto,
        UUID actor,
        List<RolConsultaVersionArtefacto> roles
) {

    public static ConsultarVersionArtefactoQuery crear(
            UUID versionArtefacto, UUID actor, List<RolConsultaVersionArtefacto> roles) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(versionArtefacto,
                ArtefactosFields.VersionArtefacto.VERSION_ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.VERSION_ARTEFACTO_REQUERIDA, result);
        ValidatorObjeto.noNulo(actor,
                ArtefactosFields.VersionArtefacto.ACTOR,
                ArtefactosCodes.VersionArtefacto.ACTOR_REQUERIDO, result);
        ValidatorColeccion.noVacia(roles,
                ArtefactosFields.VersionArtefacto.ROLES,
                ArtefactosCodes.VersionArtefacto.ROLES_REQUERIDOS, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarVersionArtefactoQuery(versionArtefacto, actor, List.copyOf(roles));
    }
}
