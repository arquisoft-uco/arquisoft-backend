package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.AportarPlanRealCommand;
import com.arquisoft.mapas_ruta.domain.planreal.AportePlanRealDomain;

public final class AportarPlanRealMapper {

    private AportarPlanRealMapper() {}

    public static AportePlanRealDomain toDomain(AportarPlanRealCommand command) {
        return AportePlanRealDomain.crear(command.planReal(), command.descripcionReal());
    }
}
