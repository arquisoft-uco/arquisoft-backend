package com.arquisoft.usuarios.infrastructure.representantecomite.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;

import java.util.UUID;

public final class RemoverRepresentanteComiteRequestMapper {

    private RemoverRepresentanteComiteRequestMapper() {}

    public static RemoverRepresentanteComiteCommand toCommand(UUID usuarioId) {
        return RemoverRepresentanteComiteCommand.crear(usuarioId);
    }
}
