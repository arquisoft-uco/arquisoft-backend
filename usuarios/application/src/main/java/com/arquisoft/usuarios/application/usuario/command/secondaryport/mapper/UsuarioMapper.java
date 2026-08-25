package com.arquisoft.usuarios.application.usuario.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    public static UsuarioEntity toEntity(UsuarioDomain aggregate) {
        return new UsuarioEntity(
                aggregate.getId(),
                aggregate.getEmail(),
                aggregate.getRol().getCodigo());
    }

    public static UsuarioDomain toDomain(UsuarioEntity entity) {
        return UsuarioDomain.reconstruir(
                entity.id(),
                entity.email(),
                UsuarioRole.desdeCodigo(entity.rol()));
    }
}
