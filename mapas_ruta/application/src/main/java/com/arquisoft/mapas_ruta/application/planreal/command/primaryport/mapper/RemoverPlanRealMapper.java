package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.RemoverPlanRealCommand;
import com.arquisoft.mapas_ruta.domain.planreal.RemocionPlanRealDomain;

public final class RemoverPlanRealMapper {

    private RemoverPlanRealMapper() {}

    public static RemocionPlanRealDomain toDomain(RemoverPlanRealCommand command) {
        return RemocionPlanRealDomain.crear(command.planReal());
    }
}
