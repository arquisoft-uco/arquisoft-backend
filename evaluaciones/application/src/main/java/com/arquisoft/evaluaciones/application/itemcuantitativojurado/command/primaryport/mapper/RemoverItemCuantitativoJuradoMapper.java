package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RemoverItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.RemocionItemCuantitativoJuradoDomain;

public final class RemoverItemCuantitativoJuradoMapper {

    private RemoverItemCuantitativoJuradoMapper() {}

    public static RemocionItemCuantitativoJuradoDomain toDomain(
            RemoverItemCuantitativoJuradoCommand command) {
        return RemocionItemCuantitativoJuradoDomain.crear(command.itemCuantitativoJurado());
    }
}
