package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RemoverItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.RemocionItemCualitativoAsesorDomain;

public final class RemoverItemCualitativoAsesorMapper {

    private RemoverItemCualitativoAsesorMapper() {}

    public static RemocionItemCualitativoAsesorDomain toDomain(
            RemoverItemCualitativoAsesorCommand command) {
        return RemocionItemCualitativoAsesorDomain.crear(command.itemCualitativoAsesor());
    }
}
