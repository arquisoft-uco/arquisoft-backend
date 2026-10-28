package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.ArtefactoAsesorJpaQueryEntity;

public final class ArtefactoAsesorQueryMapper {

    private ArtefactoAsesorQueryMapper() {}

    public static ArtefactoReadModel toReadModel(ArtefactoAsesorJpaQueryEntity entity) {
        return new ArtefactoReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getNombreProyecto(),
                entity.getVersionRepositorioArtefactoId(),
                entity.getNombreDocumento(),
                entity.getVersionDocumento(),
                entity.getNombreRepositorio());
    }
}
