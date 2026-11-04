package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RemoverItemCualitativoJuradoCommand;

import java.util.UUID;

public final class RemoverItemCualitativoJuradoRequestMapper {

    private RemoverItemCualitativoJuradoRequestMapper() {}

    public static RemoverItemCualitativoJuradoCommand toCommand(UUID itemId) {
        return RemoverItemCualitativoJuradoCommand.crear(itemId);
    }
}
