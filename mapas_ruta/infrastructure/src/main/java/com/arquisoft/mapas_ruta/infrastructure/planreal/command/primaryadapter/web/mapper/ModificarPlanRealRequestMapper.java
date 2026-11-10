package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.ModificarPlanRealCommand;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.dto.ModificarPlanRealRequestDTO;

import java.util.UUID;

public final class ModificarPlanRealRequestMapper {

    private ModificarPlanRealRequestMapper() {}

    public static ModificarPlanRealCommand toCommand(ModificarPlanRealRequestDTO dto, UUID planReal) {
        return ModificarPlanRealCommand.crear(planReal, dto.tiempoRealDias(), dto.descripcionReal());
    }
}
