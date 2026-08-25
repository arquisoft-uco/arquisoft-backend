package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;

public final class UsuarioJpaMapper {

    private UsuarioJpaMapper() {}

    public static UsuarioEntity toEntity(UsuarioJpaEntity jpaEntity) {
        return new UsuarioEntity(
                jpaEntity.getId(),
                jpaEntity.getEmail(),
                jpaEntity.getRol());
    }

    public static UsuarioJpaEntity toJpaEntity(UsuarioEntity entity) {
        return UsuarioJpaEntity.builder()
                .id(entity.id())
                .email(entity.email())
                .rol(entity.rol())
                .build();
    }
}
