package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.PlanEstimadoProyectoJpaQueryEntity;

public final class PlanEstimadoProyectoQueryMapper {

    private PlanEstimadoProyectoQueryMapper() {}

    public static PlanEstimadoProyectoReadModel toReadModel(PlanEstimadoProyectoJpaQueryEntity entity) {
        return new PlanEstimadoProyectoReadModel(
                entity.getId(),
                entity.getTareaId(),
                entity.getDescripcionTarea(),
                entity.getMapaRutaId(),
                entity.getTiempoEstimadoDias(),
                entity.getDescripcionEstimado());
    }
}
