package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RemoverItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.RemocionItemCuantitativoAsesorDomain;

public final class RemoverItemCuantitativoAsesorMapper {

    private RemoverItemCuantitativoAsesorMapper() {}

    public static RemocionItemCuantitativoAsesorDomain toDomain(
            RemoverItemCuantitativoAsesorCommand command) {
        return RemocionItemCuantitativoAsesorDomain.crear(command.itemCuantitativoAsesor());
    }
}
