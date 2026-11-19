package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.CerrarObservacionItemCommand;
import com.arquisoft.fichas.domain.observacionitem.CierreObservacionItemDomain;

public final class CerrarObservacionItemMapper {

    private CerrarObservacionItemMapper() {}

    public static CierreObservacionItemDomain toDomain(CerrarObservacionItemCommand command) {
        return CierreObservacionItemDomain.crear(command.observacionItem(), command.asesorFicha());
    }
}
