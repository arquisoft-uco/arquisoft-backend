package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.ParticipacionArtefactoJpaQueryEntity;

public final class ParticipacionArtefactoQueryMapper {

    private ParticipacionArtefactoQueryMapper() {}

    public static ParticipacionArtefactoReadModel toReadModel(ParticipacionArtefactoJpaQueryEntity entity) {
        return new ParticipacionArtefactoReadModel(
                entity.getArtefactoId(),
                entity.getProyectoGradoId(),
                entity.getNombreProyecto(),
                entity.getVersionRepositorioArtefactoId(),
                entity.getNombreDocumento(),
                entity.getVersionDocumento(),
                entity.getNombreRepositorio());
    }
}
