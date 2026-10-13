package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.ModificarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.dto.ModificarObservacionItemJuradoRequestDTO;

import java.util.UUID;

public final class ModificarObservacionItemJuradoRequestMapper {

    private ModificarObservacionItemJuradoRequestMapper() {}

    public static ModificarObservacionItemJuradoCommand toCommand(
            ModificarObservacionItemJuradoRequestDTO dto, UUID observacionId) {
        return ModificarObservacionItemJuradoCommand.crear(observacionId, dto.descripcion());
    }
}
