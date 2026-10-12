package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.EliminarUsuarioCommand;

import java.util.UUID;

public final class EliminarUsuarioRequestMapper {

    private EliminarUsuarioRequestMapper() {}

    public static EliminarUsuarioCommand toCommand(UUID usuarioId) {
        return EliminarUsuarioCommand.crear(usuarioId);
    }
}
