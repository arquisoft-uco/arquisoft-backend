package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.entity.PlanEstimadoJpaEntity;

public final class PlanEstimadoJpaMapper {

    private PlanEstimadoJpaMapper() {}

    public static PlanEstimadoJpaEntity toJpaEntity(PlanEstimadoEntity entity) {
        return PlanEstimadoJpaEntity.builder()
                .id(entity.id())
                .tareaId(entity.tarea())
                .tiempoEstimadoDias(entity.tiempoEstimadoDias())
                .descripcionEstimado(entity.descripcionEstimado().isEmpty() ? null : entity.descripcionEstimado())
                .build();
    }
}
