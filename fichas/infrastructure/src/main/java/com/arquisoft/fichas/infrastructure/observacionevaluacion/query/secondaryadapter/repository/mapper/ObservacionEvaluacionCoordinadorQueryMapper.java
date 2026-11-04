package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.ObservacionEvaluacionCoordinadorJpaQueryEntity;

public final class ObservacionEvaluacionCoordinadorQueryMapper {

    private ObservacionEvaluacionCoordinadorQueryMapper() {}

    public static ObservacionEvaluacionReadModel toReadModel(ObservacionEvaluacionCoordinadorJpaQueryEntity entity) {
        return new ObservacionEvaluacionReadModel(
                entity.getId(),
                entity.getEvaluacionFichaPerfilId(),
                entity.getObservacion());
    }
}
