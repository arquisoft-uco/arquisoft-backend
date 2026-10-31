package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;

public final class ConsultarVersionArtefactoMapper {

    private ConsultarVersionArtefactoMapper() {}

    public static VersionArtefactoDetalleCriteria toCriteria(ConsultarVersionArtefactoQuery query) {
        return new VersionArtefactoDetalleCriteria(
                query.versionArtefacto(),
                query.actor(),
                query.roles().contains(RolConsultaVersionArtefacto.ASESOR),
                query.roles().contains(RolConsultaVersionArtefacto.ESTUDIANTE));
    }
}
