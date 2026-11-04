package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.RemoverObservacionItemCommand;
import com.arquisoft.fichas.domain.observacionitem.RemocionObservacionItemDomain;

public final class RemoverObservacionItemMapper {

    private RemoverObservacionItemMapper() {}

    public static RemocionObservacionItemDomain toDomain(RemoverObservacionItemCommand command) {
        return RemocionObservacionItemDomain.crear(command.observacionItem(), command.asesorFicha());
    }
}
