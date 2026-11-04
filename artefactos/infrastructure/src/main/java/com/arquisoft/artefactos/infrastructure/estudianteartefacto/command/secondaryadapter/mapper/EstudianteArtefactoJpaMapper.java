package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;

public final class EstudianteArtefactoJpaMapper {

    private EstudianteArtefactoJpaMapper() {}

    public static EstudianteArtefactoJpaEntity toJpaEntity(EstudianteArtefactoEntity entity) {
        return EstudianteArtefactoJpaEntity.builder()
                .id(entity.id())
                .artefacto(entity.artefacto())
                .estudiante(entity.estudiante())
                .build();
    }

    public static EstudianteArtefactoEntity toEntity(EstudianteArtefactoJpaEntity jpaEntity) {
        return new EstudianteArtefactoEntity(
                jpaEntity.getId(), jpaEntity.getArtefacto(), jpaEntity.getEstudiante());
    }
}
