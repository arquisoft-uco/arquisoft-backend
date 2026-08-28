package com.arquisoft.solicitudes.application.usuario.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    public static UsuarioEntity toEntity(UsuarioDomain domain) {
        return new UsuarioEntity(
                domain.getId(),
                domain.getIdentificador(),
                domain.getNombre(),
                domain.getEmail());
    }

    public static UsuarioDomain toDomain(UsuarioEntity entity) {
        return UsuarioDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email());
    }
}
