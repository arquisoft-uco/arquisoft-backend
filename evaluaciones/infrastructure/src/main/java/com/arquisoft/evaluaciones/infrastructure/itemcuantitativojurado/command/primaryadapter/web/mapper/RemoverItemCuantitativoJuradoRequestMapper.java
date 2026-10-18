package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RemoverItemCuantitativoJuradoCommand;

import java.util.UUID;

public final class RemoverItemCuantitativoJuradoRequestMapper {

    private RemoverItemCuantitativoJuradoRequestMapper() {}

    public static RemoverItemCuantitativoJuradoCommand toCommand(UUID itemId) {
        return RemoverItemCuantitativoJuradoCommand.crear(itemId);
    }
}
