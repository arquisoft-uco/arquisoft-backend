package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ReabrirObservacionItemCommand;
import com.arquisoft.fichas.domain.observacionitem.ReaperturaObservacionItemDomain;

public final class ReabrirObservacionItemMapper {

    private ReabrirObservacionItemMapper() {}

    public static ReaperturaObservacionItemDomain toDomain(ReabrirObservacionItemCommand command) {
        return ReaperturaObservacionItemDomain.crear(command.observacionItem(), command.asesorFicha());
    }
}
