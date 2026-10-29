package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.RemoverPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;

public final class RemoverPlanEstimadoMapper {

    private RemoverPlanEstimadoMapper() {}

    public static RemocionPlanEstimadoDomain toDomain(RemoverPlanEstimadoCommand command) {
        return RemocionPlanEstimadoDomain.crear(command.planEstimado());
    }
}
