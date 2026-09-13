package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;

public final class UsuarioJpaMapper {

    private UsuarioJpaMapper() {}

    public static UsuarioJpaEntity toJpaEntity(UsuarioEntity entity) {
        return UsuarioJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .contacto(entity.contacto())
                .estadoId(entity.estado())
                .build();
    }
}
