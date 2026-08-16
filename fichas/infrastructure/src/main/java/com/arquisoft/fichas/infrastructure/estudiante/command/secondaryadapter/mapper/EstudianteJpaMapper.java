package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;

public final class EstudianteJpaMapper {

    private EstudianteJpaMapper() {}

    public static EstudianteEntity toEntity(EstudianteJpaEntity jpaEntity) {
        return new EstudianteEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail());
    }

    public static EstudianteJpaEntity toJpaEntity(EstudianteEntity entity) {
        return EstudianteJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .build();
    }
}
