package com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;

public final class AsesorJpaMapper {

    private AsesorJpaMapper() {}

    public static AsesorEntity toEntity(AsesorJpaEntity jpaEntity) {
        return new AsesorEntity(jpaEntity.getId(), jpaEntity.getNombre(), jpaEntity.getEmail());
    }
}
