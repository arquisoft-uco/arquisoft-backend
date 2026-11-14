package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.AportarPlanRealCommand;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.dto.AportarPlanRealRequestDTO;

import java.util.UUID;

public final class AportarPlanRealRequestMapper {

    private AportarPlanRealRequestMapper() {}

    public static AportarPlanRealCommand toCommand(AportarPlanRealRequestDTO dto, UUID planReal) {
        return AportarPlanRealCommand.crear(planReal, dto.descripcionReal());
    }
}
