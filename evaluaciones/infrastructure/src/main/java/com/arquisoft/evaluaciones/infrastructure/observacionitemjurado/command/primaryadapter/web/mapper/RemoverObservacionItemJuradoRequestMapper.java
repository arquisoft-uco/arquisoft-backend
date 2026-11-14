package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RemoverObservacionItemJuradoCommand;

import java.util.UUID;

public final class RemoverObservacionItemJuradoRequestMapper {

    private RemoverObservacionItemJuradoRequestMapper() {}

    public static RemoverObservacionItemJuradoCommand toCommand(UUID observacionId) {
        return RemoverObservacionItemJuradoCommand.crear(observacionId);
    }
}
