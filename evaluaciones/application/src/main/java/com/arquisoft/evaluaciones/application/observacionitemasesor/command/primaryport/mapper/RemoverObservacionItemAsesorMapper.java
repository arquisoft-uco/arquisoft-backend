package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RemoverObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RemocionObservacionItemAsesorDomain;

public final class RemoverObservacionItemAsesorMapper {

    private RemoverObservacionItemAsesorMapper() {}

    public static RemocionObservacionItemAsesorDomain toDomain(RemoverObservacionItemAsesorCommand command) {
        return RemocionObservacionItemAsesorDomain.crear(command.observacion(), command.asesor());
    }
}
