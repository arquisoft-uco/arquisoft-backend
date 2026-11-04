package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ModificarObservacionItemCommand;
import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.dto.ModificarObservacionItemRequestDTO;

import java.util.UUID;

public final class ModificarObservacionItemRequestMapper {

    private ModificarObservacionItemRequestMapper() {}

    public static ModificarObservacionItemCommand toCommand(
            ModificarObservacionItemRequestDTO dto, UUID observacionItemId, UUID asesorFichaId) {
        return ModificarObservacionItemCommand.crear(observacionItemId, dto.observacion(), asesorFichaId);
    }
}
