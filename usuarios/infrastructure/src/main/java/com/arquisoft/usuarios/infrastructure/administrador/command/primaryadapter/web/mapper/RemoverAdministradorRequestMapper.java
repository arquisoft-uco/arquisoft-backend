package com.arquisoft.usuarios.infrastructure.administrador.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.administrador.command.primaryport.model.RemoverAdministradorCommand;

import java.util.UUID;

public final class RemoverAdministradorRequestMapper {

    private RemoverAdministradorRequestMapper() {}

    public static RemoverAdministradorCommand toCommand(UUID usuarioId, UUID actorId) {
        return RemoverAdministradorCommand.crear(usuarioId, actorId);
    }
}
