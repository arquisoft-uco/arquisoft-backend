package com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ModificarObservacionItemCommand;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;

public final class ModificarObservacionItemMapper {

    private ModificarObservacionItemMapper() {}

    public static ModificacionObservacionItemDomain toDomain(ModificarObservacionItemCommand command) {
        return ModificacionObservacionItemDomain.crear(
                command.observacionItem(), command.observacion(), command.asesorFicha());
    }
}
