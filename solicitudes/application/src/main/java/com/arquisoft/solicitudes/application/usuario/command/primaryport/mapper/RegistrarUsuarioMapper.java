package com.arquisoft.solicitudes.application.usuario.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public final class RegistrarUsuarioMapper {

    private RegistrarUsuarioMapper() {}

    public static UsuarioDomain toDomain(RegistrarUsuarioCommand command) {
        return UsuarioDomain.crear(
                command.usuarioId(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
