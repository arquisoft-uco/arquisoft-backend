package com.arquisoft.usuarios.infrastructure.asesor.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.asesor.command.primaryport.model.RemoverAsesorCommand;

import java.util.UUID;

public final class RemoverAsesorRequestMapper {

    private RemoverAsesorRequestMapper() {}

    public static RemoverAsesorCommand toCommand(UUID usuarioId) {
        return RemoverAsesorCommand.crear(usuarioId);
    }
}
