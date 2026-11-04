package com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;

public final class PlanEstimadoMapper {

    private PlanEstimadoMapper() {}

    public static PlanEstimadoEntity toEntity(PlanEstimadoDomain domain) {
        return new PlanEstimadoEntity(
                domain.getId(),
                domain.getTarea(),
                domain.getTiempoEstimadoDias(),
                domain.getDescripcionEstimado());
    }
}
