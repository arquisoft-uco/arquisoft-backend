package com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;

public final class AsesorJpaMapper {

    private AsesorJpaMapper() {}

    public static AsesorEntity toEntity(AsesorJpaEntity jpaEntity) {
        return new AsesorEntity(jpaEntity.getUsuarioId());
    }

    public static AsesorJpaEntity toJpaEntity(AsesorEntity entity) {
        return AsesorJpaEntity.builder()
                .usuarioId(entity.usuario())
                .build();
    }
}
