package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RemoverObservacionItemAsesorCommand;

import java.util.UUID;

public final class RemoverObservacionItemAsesorRequestMapper {

    private RemoverObservacionItemAsesorRequestMapper() {}

    public static RemoverObservacionItemAsesorCommand toCommand(UUID observacion, UUID asesor) {
        return RemoverObservacionItemAsesorCommand.crear(observacion, asesor);
    }
}
