package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RegistrarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.dto.RegistrarObservacionItemAsesorRequestDTO;

import java.util.UUID;

public final class RegistrarObservacionItemAsesorRequestMapper {

    private RegistrarObservacionItemAsesorRequestMapper() {}

    public static RegistrarObservacionItemAsesorCommand toCommand(
            RegistrarObservacionItemAsesorRequestDTO dto, UUID evaluacionCuantitativaAsesor, UUID asesor) {
        return RegistrarObservacionItemAsesorCommand.crear(evaluacionCuantitativaAsesor, asesor, dto.descripcion());
    }
}
