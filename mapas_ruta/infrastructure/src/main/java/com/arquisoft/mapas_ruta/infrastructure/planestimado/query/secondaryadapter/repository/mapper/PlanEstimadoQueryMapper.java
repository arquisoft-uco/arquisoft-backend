package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.PlanEstimadoJpaQueryEntity;

public final class PlanEstimadoQueryMapper {

    private PlanEstimadoQueryMapper() {}

    public static PlanEstimadoReadModel toReadModel(PlanEstimadoJpaQueryEntity entity) {
        return new PlanEstimadoReadModel(
                entity.getId(),
                entity.getTareaId(),
                entity.getTiempoEstimadoDias(),
                entity.getDescripcionEstimado());
    }
}
