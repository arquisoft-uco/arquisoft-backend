package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;

public final class EstudianteJpaMapper {

    private EstudianteJpaMapper() {}

    public static EstudianteEntity toEntity(EstudianteJpaEntity jpaEntity) {
        return new EstudianteEntity(jpaEntity.getUsuarioId());
    }

    public static EstudianteJpaEntity toJpaEntity(EstudianteEntity entity) {
        return EstudianteJpaEntity.builder()
                .usuarioId(entity.usuario())
                .build();
    }
}
