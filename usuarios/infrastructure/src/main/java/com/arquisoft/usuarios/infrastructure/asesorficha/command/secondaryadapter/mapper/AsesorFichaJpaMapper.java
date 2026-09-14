package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;

public final class AsesorFichaJpaMapper {

    private AsesorFichaJpaMapper() {}

    public static AsesorFichaEntity toEntity(AsesorFichaJpaEntity jpaEntity) {
        return new AsesorFichaEntity(jpaEntity.getUsuarioId());
    }

    public static AsesorFichaJpaEntity toJpaEntity(AsesorFichaEntity entity) {
        return AsesorFichaJpaEntity.builder()
                .usuarioId(entity.usuario())
                .build();
    }
}
