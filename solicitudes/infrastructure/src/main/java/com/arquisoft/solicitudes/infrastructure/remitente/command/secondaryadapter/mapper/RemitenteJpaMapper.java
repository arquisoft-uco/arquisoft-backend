package com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;

import java.util.UUID;

public final class RemitenteJpaMapper {

    private RemitenteJpaMapper() {}

    public static RemitenteEntity toEntity(RemitenteJpaEntity jpaEntity) {
        return new RemitenteEntity(jpaEntity.getId(), jpaEntity.getUsuarioId());
    }

    public static RemitenteJpaEntity toJpaEntity(RemitenteEntity entity) {
        return RemitenteJpaEntity.builder()
                .id(entity.id())
                .usuarioId(entity.usuario())
                .build();
    }

    public static RemitenteJpaEntity toReferencia(UUID remitente) {
        return RemitenteJpaEntity.builder().id(remitente).build();
    }
}
