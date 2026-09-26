package com.arquisoft.usuarios.infrastructure.asesorficha.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;

import java.util.UUID;

public final class RemoverAsesorFichaRequestMapper {

    private RemoverAsesorFichaRequestMapper() {}

    public static RemoverAsesorFichaCommand toCommand(UUID usuarioId) {
        return RemoverAsesorFichaCommand.crear(usuarioId);
    }
}
