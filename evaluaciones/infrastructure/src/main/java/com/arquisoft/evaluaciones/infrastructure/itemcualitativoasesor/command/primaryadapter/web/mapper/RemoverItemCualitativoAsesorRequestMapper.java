package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RemoverItemCualitativoAsesorCommand;

import java.util.UUID;

public final class RemoverItemCualitativoAsesorRequestMapper {

    private RemoverItemCualitativoAsesorRequestMapper() {}

    public static RemoverItemCualitativoAsesorCommand toCommand(UUID itemId) {
        return RemoverItemCualitativoAsesorCommand.crear(itemId);
    }
}
