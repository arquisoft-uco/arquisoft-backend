package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RemoverItemCuantitativoAsesorCommand;

import java.util.UUID;

public final class RemoverItemCuantitativoAsesorRequestMapper {

    private RemoverItemCuantitativoAsesorRequestMapper() {}

    public static RemoverItemCuantitativoAsesorCommand toCommand(UUID itemId) {
        return RemoverItemCuantitativoAsesorCommand.crear(itemId);
    }
}
