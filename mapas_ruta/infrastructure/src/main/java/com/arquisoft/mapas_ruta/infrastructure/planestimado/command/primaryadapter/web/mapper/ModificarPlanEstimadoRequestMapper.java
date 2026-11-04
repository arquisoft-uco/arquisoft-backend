package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.ModificarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.ModificarPlanEstimadoRequestDTO;

import java.util.UUID;

public final class ModificarPlanEstimadoRequestMapper {

    private ModificarPlanEstimadoRequestMapper() {}

    public static ModificarPlanEstimadoCommand toCommand(ModificarPlanEstimadoRequestDTO dto, UUID planEstimado) {
        return ModificarPlanEstimadoCommand.crear(planEstimado, dto.descripcionEstimado());
    }
}
