package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RemoverItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.RemocionItemCualitativoJuradoDomain;

public final class RemoverItemCualitativoJuradoMapper {

    private RemoverItemCualitativoJuradoMapper() {}

    public static RemocionItemCualitativoJuradoDomain toDomain(
            RemoverItemCualitativoJuradoCommand command) {
        return RemocionItemCualitativoJuradoDomain.crear(command.itemCualitativoJurado());
    }
}
