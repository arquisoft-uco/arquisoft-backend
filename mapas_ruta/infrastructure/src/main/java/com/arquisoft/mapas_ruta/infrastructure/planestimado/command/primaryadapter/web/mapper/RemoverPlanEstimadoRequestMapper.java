package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.RemoverPlanEstimadoCommand;

import java.util.UUID;

public final class RemoverPlanEstimadoRequestMapper {

    private RemoverPlanEstimadoRequestMapper() {}

    public static RemoverPlanEstimadoCommand toCommand(UUID planEstimado) {
        return RemoverPlanEstimadoCommand.crear(planEstimado);
    }
}
