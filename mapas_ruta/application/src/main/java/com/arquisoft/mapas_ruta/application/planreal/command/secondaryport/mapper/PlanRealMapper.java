package com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity.PlanRealEntity;
import com.arquisoft.mapas_ruta.domain.planreal.PlanRealDomain;

public final class PlanRealMapper {

    private PlanRealMapper() {}

    public static PlanRealEntity toEntity(PlanRealDomain domain) {
        return new PlanRealEntity(
                domain.getId(),
                domain.getTarea(),
                domain.getTiempoRealDias(),
                domain.getDescripcionReal());
    }
}
