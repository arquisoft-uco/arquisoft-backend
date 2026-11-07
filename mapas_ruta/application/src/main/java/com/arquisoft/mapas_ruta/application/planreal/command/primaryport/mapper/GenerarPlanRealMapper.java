package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.GenerarPlanRealCommand;
import com.arquisoft.mapas_ruta.domain.planreal.GeneracionPlanRealDomain;

public final class GenerarPlanRealMapper {

    private GenerarPlanRealMapper() {}

    public static GeneracionPlanRealDomain toDomain(GenerarPlanRealCommand command) {
        return GeneracionPlanRealDomain.crear(command.tarea(), command.finalizadaEn());
    }
}
