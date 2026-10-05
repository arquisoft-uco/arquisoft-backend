package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;

public final class ObservacionEvaluacionJpaMapper {

    private ObservacionEvaluacionJpaMapper() {}

    public static ObservacionEvaluacionEntity toEntity(ObservacionEvaluacionJpaEntity jpaEntity) {
        return new ObservacionEvaluacionEntity(
                jpaEntity.getId(),
                jpaEntity.getEvaluacionFichaPerfilId(),
                jpaEntity.getObservacion());
    }

    public static ObservacionEvaluacionJpaEntity toJpaEntity(ObservacionEvaluacionEntity entity) {
        return ObservacionEvaluacionJpaEntity.builder()
                .id(entity.id())
                .evaluacionFichaPerfilId(entity.evaluacionFichaPerfil())
                .observacion(entity.observacion())
                .build();
    }
}
