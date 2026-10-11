package com.arquisoft.usuarios.infrastructure.coordinador.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;

import java.util.UUID;

public final class RemoverCoordinadorRequestMapper {

    private RemoverCoordinadorRequestMapper() {}

    public static RemoverCoordinadorCommand toCommand(UUID usuarioId) {
        return RemoverCoordinadorCommand.crear(usuarioId);
    }
}
