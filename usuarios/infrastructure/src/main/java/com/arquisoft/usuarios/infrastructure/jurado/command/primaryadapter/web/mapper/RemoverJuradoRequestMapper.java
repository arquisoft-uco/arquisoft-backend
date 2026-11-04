package com.arquisoft.usuarios.infrastructure.jurado.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.jurado.command.primaryport.model.RemoverJuradoCommand;

import java.util.UUID;

public final class RemoverJuradoRequestMapper {

    private RemoverJuradoRequestMapper() {}

    public static RemoverJuradoCommand toCommand(UUID usuarioId) {
        return RemoverJuradoCommand.crear(usuarioId);
    }
}
