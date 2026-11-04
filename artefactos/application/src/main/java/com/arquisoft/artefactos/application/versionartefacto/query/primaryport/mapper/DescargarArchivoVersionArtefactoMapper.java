package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.DescargarArchivoVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;

public final class DescargarArchivoVersionArtefactoMapper {

    private DescargarArchivoVersionArtefactoMapper() {}

    public static DescargaVersionArtefactoCriteria toCriteria(DescargarArchivoVersionArtefactoQuery query) {
        return new DescargaVersionArtefactoCriteria(
                query.versionArtefacto(),
                query.actor(),
                query.roles().contains(RolConsultaVersionArtefacto.ASESOR),
                query.roles().contains(RolConsultaVersionArtefacto.ESTUDIANTE));
    }
}
