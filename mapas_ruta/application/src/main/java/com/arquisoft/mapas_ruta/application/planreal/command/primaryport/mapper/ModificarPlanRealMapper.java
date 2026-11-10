package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.ModificarPlanRealCommand;
import com.arquisoft.mapas_ruta.domain.planreal.ModificacionPlanRealDomain;

public final class ModificarPlanRealMapper {

    private ModificarPlanRealMapper() {}

    public static ModificacionPlanRealDomain toDomain(ModificarPlanRealCommand command) {
        return ModificacionPlanRealDomain.crear(
                command.planReal(), command.tiempoRealDias(), command.descripcionReal());
    }
}
