package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.ModificarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.dto.ModificarObservacionItemAsesorRequestDTO;

import java.util.UUID;

public final class ModificarObservacionItemAsesorRequestMapper {

    private ModificarObservacionItemAsesorRequestMapper() {}

    public static ModificarObservacionItemAsesorCommand toCommand(
            ModificarObservacionItemAsesorRequestDTO dto, UUID observacion, UUID asesor) {
        return ModificarObservacionItemAsesorCommand.crear(observacion, asesor, dto.descripcion());
    }
}
