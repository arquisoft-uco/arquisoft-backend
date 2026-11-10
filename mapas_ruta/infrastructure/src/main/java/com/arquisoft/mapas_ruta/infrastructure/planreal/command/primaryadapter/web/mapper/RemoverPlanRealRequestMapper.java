package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.RemoverPlanRealCommand;

import java.util.UUID;

public final class RemoverPlanRealRequestMapper {

    private RemoverPlanRealRequestMapper() {}

    public static RemoverPlanRealCommand toCommand(UUID planReal) {
        return RemoverPlanRealCommand.crear(planReal);
    }
}
