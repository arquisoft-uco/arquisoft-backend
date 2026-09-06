package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public final class CrearUsuarioMapper {

    private CrearUsuarioMapper() {}

    public static UsuarioDomain toDomain(CrearUsuarioCommand command) {
        return UsuarioDomain.crear(command.email(), command.rol());
    }
}
