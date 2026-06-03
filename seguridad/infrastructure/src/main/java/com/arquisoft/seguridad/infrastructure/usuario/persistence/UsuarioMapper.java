package com.arquisoft.seguridad.infrastructure.usuario.persistence;

import com.arquisoft.seguridad.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.seguridad.domain.usuario.model.UsuarioRole;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    public static UsuarioAggregate toDomain(UsuarioJpaEntity entity) {
        return UsuarioAggregate.rebuild(
                entity.getId(),
                entity.getEmail(),
                UsuarioRole.fromCode(entity.getRol())
        );
    }

    public static UsuarioJpaEntity toEntity(UsuarioAggregate aggregate) {
        return UsuarioJpaEntity.builder()
                .id(aggregate.getId())
                .email(aggregate.getEmail())
                .rol(aggregate.getRol().getCode())
                .build();
    }
}
