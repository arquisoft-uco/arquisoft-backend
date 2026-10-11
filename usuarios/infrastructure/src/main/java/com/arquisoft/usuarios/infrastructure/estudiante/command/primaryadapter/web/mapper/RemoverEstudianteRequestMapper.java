package com.arquisoft.usuarios.infrastructure.estudiante.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;

import java.util.UUID;

public final class RemoverEstudianteRequestMapper {

    private RemoverEstudianteRequestMapper() {}

    public static RemoverEstudianteCommand toCommand(UUID usuarioId) {
        return RemoverEstudianteCommand.crear(usuarioId);
    }
}
