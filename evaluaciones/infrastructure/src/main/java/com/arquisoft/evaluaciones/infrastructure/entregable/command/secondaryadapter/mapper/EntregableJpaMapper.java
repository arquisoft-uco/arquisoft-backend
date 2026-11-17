package com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.entity.EntregableEntity;
import com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.entity.EntregableJpaEntity;

public final class EntregableJpaMapper {

    private EntregableJpaMapper() {}

    public static EntregableJpaEntity toJpaEntity(EntregableEntity entity) {
        return EntregableJpaEntity.builder()
                .id(entity.id())
                .proyecto(entity.proyecto())
                .version(entity.version())
                .proyectoGrado(entity.proyectoGrado())
                .build();
    }
}
