package com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;

import java.util.UUID;

public final class DestinatarioJpaMapper {

    private DestinatarioJpaMapper() {}

    public static DestinatarioEntity toEntity(DestinatarioJpaEntity jpaEntity) {
        return new DestinatarioEntity(jpaEntity.getId(), jpaEntity.getUsuarioId());
    }

    public static DestinatarioJpaEntity toJpaEntity(DestinatarioEntity entity) {
        return DestinatarioJpaEntity.builder()
                .id(entity.id())
                .usuarioId(entity.usuario())
                .build();
    }

    public static DestinatarioJpaEntity toReferencia(UUID destinatario) {
        return DestinatarioJpaEntity.builder().id(destinatario).build();
    }
}
