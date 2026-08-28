package com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;

public final class UsuarioJpaMapper {

    private UsuarioJpaMapper() {}

    public static UsuarioEntity toEntity(UsuarioJpaEntity jpaEntity) {
        return new UsuarioEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail());
    }

    public static UsuarioJpaEntity toJpaEntity(UsuarioEntity entity) {
        return UsuarioJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .build();
    }
}
