package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.ArtefactoEstudianteJpaQueryEntity;

public final class ArtefactoEstudianteQueryMapper {

    private ArtefactoEstudianteQueryMapper() {}

    public static ArtefactoEstudianteReadModel toReadModel(ArtefactoEstudianteJpaQueryEntity entity) {
        return new ArtefactoEstudianteReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getNombreProyecto(),
                entity.getVersionRepositorioArtefactoId(),
                entity.getNombreDocumento(),
                entity.getVersionDocumento(),
                entity.getNombreRepositorio(),
                entity.getAsesorId(),
                entity.getNombreAsesor(),
                entity.isOmitido());
    }
}
