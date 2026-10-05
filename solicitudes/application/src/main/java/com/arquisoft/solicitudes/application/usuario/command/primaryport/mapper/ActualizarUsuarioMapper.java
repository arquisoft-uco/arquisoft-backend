package com.arquisoft.solicitudes.application.usuario.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.ActualizarUsuarioCommand;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public final class ActualizarUsuarioMapper {

    private ActualizarUsuarioMapper() {}

    public static UsuarioDomain toDomain(ActualizarUsuarioCommand command) {
        return UsuarioDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
