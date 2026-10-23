package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.EstudianteArtefactoJpaQueryEntity;

public final class EstudianteArtefactoQueryMapper {

    private EstudianteArtefactoQueryMapper() {}

    public static EstudianteArtefactoReadModel toReadModel(EstudianteArtefactoJpaQueryEntity entity) {
        return new EstudianteArtefactoReadModel(
                entity.getId(),
                entity.getArtefactoId(),
                entity.getEstudianteId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.isVigente());
    }
}
