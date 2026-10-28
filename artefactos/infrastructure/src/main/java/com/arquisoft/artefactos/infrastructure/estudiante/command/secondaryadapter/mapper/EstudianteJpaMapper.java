package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;

public final class EstudianteJpaMapper {

    private EstudianteJpaMapper() {}

    public static EstudianteEntity toEntity(EstudianteJpaEntity jpaEntity) {
        return new EstudianteEntity(jpaEntity.getId(), jpaEntity.getNombre(), jpaEntity.getEmail());
    }
}
