package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.entity.ArchivoVersionArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository.ArchivoVersionArtefactoJpaQueryEntity;

public final class ArchivoVersionArtefactoQueryMapper {

    private ArchivoVersionArtefactoQueryMapper() {}

    public static ArchivoVersionArtefactoEntity toEntity(ArchivoVersionArtefactoJpaQueryEntity jpa) {
        return new ArchivoVersionArtefactoEntity(
                jpa.getAsesorId(), jpa.getUriArchivo(), jpa.getVersion(), jpa.getNombreDocumento(),
                jpa.getNombreProyecto(), jpa.isProyectoFinalizado());
    }
}
