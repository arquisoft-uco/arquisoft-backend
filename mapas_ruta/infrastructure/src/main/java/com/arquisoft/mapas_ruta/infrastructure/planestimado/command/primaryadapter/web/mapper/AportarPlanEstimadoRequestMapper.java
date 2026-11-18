package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AportarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.AportarPlanEstimadoRequestDTO;

import java.util.UUID;

public final class AportarPlanEstimadoRequestMapper {

    private AportarPlanEstimadoRequestMapper() {}

    public static AportarPlanEstimadoCommand toCommand(AportarPlanEstimadoRequestDTO dto, UUID planEstimado) {
        return AportarPlanEstimadoCommand.crear(planEstimado, dto.descripcionEstimado());
    }
}
