package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.AgregarObservacionItemCommand;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.ObservacionItemDomain;

public final class AgregarObservacionItemMapper {

    private AgregarObservacionItemMapper() {}

    public static AgregacionObservacionItemDomain toDomain(AgregarObservacionItemCommand command) {
        var observacionItem = ObservacionItemDomain.crear(command.revisionItem(), command.observacion());
        return AgregacionObservacionItemDomain.crear(observacionItem, command.asesorFicha());
    }
}
