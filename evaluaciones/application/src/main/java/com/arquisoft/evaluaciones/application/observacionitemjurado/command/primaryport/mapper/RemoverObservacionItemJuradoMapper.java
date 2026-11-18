package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RemoverObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RemocionObservacionItemJuradoDomain;

public final class RemoverObservacionItemJuradoMapper {

    private RemoverObservacionItemJuradoMapper() {}

    public static RemocionObservacionItemJuradoDomain toDomain(RemoverObservacionItemJuradoCommand command) {
        return RemocionObservacionItemJuradoDomain.crear(command.observacionItemJurado());
    }
}
