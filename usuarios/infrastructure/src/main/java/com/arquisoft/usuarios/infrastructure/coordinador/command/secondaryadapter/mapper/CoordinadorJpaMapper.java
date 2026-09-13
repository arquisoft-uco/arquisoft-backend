package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;

public final class CoordinadorJpaMapper {

    private CoordinadorJpaMapper() {}

    public static CoordinadorEntity toEntity(CoordinadorJpaEntity jpaEntity) {
        return new CoordinadorEntity(jpaEntity.getUsuarioId());
    }

    public static CoordinadorJpaEntity toJpaEntity(CoordinadorEntity entity) {
        return CoordinadorJpaEntity.builder()
                .usuarioId(entity.usuario())
                .build();
    }
}
